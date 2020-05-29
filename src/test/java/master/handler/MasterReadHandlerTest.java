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
//        MasterAgent.slaves = new ConcurrentHashMap<>();
//        MasterAgent.slaves.put(0,new Slave(0,"master","123",123));
//        MasterAgent.slaves.put(1,new Slave(1,"master","123",123));
//        MasterAgent.slaves.put(2,new Slave(2,"master","123",123));
//        MasterAgent.slaves.put(3,new Slave(3,"master","123",123));
//        MasterAgent.slaves.put(4,new Slave(4,"master","123",123));
//
//        MasterAgent.blocks = new ConcurrentHashMap<>();
//        Block block = new Block(0l);
//        block.addnodes(Arrays.asList(0,1,2));
//        MasterAgent.blocks.put(0l,block);
//
//        MasterAgent.blocks.put(1l,new Block(1l));
//        MasterAgent.blocks.put(2l,new Block(2l));
//        MasterAgent.blocks.put(3l,new Block(3l));
//        MasterAgent.blocks.put(4l,new Block(4l));
//
//
//        MasterAgent.dataID = new AtomicLong(1l);
//        MasterAgent.blockID = new AtomicLong(5l);
//        MasterAgent.slaveID = new AtomicInteger(5);

        MasterAgent.initialize();

        Message msg = new Message();
        msg.add(MessagePool.TYPE,MessagePool.READ);
        msg.add(MessagePool.DATAID,"500");
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