/*
author:chxy
data:2020/5/3
description:处理读写请求
*/
package master;

import org.apache.log4j.Logger;
import other.Handler;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class AccessManager implements Runnable{

    private static Logger logger = Logger.getLogger(AccessManager.class);

    private final AtomicBoolean timeToStop;//结束标志，共用一个，同步关闭

    private final ConcurrentHashMap<String,File> files;//文件元数据，与slavemanager互斥访问

    private final ConcurrentHashMap<Integer, Slave> slaves;//结点元数据，与slavemanager互斥访问

    private final ConcurrentHashMap<Integer, List<String>> slaveToFiles;//记录一个结点存储了哪些文件，与SlaveManager互斥访问

    private final Handler accessHandler;//处理读写请求（核心点）

    private final BlockingQueue<Message> messagesIn;//输入消息队列

    private final BlockingQueue<Message> messagesOut;//输出消息队列

    private static final Random random = new Random();

    public static final int DUPLICATIONNUMS = 3; //默认副本数为3

//才哦用随机算法，选择一个salve作为访问到目标结点
    private class AccessHandler implements Handler {

        private Slave selectSlave(List<Integer> ids){
            int id = random.nextInt(ids.size());
            return slaves.get(id);
        }

        /*从slaves文件中随机选择DUPLICATIONNUMS个slave结点作为候选结点
        但是，如果当前结点个数小于副本数，就直接返回全体结点，没有随机选择过程

         */
        private List<Slave> selectSlaves(){

            List<Slave> candidates = new ArrayList<>();
            List<Integer> nums = new ArrayList<>();
            if(slaves.size() <= DUPLICATIONNUMS){
                candidates.addAll((Collection<? extends Slave>) slaves.keys());
                return candidates;
            }

            int bound = DUPLICATIONNUMS;
            while(true) {
                int next = random.nextInt(bound);
                if (next > bound * 0.5) {
                    nums.add(next);
                    bound = next;
                }
                if (nums.size() == 3) break;
            }

            Collections.sort(nums);

            int counter = -1,k = 0;
            for(Map.Entry<Integer,Slave> entry:slaves.entrySet()){
                counter++;
                if(counter == nums.get(k)){
                    candidates.add(entry.getValue());
                    k++;
                    if(k == nums.size()-1)break;
                }
            }
            return candidates;
        }

        @Override

        /*
        * 如果这个文件不存在：采用随机算法，选取三个有效结点，将文件分布在这三个结点上；
        *随机算法描如下：
        * 首先生成三个随机数，假如是1，3，5（三个随机数不可重复，且不能超过现有结点的个数，按正序排列）
        * 然后再遍历一趟slave，在遍历的过程中设置一个计数器，如果计数器的值等于随机数了，
        * 则将当前遍历元素作为候选结点
        *
        *然后更新file 的 slaveID 和 slave 属性
         然后更新files文件
         然后更新slaveToFiles文件
         *
         * 更新slaveToFile文件的时候，非常有必要检查这个node是否还是有效的：
         * 可能这个node已经失效了，从slavetofile中被移除了，但是这一信息并没有同步更新，
         * 得到的文件列表就是null，就会抛出nullpointerexception
         *
         *
         *
         *

        * 最后在候选结点中随机选择一个写入结点，客户端的数据只写入这一个结点，
        * 其他候选结点由于更新传播会自动地同步写入数据
        *
        * 重新转换message：
        * FROMHOST：不变
        * TOHOST：之前是master的IP，之后是slave的IP
        * 新增信息：
        * key:FELLOW
        * value:fellows,即共同存储同一份文件的结点，它们之间需要互相交换心跳和更新
        *
        * 问题：如果这个message丢失了，会有什么影响？
        *
        * 没有任何影响。从结点那边收不到任何讯息，也就不会向客户端发出响应，客户端就会超时，就会
        * 重新发出写文件的请求。请求到达时发现文件名已经存在，走的是另外一个分支
        *
        * */
        public void handle(Message message) throws Exception {

            logger.info("deal with read and write in the same way for now!");
            String fileName = message.get(Message.FILE);
            int amount = Integer.parseInt(message.get(Message.AMOUNT));

            if (Message.READ.equals(message.get(Message.TYPE))) {//读文件
                //得到文件分布的结点
                File file = files.get(fileName);
                if(file != null) {//文件不存在，do nothing
                    List<Integer> nodes = file.getSlaves();
                    //采用随机算法，给出应该被访问的slave
                    int id = random.nextInt(nodes.size() - 1);
                    //通过slaves元数据，找到slave的IP，向他发送message
                    Slave slave = slaves.get(id);
                    message.add(Message.TOHOST, slave.getHost());
                }
            } else {//写文件
                //首先判断文件是否存在
                File file = files.get(fileName);
                if (file == null) {//不存在则新建文件
                    file = new File(fileName, DUPLICATIONNUMS);

                    List<Slave> candidates = selectSlaves();
                    //更新files文件
                    int previous = file.getId().getAndAdd(amount);
                    List<Integer> ids = new ArrayList<>();
                    for(Slave candidate:candidates){
                        ids.add(candidate.getId());
                    }
                    file.getSlaves().addAll(ids);
                    files.put(fileName,file);
                    Slave candidate = candidates.remove(candidates.size()-1);

                    //更新nodeToFiles文件
                    for(int id:ids){

                        List<String> f = slaveToFiles.get(id);
                        if(f != null){
                            f.add(fileName);
                        }
                    }

                    //重写消息并发送
                    message.add(Message.TOHOST, candidate.getHost());
                    message.add(Message.RANGE,Message.rangeToStr(previous,previous+amount));

                    String fellow = Message.fellowTransform(candidates);
                    message.add(Message.FELLOW, fellow);

                    messagesOut.add(message);

                } else {//文件已存在,先修改内部文件，再发送消息
                    //更新files文件
                    int previous = file.getId().getAndAdd(amount);

                    Slave candidate  = selectSlave(file.getSlaves());

                    //重写消息并发送
                    message.add(Message.TOHOST, candidate.getHost());
                    message.add(Message.RANGE,Message.rangeToStr(previous,previous+amount));
                    message.add(Message.FELLOW,Message.fellowTransform(file.getSlaves(),0));
                    messagesOut.add(message);
                }
            }
        }
    }

    //构造函数
    public AccessManager(AtomicBoolean timeToStop,
                         ConcurrentHashMap<String,File> files,
                         ConcurrentHashMap<Integer, Slave> slaves,
                         ConcurrentHashMap<Integer, List<String>> slaveToFiles,
                         BlockingQueue<Message> messagesIn,
                         BlockingQueue<Message> messagesOut){
        this.timeToStop = timeToStop;
        this.files = files;
        this.slaves = slaves;
        this.slaveToFiles = slaveToFiles;
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


