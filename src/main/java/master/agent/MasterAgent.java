/*
author:chxy
data:2020/5/20
description:
*/
package master.agent;

import master.dispatcher.MasterDispatcher;
import master.handler.MasterHeartbeatHandler;
import master.staticresource.Block;
import miscellaneous.*;
import org.apache.log4j.Logger;
import master.staticresource.Slave;
import util.Deseri;
import util.SeriUtil;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MasterAgent {

    private static final Logger logger =  Logger.getLogger(MasterAgent.class);

    public static final int NUMSPERBLOCK = 1000;//每一块所包含的数据条数
    public static final int POOLSIZE = 100;//线程池大小
    public static final int DUPLICATIONNUMS = 3;//副本数

    public static  ConcurrentHashMap<Integer, Slave> slaves ;//结点元数据

    public static  ConcurrentHashMap<Long, Block> blocks;//块元数据

    public static  AtomicLong dataID ;//数据id生成器,从1开始

    public static  AtomicLong blockID ;//块id生成器,从0开始

    public static  AtomicInteger slaveID  ;//数据结点id生成器，从0开始

    public static final BlockingQueue<Message> queue1 = new LinkedBlockingQueue<>();//消息队列1

    public static final BlockingQueue<Message> queue2 = new LinkedBlockingQueue<>();//消息队列2

    public static final ExecutorService engine = Executors.newFixedThreadPool(POOLSIZE);//处理消息的引擎，线程池,默认1000个活跃线程

    private static Timer heartbeatguard = new Timer();//定时器，定时地检查节点的心跳情况

    public static final AtomicBoolean timeToStop = new AtomicBoolean(false);//用于同步关闭各个组件

    private static Dispatcher dispatcher;//消息分发器

    //初始化静态属性，slaves,blocks,ids
    public static void initialize() {

        try {

            slaves = Deseri.restoreSlaves(FilePath.ROOTPATH.resolve(FilePath.SLAVEPATH).toFile());

            blocks = Deseri.restoreBlocks(FilePath.ROOTPATH.resolve(FilePath.BLOCKPATH).toFile());

            List<Object> ids = Deseri.restoreIds(FilePath.ROOTPATH.resolve(FilePath.IDPATH).toFile());

            dataID = (AtomicLong)ids.get(0);

            blockID = (AtomicLong)ids.get(1);

            slaveID = (AtomicInteger)ids.get(2);

            logger.debug("initialize is done");

        }catch(Exception e){
            logger.error(e.getCause().getMessage());
            System.exit(-1);
        }
    }

    //初始化动态资源,(设置线程池),启动网络通讯线程和心跳扫描线程
    public static void launch(){

        /*启动心跳扫描线程*/
        heartbeatguard.schedule(new TimerTask() {
            @Override
            public void run() {
                MasterHeartbeatHandler.scanHeartbeat();
            }
        },1000l,(long)(Node.TIMEOUT*1.5));

        /*启动调度器*/
        dispatcher = MasterDispatcher.getInstance();
        Thread t = new Thread(dispatcher);
        t.start();

        /*启动网络通讯线程*/

    }

    //首先要关闭动态资源
    public static void stop(){

        try {

            /*关闭网络通讯线程*/

            /*关闭调度器*/
            Message msgQuit = new Message();
            msgQuit.add(MessagePool.TYPE,MessagePool.QUIT);

            /**/
            /*以一种柔和的方式关闭线程池，该方法会立即返回*/
            /*为确保所有任务执行完毕，须调用awaitTermination*/
            /*该方法会一直阻塞，直到所有任务执行完毕,这里假定所有任务都是可执行完毕的*/
            engine.shutdown();
            engine.awaitTermination(Long.MAX_VALUE,TimeUnit.MILLISECONDS);


            /*排空消息队列*/
            queue1.clear();
            queue2.clear();

            /*以柔性的方式关闭心跳扫描线程，正在执行的任务将执行完毕*/
            heartbeatguard.cancel();

            //这是一个排空期，确保执行cleanup方法的时候，没有任何存活的线程会去访问静态资源从而导致数据不一致
            Thread.currentThread().sleep(1000l);
        } catch (InterruptedException e) {
            logger.error("error in stop method");
            System.exit(-1);
        }
    }

    public static void cleanup(){
        //将slaves，blocks和ids序列化到磁盘
        try {
            SeriUtil.saveSlaves(FilePath.ROOTPATH.resolve(FilePath.SLAVEPATH).toFile(),slaves);

            SeriUtil.saveBlocks(FilePath.ROOTPATH.resolve(FilePath.BLOCKPATH).toFile(),blocks);

            List<Object> ids = new ArrayList<>();
            ids.add(dataID);
            ids.add(blockID);
            ids.add(slaveID);

            SeriUtil.saveIDs(FilePath.ROOTPATH.resolve(FilePath.IDPATH).toFile(),ids);

            logger.debug("clean up is done");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    public static void main(String[] args) {

        Scanner sca = new Scanner(System.in);

        String order = null;

        while(!(order = sca.next()).equals("start"));

        initialize();

        launch();

        /*这中间可以提供修改线程池属性的接口*/
        /*((ThreadPoolExecutor)engine).*/
        /*容量，生存时间，任务队列的长度*/
        /*还可以提供查看消息队列属性接口，例如消息队列当前的长度*/

        while(!(order = sca.next()).equals("quit"));

        stop();

        cleanup();

    }
}


