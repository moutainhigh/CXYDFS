/*
author:chxy
data:2020/5/4
description:全权处理网络通讯
*/
package network;

import master.Message;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/*
* 设计思路：
* masternetworkhandle作为一个消息中转站，它从网络接受消息，
* 放入消息队列，*manager作为消息的消费者，从队列中取走消息，并进行处理。
* 处理完成之后，又作为消息的生产者，将新的消息投入队列。masternetworkhandle从
* 队列中取走消息，发送到网络上。
*
* 异常处理：
* run()方法中的异常处理：
*  有两层try-catch语句。内层try-catch语句用于处理消息接受过程中发生的异常。
* 外层的try-catch语句用于处理发送消息过程中发生的异常以及其他类型异常。
* 其他方法的异常处理（rcvMsg,sendMsg,doConnect,doAccept）:
*  异常经过封装往上抛出，原始异常作为新异常的cause.最后这些异常会汇总到run方法中的异常处理体系。
* 这样可以确保：任意一个环节抛出异常，run方法中的while循环不会中断，它的唯一中断因子就是timeToStop参数。
*
* 通信过程：
* 作为服务器角色：只需要接受客户端的连接建立请求，然后接收消息；
* 作为客户端角色：需要主动与服务器通信。它首先要发送connect信息，建立与服务器的通信信道。
* 然后维护一个连接池，以kv的形式存储连接，key是服务器的IP，value是与服务器的channel
* 下次与服务器通信的时候就不需要新建连接.
*
* 这里存在一个问题：当发送connect消息之后，紧接着发送其他消息，比如register消息，后一个消息可能由于
* 连接尚未建立而丢弃掉。
 */

public class MasterNetworkHandle  extends NetworkHandle implements Runnable{

    private static Logger logger = Logger.getLogger(MasterNetworkHandle.class);

    private int port;//作为服务器的指定端口；

    private ServerSocketChannel serverChannel;//服务器连接通道；

    private Selector selector;//轮询器

    private final Map<String, SocketChannel> connectionPool;//连接池，保存了与其他所有结点的通讯通道

    private final AtomicBoolean timeToStop;//结束标志，共用一个，同步关闭

    private final BlockingQueue<Message> messagesToSlavesManager;//消息队列,向slavesmanager传递消息

    private final BlockingQueue<Message> messagesToAccesssManager;//消息队列,向accessmanager传递消息

    private final BlockingQueue<Message> messagesFromSlavesManager;//消息队列,从slavesmanager接受消息

    private final BlockingQueue<Message> messagesFromAccesssManager;//消息队列,从accessmanager接受消息


    //构造函数
    public MasterNetworkHandle(int port,
                               AtomicBoolean timeToStop,
                               BlockingQueue<Message> messagesToSlavesManager,
                               BlockingQueue<Message> messagesToAccesssManager,
                               BlockingQueue<Message> messagesFromSlavesManager,
                               BlockingQueue<Message> messagesFromAccesssManager) throws Throwable {

        this.connectionPool = new HashMap<>();
        this.timeToStop = timeToStop;
        this.messagesToSlavesManager = messagesToSlavesManager;
        this.messagesToAccesssManager = messagesToAccesssManager;
        this.messagesFromSlavesManager = messagesFromSlavesManager;
        this.messagesFromAccesssManager = messagesFromAccesssManager;

        try {
            this.port = port;
            this.serverChannel = ServerSocketChannel.open();
            this.selector = Selector.open();

            //开启非阻塞模式
            serverChannel.configureBlocking(false);
            //绑定端口
            serverChannel.bind(new InetSocketAddress(port), 1024);
            //向selector注册，注册事件是接受连接请求
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("MasterNetworkHandle has been instantiated!");
        }catch(IOException e){
            logger.error("install socketchannel and selector failed!");
            cleanUp();
            Throwable ae = new IOException("install socketchannel and selector failed!");
            ae.initCause(e);
            throw ae;
        }
    }

    @Override
    public void run() {

        while (!timeToStop.get()) {
            //首先检查是否有输入请求
            //开启非阻塞模式
            try {
                selector.selectNow();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        if(key.isAcceptable()){
                            doAccept(key);//接受连接
                        }
                        if(key.isReadable()){
                            rcvMsg(key);//读
                        }
                        if(key.isConnectable()){
                            doConnect(key);//请求连接
                        }
                    } catch (IOException e) {
                        logger.error("error occur in processing key:\t"+e.getMessage());
                        logger.error(e.getCause().getMessage());
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }

                //上层的try-catch语句可以确保即使处理输入抛出了异常，也不会印影响到处理输出
                //接下来处理输出
                while(true){
                    //以非阻塞的方式获取元素
                    Message message = messagesFromAccesssManager.poll();
                    if(message == null)break;
                    sendMsg(message);
                }

                while(true){
                    Message message = messagesFromSlavesManager.poll();
                    if(message == null)break;
                    sendMsg(message);
                }
            } catch (Exception e) {
                logger.error("error occur in sending message:\t"+e.getMessage());
                logger.error(e.getCause().getMessage());
            }
        }

        cleanUp();
    }

    @Override
    protected void cleanUp(){

        //selector关闭后会自动释放里面管理的资源
        if(selector != null)
            try{
                selector.close();
                logger.info("masternetworkhandle has been shutdown proerly!");
            }catch (Exception e) {
                logger.error("failed to close selector!");
                e.printStackTrace();
            }
    }

    protected Message rcvMsg(SelectionKey key) throws IOException {

        //处理消息
        if(key.isReadable()){
            try {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                sc.read(buffer);

                buffer.flip();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                //还原成消息
                final Message message = Message.buildFromStream(bytes);
                //将消息插入队列
                if (Message.HEARTBEAT.equals(message.get(Message.TYPE)) ||
                        Message.REGISTER.equals(message.get(Message.TYPE))) {//应该发送给slavemanager
                    messagesToSlavesManager.add(message);
                } else if (Message.READ.equals(message.get(Message.TYPE)) ||
                        Message.WRITE.equals(Message.TYPE)) {//应该发送给accessmanager
                    messagesToAccesssManager.add(message);
                } else {//有可能是connect消息，do nothing
                    logger.info("wrong message was received in rcvMsg!");
                }

                logger.debug("message \t" + Message.parseToString(message) + "\t has been processed in rcvMsg!");
            }catch (IOException e){
                IOException ae = new IOException("error occur in rcvMsg");
                ae.initCause(e);
                throw ae;
            }
        }
        return null;
    }

    protected void doAccept(SelectionKey key) throws IOException {

        try {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel client = ssc.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            logger.debug("requset from \t" + client.socket().getInetAddress().getHostName() + "\t has been accepted");
        }catch(IOException e){
            IOException ae = new IOException("error occur in doAccept");
            ae.initCause(e);
            throw ae;
        }
    }

    protected void sendMsg(Message message) throws IOException {

        String ip = message.get("DST");
        SocketChannel channel;
        //如果连接池中不存在该IP对应的socketchannel，则先建立连接
        if ((channel = connectionPool.get(ip)) == null) {
            try {
                channel = SocketChannel.open();
                doConnect(ip, channel);
            }catch (IOException e){
                IOException ae = new IOException("error occur in sendMsg:connection failed");
                ae.initCause(e.getCause());
                throw ae;
            }
        } else if (channel.isConnected()) {
            //发送消息
            byte[] bytes = Message.parsetToStream(message);
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            channel.write(buffer);
            logger.debug("message \t" + Message.parseToString(message) + "\t has been send out");
        } else {
            connectionPool.remove(ip);
            System.out.println("unknown exception!");
        }
    }

    protected void doConnect(String ip,SocketChannel channel) throws IOException {

        try{
        //如果能够立马建立连接，则将新建连接投入连接池
        if(channel.connect(new InetSocketAddress(ip,12346))){
            connectionPool.put(ip,channel);
            logger.debug("connection to host \t"+ip+"\t"+"has been established at once!");
        }
        //如果不能立马建立连接，则向selector注册连接事件，稍后调用finishConnect()方法
        else{
            channel.register(selector,SelectionKey.OP_CONNECT);
        }
        }catch (IOException e){
            IOException ae = new IOException("error occur in doConnect-connect");
            ae.initCause(e);
            throw ae;
        }
    }

    protected void doConnect(SelectionKey key) throws IOException {

        try {
            SocketChannel channel = (SocketChannel) key.channel();
            if (channel.finishConnect()) {
                String host = channel.socket().getInetAddress().getHostName();
                connectionPool.put(host, channel);
                logger.debug("connection to host \t" + host + "\t" + "has been established at twice!");
            }//else,连接无法建立，do nothing
        }catch (IOException e){
            IOException ae = new IOException("error occur in doConnect-finishconnect");
            ae.initCause(e);
            throw ae;
        }
    }

    public static void main(String[] args) throws Throwable {

        MasterNetworkHandle handle = new MasterNetworkHandle(12345,
                new AtomicBoolean(false),
                null,
                null,
                null,
                null);

        Message message = new Message();
        message.add("DST","127.0.0.1");
        Thread t = new Thread(handle);
        t.start();
        handle.sendMsg(message);
        message.add("hello","mama");
        handle.sendMsg(message);
    }
}


