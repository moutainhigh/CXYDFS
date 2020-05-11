/*
author:chxy
data:2020/5/3
description:管理结点，处理结点上线和下线
*/
package master;

import org.apache.log4j.Logger;
import other.Handler;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SlaveManager implements Runnable{

    private static Logger logger = Logger.getLogger(SlaveManager.class);

    private  AtomicInteger slaveID;//用于授予slave节点的id，从1开始

    private final AtomicBoolean timeToStop;//结束标志，共用一个，同步关闭

    private final ConcurrentHashMap<Integer, Slave> slaves;//结点元数据，与accessmanager互斥访问

    private final ConcurrentHashMap<Integer, List<String>> slaveToFiles;//记录一个结点存储了哪些文件，与accessmanager互斥访问

    private final ConcurrentHashMap<Integer, Heartbeat> heartbeats;//记录结点的心跳情况，与heartbeatGuard互斥访问

    private final Handler registerHandler;//处理注册（核心点）

    private final Handler heartbeatHandler;//处理心跳（核心点）

    private final BlockingQueue<Message> messagesIn;//输入消息队列

    private final BlockingQueue<Message> messagesOut;//输出消息队列

    private final Timer heartbeatGuard ;

    public static final String MASTER = "MASTER";//主结点类型

    public static final String SLAVE = "SLAVE";//从结点类型

    public static final long timeout = 10000*30;//心跳超时参数

    public static final int lostOut = 3;//失联超3次则被踢出

    public static final int complainOut = 2;//被两个及以上的fellow投诉则踢出

    //处理注册的handler
    private class RegisterHandler implements Handler{

        @Override


     /*
        这里面临一个问题：该进程同时访问多个文件，而这些文件同时也被其他多个进程同时访问，
        是否需要加锁：一次性获取所有的互斥资源，访问完毕之后再释放？
        不需要:
         当accessmaager修改完了slaves文件之后（增加了一个结点），
         再去修改slavesToFiles文件，如果这中间发生了其他线程修改slavesToFiles文件（改变slave到file的映射），没有影响；
         如果这中间发生了其他线程修改heartbeats文件，也没有影响（删除一个结点）；
        */
        public void handle(Message message) throws Exception {

            //解析消息，判断注册的阶段,"这里假定消息格式正确，不处理由于消息格式导致的异常"
            int phase = Integer.parseInt(message.get(Message.PHASE));
            if(phase == 1){//注册的第一阶段，授予id
                message.add(Message.SLAVEID,Integer.toString(slaveID.getAndDecrement()));

                logger.debug("the message in phase"+phase+"has been process,hand out to the queue!");
            }else if(phase == 3) {//注册的第三阶段，更新内部文件,slaves,slaveToFiles,heartbeats
                int id = Integer.parseInt(message.get(Message.SLAVEID));
                if (!slaves.containsKey(id)) {//如果出现了重复的注册，则说明报文4丢失，忽略
                    String host = message.get(Message.FROMHOST);
                    Heartbeat heartbeat = new Heartbeat(id);
                    Slave slave = new Slave(id, SlaveManager.SLAVE, host, heartbeat);

                    //修改内部文件
                    slaves.put(id,slave);

                    heartbeats.put(id, heartbeat);

                    slaveToFiles.put(id, new ArrayList<>());

                    logger.debug("the message in phase"+phase+"has been process,hand out to the queue! \n" +
                            "the slave now is :\t"+slaves+"\n"+
                            "the heartbeats now is:"+heartbeats+"\n" +
                            "the slaveToFiles now is:"+slaveToFiles);
                }
            }
            //共有操作：phase+1,tohost与fromhost调转
            message.add(Message.PHASE,Integer.toString(phase+1));
            Message.reverseHost(message);

            messagesOut.add(message);
        }
    }

    //处理心跳的handler
    /*
    * 专门设置一个guard线程，用于定期地更新心跳信息：
    * 如果thisContact-lastContact >= timeout，则说明心跳超时，lost参数加一；
    * 如果lost参数加到3，则踢出条件之一满足，该结点被踢出；
    * 如果lost从1到2到3这个过程中，某一次没有发生超时，则lost清0；
    * 只有连续超时3次才会被踢出！
    *
    * 从结点之间交换心跳也是这个逻辑，只有连续超时3次才会被认定不可达，向主结点报告！
    *
    * 如果发现complainOut参数达到2，则踢出条件之一满足，该结点被踢出；
    *
    * guard定期执行，这个期限是：timeout*1.5;
    *
    * 如果这个期限设置得太小，接近或小于timeout，有可能造成误判。比如说：我正准备更新我的heartbeat，但是heartbeat是一个竞争性的资源，
    * 被guard线程获取到了，它检查发现心跳超时，但实际上心跳未超时。
    *
    * 如果这个期限设置得太大，远超timeout，则对心跳超时的判断不够及时；
    *
    * 比如我的超时参数是60秒钟，收到心跳信息之后，再隔60秒钟（或少于60秒钟）必须再收到下一个心跳，否则被认为超时。
    * 那么从结点发完一个心跳之后，是不是要在等60秒钟发送第二个心跳。显然不行，因为还要经过网络传输，在源主机和
    * 目的主机还要经过排队/处理，等到心跳信息被更新，肯定已经超过60秒。
    * 所以发送方发完一个心跳，再等30秒就要发送下一个心跳。
    *
    * 同时被3个fellow投诉会被踢出
    * 假设现在系统有10个结点，其中ABC三个结点存储同一份文件。
    * 假设A与B之间出现了网络分割，互相投诉对方，但是由于只被一个结点投诉，
    * 所以它们都不会被踢出。这会否导致信息不一致？
    * 会。
    * 假设A收到了10条数据的更新，id：100-110，
    * 但是B无法收到更新，这部分数据不存在。A与B的数据就出现了不一致，而且这个不一致会一直持续下去！
    *
    * 这个时候，可以让C传播此更新，C收到A的更新后，会把该更新再转发给B。
    * 如果C也无法与B通讯，那么投诉B的结点将有两个，足以把B踢出去，信息不一致的问题自然就解决了！
    *
    * */
    private class HeartbeatHandler implements Handler{

        @Override
        public void handle(Message message) throws Exception {

            logger.debug("HeartbeatHandler is processing message!");
            //获取id
            int id = Integer.parseInt(message.get(Message.SLAVEID));
            //获取heartbeat
            Heartbeat heartbeat = heartbeats.get(id);
            if(heartbeat != null){
                //首先更新contact信息
                long timestamp = Long.parseLong(message.get(Message.TIMESTAMP));
                heartbeat.updateContact(timestamp);

                //更新被投诉结点
                String[] complaints = Message.strToComplaint(message.get(Message.COMPLAINTS));

                for(String complaint : complaints){
                   Heartbeat host = heartbeats.get(Integer.parseInt(complaint));
                   if(host != null){//host等于null说明被投诉的结点已经被删除了，但是更新没有及时传递到从结点，do nothing
                       host.getComplaint().add(id);
                   }else{
                       logger.debug(" be-complained-host does not exist,do nothing!");
                   }
                }
                logger.debug("heartbeat message has been processed out,do nothing!");
                logger.debug("the heartbeat now is:\t"+heartbeat);
            }else{
                /*
                此种异常发生于：
                某个结点掉线了，（被举报）这个掉线处理需要一定的时延，他自己还傻傻地不知道，
                依旧发送心跳信息
                暂不处理
                 */
                logger.error("unexceptional errors occur : cannot find heartbeat owner,do nothing for now!");
            }
        }
    }

    //构造函数
    public SlaveManager(AtomicBoolean timeToStop,
                        ConcurrentHashMap<Integer, Slave> slaves,
                        ConcurrentHashMap<Integer,List<String>> slaveToFiles,
                        BlockingQueue<Message> messagesIn,
                        BlockingQueue<Message> messagesOut){

        this.timeToStop = timeToStop;
        this.slaves = slaves;
        this.heartbeats = new ConcurrentHashMap<>();
        this.messagesIn = messagesIn;
        this.messagesOut = messagesOut;
        this.slaveToFiles = slaveToFiles;
        slaveID = new AtomicInteger(1);
        registerHandler = new RegisterHandler();
        heartbeatHandler = new HeartbeatHandler();
        this.heartbeatGuard = new Timer();
        logger.info("slavemanager has been ready!");

    }


    /*
    * 这里存在数据不一致的情况：
    *  guard在检查心跳信息的时候，这个心跳信息不可能遭到其他线程的修改:
    * heartbeats是临界资源
    * heartbeat不是临界资源
    *
    * */
    private void scanHeartbeat(){


       for(Map.Entry<Integer,Heartbeat> entry:heartbeats.entrySet()){
           boolean isTimeout = false;
           boolean isComplainout = false;
           Heartbeat beat = entry.getValue();
           //首先检查心跳
           long span = beat.getDiff();
           if(span == 0||span > timeout){//超时处理,等于0说明没有更新过
               int lost = beat.getLost()+1;
               beat.setLost(lost);
               if(lost >= 3){
                   isTimeout = true;
               }
           }else{//未超时处理
               beat.setLost(0);
           }
           //然后检查投诉情况
           if(beat.getComplaint().size() >= complainOut)
               isComplainout = true;

           //最后决定是否做踢出处理
           if(isTimeout || isComplainout){
               logger.info("host:\t"+beat.getId()+"has been kicked out!");
           }
       }


    }

    @Override
    public void run() {

        //首先启动guard
        heartbeatGuard.schedule(new TimerTask() {
            @Override
            public void run() {
                scanHeartbeat();
            }
        },1000l,(long)(timeout*1.5));

        while(!timeToStop.get()){

            //从队列中取出消息，解析消息，处理消息
            //取出消息，队列为空则阻塞（1）
            try {
                Message message = messagesIn.take();
                if (Message.REGISTER.equals(message.get(Message.TYPE))){//注册
                    registerHandler.handle(message);
                }else if(Message.HEARTBEAT.equals(message.get(Message.TYPE))){//心跳
                    heartbeatHandler.handle(message);
                }else{//消息错误，do nothing
                    logger.info("wrong message was received,do nothing in slavemanager!");
                }
            }catch(Exception e){
                e.printStackTrace();
                logger.debug("other unexceptional errors occur in registerhandle or heartbeat handle,and this should not happen! \t ");
            }
        }

        heartbeatGuard.cancel();
    }
}


