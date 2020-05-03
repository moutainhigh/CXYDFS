package master;/*
author:chxy
data:2020/5/3
description:模拟客户，向master发送message
*/

import java.util.concurrent.BlockingQueue;

public class TmpClient implements Runnable{

    private BlockingQueue<Message> messagesForSlaveManager;
    private BlockingQueue<Message> messagesForAccessManager;

    public TmpClient( BlockingQueue<Message> messagesForSlaveManager,
                      BlockingQueue messagesForAccessManager){
        this.messagesForAccessManager = messagesForAccessManager;
        this.messagesForSlaveManager = messagesForSlaveManager;
    }
    @Override
    public void run() {

        Message message = new Message();
        message.add(Message.TYPE,Message.REGISTER);
        messagesForSlaveManager.add(message);

        message = new Message();
        message.add(Message.TYPE,Message.READ);
        messagesForAccessManager.add(message);
    }
}


