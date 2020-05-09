/*
author:chxy
data:2020/5/3
description:主线程，启动/关停accessmanager 和 slavemanager
*/
package master;

import network.MasterNetworkHandle;
import network.NetworkHandle;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/*
* 这里非常有必要将slavetofile中的文件类型显示指定为
*
* */

public class Main {

    private static Logger logger = Logger.getLogger(Main.class);

    private static AtomicBoolean timeToStop;//结束标志，共用一个，同步关闭

    private static ConcurrentHashMap<String,File> files;//文件元数据，AccessManager与addManager互斥访问

    private static ConcurrentHashMap<Integer, Slave> slaves;//结点元数据，AccessManager与slaveManager互斥访问

    private static ConcurrentHashMap<Integer, List<String>> slaveToFiles;//记录一个结点存储了哪些文件，AccessManager与slaveManager互斥访问

    private static BlockingQueue<Message> messagesFromManager;//消息队列,*slavemanager向网络传输线程发送消息

    private static BlockingQueue<Message> messagesToSlaveManager;//消息队列,slavemanager从网络传输线程接受消息

    private static BlockingQueue<Message> messagesToAccessManager;//消息队列,AccessManager从网络传输线程接受消息

    //将初始化过程包装进这个方法
    private static void initialize(){

        timeToStop = new AtomicBoolean(false);
        files = new ConcurrentHashMap<>();
        slaves = new ConcurrentHashMap<>();
        slaveToFiles = new ConcurrentHashMap<>();
        messagesFromManager = new LinkedBlockingDeque<>();//无限队列
        messagesToSlaveManager = new LinkedBlockingDeque<>();
        messagesToAccessManager = new LinkedBlockingDeque<>();
        logger.info("initialization in main process has been completed!");
    }

    static{//静态代码块
        initialize();
    }

    public static void main(String[] args) throws Throwable {

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
                slaveToFiles,
                messagesToAccessManager,
                messagesFromManager);
        new Thread(accessManager).start();

        logger.info("accessmanager has been launched!");

        //启动slavemanager线程
        SlaveManager slaveManager = new SlaveManager(timeToStop,
                slaves,
                slaveToFiles,
                messagesToSlaveManager,
                messagesFromManager);
        new Thread(slaveManager).start();

        logger.info("slavemanager has been launched!");

        //启动masternetworkhanle线程
        MasterNetworkHandle masterNetworkHandle = new MasterNetworkHandle(
                NetworkHandle.MASTERPORT,
                timeToStop,
                messagesToSlaveManager,
                messagesToAccessManager,
                messagesFromManager
        );
        new Thread(masterNetworkHandle).start();

        logger.info("masternetworkhandle has been launched!");

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
        messagesToAccessManager.add(new Message());
        messagesToSlaveManager.add(new Message());

        logger.info("system has been shutdown properly!");
    }
}


