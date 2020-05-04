/*
author:chxy
data:2020/5/4
description:全权处理网络通讯
*/
package network;

import master.Message;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
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

public class NetworkHandle implements Runnable{

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
    public NetworkHandle(int port,
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
        }catch(IOException e){
            cleanUp();
            Throwable ae = new IOException("initialize socketchannel and selector failed");
            ae.initCause(e);
            throw ae;
        }
    }

    private void cleanUp(){

        //selector关闭后会自动释放里面管理的资源
        if(selector != null)
            try{
                selector.close();
            }catch (Exception e) {
                e.printStackTrace();
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
                        rcvMsg(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }

                //接下来处理输出
//                while(true){
//                    Message message = messagesFromAccesssManager.poll();
//                    if(message == null)break;
//                    sendMsg(message);
//                }
//                while(true){
//                    Message message = messagesFromSlavesManager.poll();
//                    if(message == null)break;
//                    sendMsg(message);
//                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Message rcvMsg(SelectionKey key) throws IOException {

        //处理新的连接建立请求
        if(key.isAcceptable()){
            doAccept(key);
        }

        //处理消息
        if(key.isReadable()){
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            sc.read(buffer);
            System.out.println("do nothing with the message in rcvMsg");
        }

        return null;
    }

    private void doAccept(SelectionKey key) throws IOException {

        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel client = ssc.accept();
        client.configureBlocking(false);
        client.register(selector,SelectionKey.OP_READ);
    }

    private void sendMsg(Message message){

        System.out.println("do nothing for now");
    }

    public static void main(String[] args) throws Throwable {

        NetworkHandle handle = new NetworkHandle(12345,
                new AtomicBoolean(false),
                null,
                null,
                null,
                null);

        Thread t = new Thread(handle);
        t.start();

    }
}


