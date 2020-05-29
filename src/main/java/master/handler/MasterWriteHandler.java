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
            long keyID = MasterAgent.dataID.getAndIncrement();
            //确定文件所在的块
            long blockID = Block.getBlockID(keyID);
            //取得块
            Block block;
            String toHost;
            if ((block = MasterAgent.blocks.get(blockID)) == null) {//块不存在，需要建立一个新块再转发
                block = new Block(MasterAgent.blockID.getAndIncrement());
                List<Slave> candidates = selectSlaves(MasterAgent.slaves.values());
                //block.addnodes(candidates);

                //进行二次判断，只有在确保块不存在的前提下，才可以修改元数据
                List<String> hostIp = new ArrayList<>();
                if(!MasterAgent.blocks.containsKey(blockID)) {
                    //更新blocks
                    MasterAgent.blocks.put(blockID, block);
                    for (Slave candidate : candidates) {
                        MasterAgent.slaves.get(candidate.getId()).addBlock(blockID);
                        hostIp.add(candidate.getIp());
                    }
                    Slave candiate = candidates.get(0);
                    toHost = candiate.getIp();

                    //向所有存储该块的结点发送新建块的信息
                    for(String ip :hostIp){
                        Message msg = new Message();
                        msg.add(MessagePool.TYPE,MessagePool.ADDBLOCK);
                        msg.add(MessagePool.BLOCKID,Long.toString(blockID));
                    }

                }else{//块已经存在，前期工作作废，走块已经存在的访问逻辑
                    Slave slave = selectSlave(block.getnodes());
                    toHost = slave.getIp();
                }

            } else {
                Slave slave = selectSlave(block.getnodes());
                toHost = slave.getIp();
            }

            msg.add(MessagePool.TOHOST, toHost);
     //       msg.add(MessagePool.KEYID, Long.toString(keyID));

            MasterAgent.queue2.add(msg);

            logger.debug("write request from host" + msg.get(MessagePool.FROMHOST)+"has been processed out!");
        }catch (Exception e){
            logger.error("unexceptional error occur,write request from" +msg.get(MessagePool.FROMHOST)+"has failed!");
            logger.debug(e.getMessage());
        }

    }
}


