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
import slave.Slave;

import java.util.ArrayList;
import java.util.List;


public class MasterWriteHandler extends MasterAccessHandler {

    private static Logger logger = Logger.getLogger(MasterWriteHandler.class);

    //构造函数
    public MasterWriteHandler(Message msg) {
        super();
        this.msg = msg;
    }

    @Override
    public void run() {
        handle();
    }

    @Override
    public void handle() {

        logger.debug("MasterWriteHandler is processing msg!");

        try {
            //首先分配id
            long dataID = MasterAgent.dataID.getAndIncrement();
            logger.debug("dataid is:"+dataID);
            //确定文件所在的块
            long blockID = Block.getBlockID(dataID);
            logger.debug("blockid is:"+blockID);

            Block block = null;

            /*满足这样的特征则新建块：dataid是numsperblock的整数倍+1*/
            if ((dataID-1l)%MasterAgent.NUMSPERBLOCK == 0) {
                block = new Block(blockID);
                List<Slave> candidates = selectSlaves(MasterAgent.slaves.values());
                List<Integer> ids = new ArrayList<>();
                List<String> ips = new ArrayList<>();
                for(Slave slave : candidates){
                    ids.add(slave.getId());
                    ips.add(slave.getIp());
                }
                block.addnodes(ids);

                //更新blocks
                MasterAgent.blocks.put(blockID, block);
                //更新slaves
                for (Slave candidate : candidates)
                    MasterAgent.slaves.get(candidate.getId()).addBlock(blockID);

                logger.debug("slaves now is:"+MasterAgent.slaves+"\t blocks now is:"+MasterAgent.blocks);

                //向所有存储结点发送新建块的信息
                for(String ip : ips){
                    Message msg = new Message();
                    msg.add(MessagePool.TYPE,MessagePool.ADDBLOCK);
                    msg.add(MessagePool.BLOCKID,Long.toString(blockID));
                    msg.add(MessagePool.FELLOW,Message.fellowTransform(candidates));
                    msg.add(MessagePool.TOHOST,ip);

                    MasterAgent.queue2.add(msg);
                }

                //只向一个存储结点发送写数据的信息
                msg.add(MessagePool.DATAID,Long.toString(dataID));
                msg.add(MessagePool.TOHOST,ips.get(0));

            } else {
                /*
                * 考虑这样一种情形：有两个keyid：1,2，分别由请求1和请求2来处理，
                * 请求1应该新建块，后执行了，请求2不建块，先执行了，直接转入else语句，发现块不存在，
                * 这个时候会抛出空指针异常，导致请求2失败
                * 所以这里通过while循环，让请求2在请求1之后执行
                * */

                while(block == null)block = MasterAgent.blocks.get(blockID);
                Slave slave = selectSlave(block.getnodes());
                String toHost = slave.getIp();
                msg.add(MessagePool.DATAID,Long.toString(dataID));
                msg.add(MessagePool.TOHOST,toHost);
            }

            MasterAgent.queue2.add(msg);

            logger.debug("write request from host" + msg.get(MessagePool.FROMHOST)+"has been processed out!");

        }catch (Exception e){
            logger.error("unexceptional error occur,write request from" +msg.get(MessagePool.FROMHOST)+"has failed!");
            logger.debug(e.getMessage());
            e.printStackTrace();
        }
    }
}


