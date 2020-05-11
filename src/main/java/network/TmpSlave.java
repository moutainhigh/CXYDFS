package network;/*
author:chxy
data:2020/4/19
description:
*/

import master.Message;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;


public class TmpSlave implements Runnable{

    private static Logger logger = Logger.getLogger(TmpSlave.class);

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean started;
    public TmpSlave(String ip, int port) {
        this.host = ip;
        this.port = port;
        try{

            //创建选择器
            selector = Selector.open();

            //打开监听通道
            socketChannel = SocketChannel.open();

            //如果为 true，则此通道将被置于阻塞模式；如果为 false，则此通道将被置于非阻塞模式
            socketChannel.configureBlocking(false);//开启非阻塞模式

            started = true;
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void stop(){
        started = false;
    }
    @Override
    public void run() {
        try{
            doConnect();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        //循环遍历selector
        while(started){
            try{
                //无论是否有读写事件发生，selector每隔1s被唤醒一次
                selector.select(1000);
                //阻塞,只有当至少一个注册的事件发生的时候才会继续.
                //selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                SelectionKey key = null;
                while(it.hasNext()){
                    key = it.next();
                    it.remove();
                    try{
                        handleInput(key);
                    }catch(Exception e){
                        if(key != null){
                            key.cancel();
                            if(key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
        //selector关闭后会自动释放里面管理的资源
        if(selector != null)
            try{
                selector.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
    }
    private void handleInput(SelectionKey key) throws IOException{
        if(key.isValid()){
            SocketChannel sc = (SocketChannel) key.channel();
            if(key.isConnectable()){
                //完成连接，若无法完成连接则退出
                if(sc.finishConnect());
                else System.exit(1);
            }
            //读消息
            if(key.isReadable()){
                //创建ByteBuffer，并开辟一个1M的缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(4);

                //读取请求码流，返回读取到的字节数
                int readBytes = sc.read(buffer);

                //读取到字节，对字节进行编解码
                if(readBytes>0){
                    //将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
                    buffer.flip();

                    //根据缓冲区可读字节数创建字节数组
                    byte[] bytes = new byte[buffer.remaining()];

                    //将缓冲区可读字节数组复制到新建的数组中
                    buffer.get(bytes);
                    String result = new String(bytes,"UTF-8");
                    System.out.println("客户端收到消息：" + result);
                }

                //没有读取到字节 忽略
                else if(readBytes==0);

                    //链路已经关闭，释放资源
                else if(readBytes<0){
                    key.cancel();
                    sc.close();
                }
            }
        }
    }
    //异步发送消息
    private void doWrite(SocketChannel channel,String request) throws IOException{
        //将消息编码为字节数组
        byte[] bytes = request.getBytes();

        //根据数组容量创建ByteBuffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);

        //将字节数组复制到缓冲区
        writeBuffer.put(bytes);

        //flip操作
        writeBuffer.flip();

        //发送缓冲区的字节数组
        channel.write(writeBuffer);
        //****此处不含处理“写半包”的代码

        logger.debug("message has been send out!");

    }
    private void doConnect() throws IOException{
        //若能够马上建立连接则返回true，否则返回false
        socketChannel.bind(new InetSocketAddress("localhost",NetworkHandle.SLAVEPORT));
        if(socketChannel.connect(new InetSocketAddress(host,port)));
        //若不能马上建立连接，则向selector注册，稍后由finishConnect方法来完成
        //注册事件是请求连接，一旦该channel请求连接，该请求会被selector捕获到
        else socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }
    public void sendMsg(String msg) throws Exception{

        //向selector注册，注册事件是读
        socketChannel.register(selector, SelectionKey.OP_READ);
        doWrite(socketChannel, msg);
    }

    public static void main(String[] args) throws Exception {

        Scanner sca = new Scanner(System.in);
        TmpSlave client = new TmpSlave("127.0.0.1",NetworkHandle.MASTERPORT);
        Thread t = new Thread(client);
        t.start();

        String order = null;
        while(!"start".equals(order)){
            order = sca.next();
        }
        //可以发送命令
        Message msg = new Message();
        msg.add(Message.TYPE,Message.CONNECT);
        msg.add(Message.FROMHOST,"127.0.0.1");

        //建立连接
        client.sendMsg(Message.parseToString(msg));

        //注册（跳过了1）
        msg = new Message();
        msg.add(Message.TYPE,Message.REGISTER);
        msg.add(Message.FROMHOST,"127.0.0.1");
        msg.add(Message.TOHOST,"127.0.0.1");
        msg.add(Message.SLAVEID,"1");
        msg.add(Message.PHASE,"3");

        Message msg2 = new Message();
        msg2.add(Message.TYPE,Message.REGISTER);
        msg2.add(Message.FROMHOST,"127.0.0.1");
        msg2.add(Message.TOHOST,"127.0.0.1");
        msg2.add(Message.SLAVEID,"2");
        msg2.add(Message.PHASE,"3");

        if("register".equals(order = sca.next())){
            client.sendMsg(Message.parseToString(msg));
            client.sendMsg(Message.parseToString(msg2));
        }

//        //发送心跳,互相投诉
//        msg = new Message();
//        msg.add(Message.TYPE,Message.HEARTBEAT);
//        msg.add(Message.SLAVEID,"1");
//        msg.add(Message.COMPLAINTS,"2");
//
//        msg2 = new Message();
//        msg2.add(Message.TYPE,Message.HEARTBEAT);
//        msg2.add(Message.SLAVEID,"2");
//        msg2.add(Message.COMPLAINTS,"1");
//        while("heartbeat".equals(order = sca.next())){
//            client.sendMsg(Message.parseToString(msg));
//            client.sendMsg(Message.parseToString(msg2));
//        }

        //停止发送命令
        while(!"quit".equals(order)){
            order = sca.next();
        }

        client.started = false;
    }
}
