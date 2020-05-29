package test.master.dispatcher;


import master.agent.MasterAgent;
import master.dispatcher.MasterDispatcher;
import miscellaneous.Dispatcher;
import miscellaneous.Message;
import miscellaneous.MessagePool;

class MasterDispatcherTest {

    @org.junit.jupiter.api.Test
    void initialize() {
    }

    @org.junit.jupiter.api.Test
    void dispatch() {

        Dispatcher dispatcher = MasterDispatcher.getInstance();
        Message msgRead = new Message();
        msgRead.add(MessagePool.TYPE,MessagePool.READ);
        Message msgWrite = new Message();
        msgWrite.add(MessagePool.TYPE,MessagePool.WRITE);
        Message msgHeartbeat = new Message();
        msgHeartbeat.add(MessagePool.TYPE,MessagePool.HEARTBEAT);
        Message msgRegister = new Message();
        msgRegister.add(MessagePool.TYPE,MessagePool.REGISTER);
        Message msgQuit = new Message();
        msgQuit.add(MessagePool.TYPE,MessagePool.QUIT);
        MasterAgent.queue1.add(msgRead);
        MasterAgent.queue1.add(msgWrite);
        MasterAgent.queue1.add(msgHeartbeat);
        MasterAgent.queue1.add(msgRegister);

        Message msgRegister2 = new Message();
        msgRegister2.add(MessagePool.TYPE,MessagePool.REGISTER);
        msgRegister2.add(MessagePool.SLAVEID,"01");
        MasterAgent.queue1.add(msgRegister2);

        MasterAgent.queue1.add(msgQuit);


        Thread t = new Thread(dispatcher);
        t.start();
    }

    @org.junit.jupiter.api.Test
    void dispatch2() {

        Dispatcher dispatcher = MasterDispatcher.getInstance();

        Message msgRegister = new Message();
        msgRegister.add(MessagePool.TYPE,MessagePool.REGISTER);
        Message msgQuit = new Message();
        msgQuit.add(MessagePool.TYPE,MessagePool.QUIT);
        MasterAgent.queue1.add(msgRegister);
        MasterAgent.queue1.add(msgQuit);

        Thread t = new Thread(dispatcher);
        t.start();
    }

}