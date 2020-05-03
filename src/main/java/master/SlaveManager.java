/*
author:chxy
data:2020/5/3
description:管理结点，处理结点上线和下线
*/
package master;

import other.Handler;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlaveManager implements Runnable{

    private final AtomicBoolean timeToStop;//结束标志，共用一个，同步关闭

    private final ConcurrentHashMap<Integer, Slave> slaves;//结点元数据，与accessmanager互斥访问

    private final ConcurrentHashMap<Slave, List<String>> nodeToFiles;//记录一个结点存储了哪些文件，与accessmanager互斥访问

    private final Handler registerHandler;//处理注册（核心点）

    private final Handler heartbeatHandler;//处理心跳（核心点）

    private final BlockingQueue<Message> messages;//消息队列

    //处理注册的handler
    private static class RegisterHandler implements Handler{

        @Override
        public void handle(Message message) throws Exception {

            System.out.println("do nothing in the registerHandler for now!");
        }
    }

    //处理心跳的handler
    private static class HeartbeatHandler implements Handler{

        @Override
        public void handle(Message message) throws Exception {

            System.out.println("do nothing in the heartbeatHandler for now!");
        }
    }

    //构造函数
    public SlaveManager(AtomicBoolean timeToStop,
                        ConcurrentHashMap<Integer, Slave> slaves,
                        ConcurrentHashMap<Slave,List<String>> nodeToFiles,
                        BlockingQueue<Message> messages){
        this.timeToStop = timeToStop;
        this.slaves = slaves;
        this.messages = messages;
        this.nodeToFiles = nodeToFiles;
        registerHandler = new RegisterHandler();
        heartbeatHandler = new HeartbeatHandler();
    }

    @Override
    public void run() {

        while(!timeToStop.get()){

            //从队列中取出消息，解析消息，处理消息
            //取出消息，队列为空则阻塞（1）
            try {
                Message message = messages.take();
                if (Message.REGISTER.equals(message.get(Message.TYPE))){//注册
                    registerHandler.handle(message);
                }else if(Message.HEARTBEAT.equals(message.get(Message.TYPE))){//心跳
                    heartbeatHandler.handle(message);
                }else{//消息错误，do nothing
                    System.out.println("wrong message,do nothing in slavemanager!");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch(Exception e){
                /*e.getCause().printStackTrace();*/
            }
        }
    }
}


