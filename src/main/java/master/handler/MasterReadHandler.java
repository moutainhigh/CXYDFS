/*
author:chxy
data:2020/5/20
description:
*/
package master.handler;

import master.agent.MasterAgent;
import master.staticresource.Block;
import miscellaneous.Message;
import miscellaneous.MessagePool;
import org.apache.log4j.Logger;
import master.staticresource.Slave;

import java.util.List;

public class MasterReadHandler extends MasterAccessHandler{

    private static Logger logger = Logger.getLogger(MasterReadHandler.class);

    //构造函数
    public MasterReadHandler(Message msg) {
        super();
        this.msg = msg;
    }

    @Override
    public void run() {
        handle();
    }

    @Override
    /*
    * 可能会出现两个不存在：1.我要读的块不存在；2.我要访问的结点不存在
    * 都会导致nullpointerexception异常的抛出
    * 不会影响数据的一致性
    * 该信息无法得到处理，客户端等待并超时
    *
    * */
    public void handle() {

        logger.debug("MasterReadHandler is processing msg!");

        try {
            //获取数据的keyID
            int keyID = Integer.parseInt(msg.get(MessagePool.DATAID));
            //获取数据所在的块号
            long blockID = Block.getBlockID(keyID);
            //获取块，这个块也可能不存在
            //如果块不存在，任务将终止，不会对内部文件造成破坏，客户端方面会因等待超时
            Block block = MasterAgent.blocks.get(blockID);
            //获取块分布的结点
            List<Integer> slaves = block.getnodes();
            //随机选出一个结点，这个结点可能已经挂机并被移除
            //如果结点不存在，任务将终止，不会对内部文件造成破坏，客户端方面会因等待超时
            Slave slave = selectSlave(slaves);
            //找到slave的IP，向他发送message
            msg.add(MessagePool.TOHOST, slave.getIp());
            //插入消息队列，转发
            MasterAgent.queue2.add(msg);

            logger.debug("read request from host" + msg.get(MessagePool.FROMHOST)+"has been processed out!");
            logger.debug("now the msg is:"+msg);
        }catch (Exception e){
            logger.error("unexceptional error occur,read request from" +msg.get(MessagePool.FROMHOST)+"has failed!");
            logger.debug(e.getMessage());
            e.printStackTrace();
        }



    }
}


