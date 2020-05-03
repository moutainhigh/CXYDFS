/*
author:chxy
data:2020/5/3
description:处理读写请求
*/
package master;

import other.Handler;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class AccessManager implements Runnable{

    private final AtomicBoolean timeToStop;//结束标志，共用一个，同步关闭

    private final ConcurrentHashMap<String,File> files;//文件元数据，与slavemanager互斥访问

    private final ConcurrentHashMap<Integer, Slave> slaves;//结点元数据，与slavemanager互斥访问

    private final ConcurrentHashMap<Slave, List<String>> nodeToFiles;//记录一个结点存储了哪些文件，与NodeManager互斥访问

    private final Handler accessHandler;//处理读写请求（核心点）

    private final BlockingQueue<Message> messages;//消息队列,与网络传输线程共享

    private static class AccessHandler implements Handler{

        @Override
        public void handle(Message message) throws Exception {

            System.out.println("deal with read and write in the same way for now!");
            System.out.println("do nothing in the accessHandler for now!");
        }
    }

    //构造函数
    public AccessManager(AtomicBoolean timeToStop,
                         ConcurrentHashMap<String,File> files,
                         ConcurrentHashMap<Integer, Slave> slaves,
                         ConcurrentHashMap<Slave, List<String>> nodeToFiles,
                         BlockingQueue<Message> messages){
        this.timeToStop = timeToStop;
        this.files = files;
        this.slaves = slaves;
        this.nodeToFiles = nodeToFiles;
        this.messages = messages;
        this.accessHandler = new AccessHandler();

    }

    @Override
    public void run() {

        while(!timeToStop.get()){

            //从队列中取出消息，解析消息，处理消息
            //取出消息，队列为空则阻塞（1）
            try {
                Message message = messages.take();
                if(Message.READ.equals(message.get(Message.TYPE))||
                Message.WRITE.equals(message.get(Message.TYPE))){
                    accessHandler.handle(message);
                }else{//消息错误，do nothing
                    System.out.println("wrong message,do nothing in accessmanager!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch(Exception e){
                /*e.getCause().printStackTrace();*/
            }

        }

    }
}


