package master.handler;

import master.agent.MasterAgent;
import master.dispatcher.MasterDispatcher;
import miscellaneous.Dispatcher;
import miscellaneous.Message;
import miscellaneous.MessagePool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MasterWriteHandlerTest {

    @Test
    void run() {
    }

    @Test
    void handle() {

        MasterAgent.initialize();

        Message msg = new Message();
        msg.add(MessagePool.TYPE,MessagePool.WRITE);
        MasterAgent.queue1.add(msg);

        Message msg2 = new Message();
        msg2.add(MessagePool.TYPE,MessagePool.WRITE);
        MasterAgent.queue1.add(msg2);

        Dispatcher dispatcher = MasterDispatcher.getInstance();
        Thread t = new Thread(dispatcher);
        t.start();

        Message msgQuit = new Message();
        msgQuit.add(MessagePool.TYPE,MessagePool.QUIT);
        MasterAgent.queue1.add(msgQuit);
    }
}