package master.handler;

import master.agent.MasterAgent;
import master.dispatcher.MasterDispatcher;
import master.staticresource.Block;
import master.staticresource.Master;
import miscellaneous.Dispatcher;
import miscellaneous.Message;
import miscellaneous.MessagePool;
import org.junit.jupiter.api.Test;
import slave.Slave;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


class MasterReadHandlerTest {

    @Test
    void run() {


        MasterAgent.initialize();

        Message msg = new Message();
        msg.add(MessagePool.TYPE,MessagePool.READ);
        msg.add(MessagePool.DATAID,"1");
        msg.add(MessagePool.FROMHOST,"127.0.0.1");
        msg.add(MessagePool.TOHOST,"127.0.0.1");
        MasterAgent.queue1.add(msg);

        Dispatcher dispatcher = MasterDispatcher.getInstance();
        Thread t = new Thread(dispatcher);
        t.start();

        Message msgQuit = new Message();
        msgQuit.add(MessagePool.TYPE,MessagePool.QUIT);
        MasterAgent.queue1.add(msgQuit);
    }

    @Test
    void handle() {

    }
}