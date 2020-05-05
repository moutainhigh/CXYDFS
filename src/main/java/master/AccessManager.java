/*
author:chxy
data:2020/5/3
description:处理读写请求
*/
package master;

import log4j.Demo;
import org.apache.log4j.Logger;
import other.Handler;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class AccessManager implements Runnable{

    private static Logger logger = Logger.getLogger(AccessManager.class);

    private final AtomicBoolean timeToStop;//结束标志，共用一个，同步关闭

    private final ConcurrentHashMap<String,File> files;//文件元数据，与slavemanager互斥访问

    private final ConcurrentHashMap<Integer, Slave> slaves;//结点元数据，与slavemanager互斥访问

    private final ConcurrentHashMap<Slave, List<String>> nodeToFiles;//记录一个结点存储了哪些文件，与NodeManager互斥访问

    private final Handler accessHandler;//处理读写请求（核心点）

    private final BlockingQueue<Message> messagesIn;//输入消息队列

    private final BlockingQueue<Message> messagesOut;//输出消息队列

    private class AccessHandler implements Handler{

        @Override
        public void handle(Message message) throws Exception {

            logger.info("deal with read and write in the same way for now!");
            logger.debug("hand off the message to the queue in the accessHandler for now!");
            messagesOut.add(message);
        }
    }

    //构造函数
    public AccessManager(AtomicBoolean timeToStop,
                         ConcurrentHashMap<String,File> files,
                         ConcurrentHashMap<Integer, Slave> slaves,
                         ConcurrentHashMap<Slave, List<String>> nodeToFiles,
                         BlockingQueue<Message> messagesIn,
                         BlockingQueue<Message> messagesOut){
        this.timeToStop = timeToStop;
        this.files = files;
        this.slaves = slaves;
        this.nodeToFiles = nodeToFiles;
        this.messagesIn = messagesIn;
        this.messagesOut = messagesOut;
        this.accessHandler = new AccessHandler();
        logger.info("accessmanager has been ready!");

    }

    @Override
    public void run() {

        while(!timeToStop.get()){

            //从队列中取出消息，解析消息，处理消息
            //取出消息，队列为空则阻塞（1）
            try {
                Message message = messagesIn.take();
                if(Message.READ.equals(message.get(Message.TYPE))||
                Message.WRITE.equals(message.get(Message.TYPE))){
                    accessHandler.handle(message);
                }else{//消息错误，do nothing
                    logger.info("wrong message,do nothing in accessmanager!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch(Exception e){
                /*e.getCause().printStackTrace();*/
            }

        }

    }
}


