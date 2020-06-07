/*
author:chxy
data:2020/5/23
description:
*/
package master.handler;

import master.agent.MasterAgent;
import miscellaneous.AbstractHandler;
import miscellaneous.Message;
import miscellaneous.MessagePool;
import miscellaneous.Node;
import org.apache.log4j.Logger;
import master.staticresource.Heartbeat;
import master.staticresource.Slave;

public class MasterHeartbeatHandler extends AbstractHandler {

    private static Logger logger = Logger.getLogger(MasterHeartbeatHandler.class);

    public MasterHeartbeatHandler(Message msg) {
        this.msg = msg;
    }

    @Override
    public void run() {

        handle();
    }

    @Override
    public void handle() {

        logger.debug("MasterHeartbeatHandler is processing msg!");
        int slaveID = Integer.parseInt(msg.get(MessagePool.SLAVEID));
        long timestamp = Long.parseLong(msg.get(MessagePool.TIMESTAMP));
        String[] complaints = Message.strToComplaint(msg.get(MessagePool.COMPLAINTS));
        Slave slave = MasterAgent.slaves.get(slaveID);
        Heartbeat heartbeat = slave.getHeartbeat();

        //更新时间戳
        heartbeat.updateContact(timestamp);

    }

    public static void scanHeartbeat(){

        for (Slave slave : MasterAgent.slaves.values()) {
            boolean isTimeout = false;
            Heartbeat beat = slave.getHeartbeat();
            //首先检查心跳
            long span = beat.getDiff();
            if (span == 0 || span > Node.TIMEOUT) {//超时处理,等于0说明没有更新过
                int lost = beat.getLost() + 1;
                beat.setLost(lost);
                if (lost >= 3) {
                    isTimeout = true;
                }
            } else {//未超时处理
                beat.setLost(0);
            }

            //决定是否做踢出处理
            if (isTimeout) {
                logger.info("host:\t" + slave.getId() + "has been kicked out!");
            }
        }
    }
}


