/*
author:chxy
data:2020/5/3
description:主线程，启动/关停accessmanager 和 slavemanager
*/
package master;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    private static AtomicBoolean timeToStop;//结束标志，共用一个，同步关闭

    private static ConcurrentHashMap<String,File> files;//文件元数据，AccessManager与NodeManager互斥访问

    private static ConcurrentHashMap<Integer, Slave> slaves;//结点元数据，AccessManager与NodeManager互斥访问

    private static ConcurrentHashMap<Slave, List<String>> nodeToFiles;//记录一个结点存储了哪些文件，AccessManager与NodeManager互斥访问

    private static BlockingQueue<Message> messagesForSlaveManager;//消息队列,slavemanager与网络传输线程共享

    private static BlockingQueue<Message> messagesForAccessManager;//消息队列，AccessManager与网络传输线程共享

    //将初始化过程包装进这个方法
    private static void initialize(){

        timeToStop = new AtomicBoolean(false);
        files = new ConcurrentHashMap<>();
        slaves = new ConcurrentHashMap<>();
        nodeToFiles = new ConcurrentHashMap<>();
        messagesForSlaveManager = new LinkedBlockingDeque<>();//无限队列
        messagesForAccessManager = new LinkedBlockingDeque<>();
        System.out.println("just initialize the collections for now ");
    }

    static{//静态代码块
        initialize();
    }

    public static void main(String[] args) {

        Scanner sca = new Scanner(System.in);
        String order;
        //接受启动命令
        while(true){
            try {
                order = sca.next();
                if("start".equals(order))break;
            }catch (Exception e){//处理输入异常
                e.printStackTrace();
            }
        }

        //启动accessmanager线程
        AccessManager accessManager = new AccessManager(timeToStop,
                files,
                slaves,
                nodeToFiles,
                messagesForAccessManager);
        new Thread(accessManager).start();


        //启动slavemanager线程
        SlaveManager slaveManager = new SlaveManager(timeToStop,
                slaves,
                nodeToFiles,
                messagesForSlaveManager);
        new Thread(slaveManager).start();

        //启动TmpClient线程，向两个manager发送消息
        TmpClient client = new TmpClient(messagesForSlaveManager,messagesForAccessManager);
        new Thread(client).start();

        //接受终止命令
        while(true){
            try{
                order = sca.next();
                if("quit".equals(order))break;
            }catch (Exception e){//处理输入异常
                e.printStackTrace();
            }
        }

        //结束accessmanager和slavemanager
        //要往两个消息队列中填充给一个“假消息”，以避免线程处于阻塞状态
        timeToStop.set(true);
        messagesForAccessManager.add(new Message());
        messagesForSlaveManager.add(new Message());
    }
}


