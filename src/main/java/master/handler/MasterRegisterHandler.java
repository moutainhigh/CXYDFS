/*
author:chxy
data:2020/5/23
description:
*/
package master.handler;

import master.agent.MasterAgent;
import master.network.NetworkHelper;
import miscellaneous.AbstractHandler;
import miscellaneous.Message;
import miscellaneous.MessagePool;
import miscellaneous.Node;
import org.apache.log4j.Logger;
import slave.Slave;


public class MasterRegisterHandler extends AbstractHandler {

    private static Logger logger = Logger.getLogger(MasterRegisterHandler.class);

    public MasterRegisterHandler(Message msg) {
        this.msg = msg;
    }

    @Override
    public void handle() {

        logger.debug("MasterRegisterHandler is processing msg and the message is:" + msg);

        try {

            //解析消息，判断注册的阶段,"这里假定消息格式正确，不处理由于消息格式导致的异常"
            int phase = Integer.parseInt(msg.get(MessagePool.PHASE));
            if (phase == 1) {//注册的第一阶段，授予id
                msg.add(MessagePool.SLAVEID, Integer.toString(MasterAgent.slaveID.getAndDecrement()));

            } else if (phase == 3) {//注册的第三阶段，更新内部文件,slaves,slaveToFiles
                int id = Integer.parseInt(msg.get(MessagePool.SLAVEID));
                if (!MasterAgent.slaves.containsKey(id)) {//如果出现了重复的注册，则说明报文4丢失，忽略
                    String hostIP = msg.get(MessagePool.FROMHOST);
                    Slave slave = new Slave(id, Node.SLAVE, hostIP, NetworkHelper.SLAVEPORT);
                    MasterAgent.slaves.put(id, slave);
                }
            }

            //共有操作：phase+1,tohost与fromhost调转
            msg.add(MessagePool.PHASE, Integer.toString(phase + 1));
            Message.reverseHost(msg);

            MasterAgent.queue2.add(msg);
        }catch (Exception e){
            logger.error("unexceptional error occur in register handle!");
            e.printStackTrace();
        }

       logger.debug("message "+msg+" has been processed out and now the slaves is \n"+MasterAgent.slaves+"\n" +
               "the message is "+msg);

    }

    @Override
    public void run() {
        handle();
    }


}




