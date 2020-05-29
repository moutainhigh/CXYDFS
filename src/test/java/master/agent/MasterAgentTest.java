package master.agent;

import master.staticresource.Block;
import org.junit.jupiter.api.Test;
import slave.Slave;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class MasterAgentTest {

    @Test
    void initializeTest() {
        MasterAgent.slaves = new ConcurrentHashMap<>();
        MasterAgent.slaves.put(1,new Slave(1,"master","123",123));
        MasterAgent.slaves.put(2,new Slave(2,"master","123",123));
        MasterAgent.slaves.put(3,new Slave(3,"master","123",123));
        MasterAgent.slaves.put(4,new Slave(4,"master","123",123));
        MasterAgent.slaves.put(5,new Slave(5,"master","123",123));

        MasterAgent.blocks = new ConcurrentHashMap<>();
        MasterAgent.blocks.put(1l,new Block(1l));
        MasterAgent.blocks.put(2l,new Block(2l));
        MasterAgent.blocks.put(3l,new Block(3l));
        MasterAgent.blocks.put(4l,new Block(4l));
        MasterAgent.blocks.put(5l,new Block(5l));

        MasterAgent.dataID = new AtomicLong(1l);
        MasterAgent.blockID = new AtomicLong(5l);
        MasterAgent.slaveID = new AtomicInteger(5);

//        MasterAgent.cleanup();
//
//        MasterAgent.initialize();
//        System.out.println(MasterAgent.slaves);
//        System.out.println(MasterAgent.blocks);
//        System.out.println(MasterAgent.dataID.get());
//        System.out.println(MasterAgent.blockID.get());
//        System.out.println(MasterAgent.slaveID.get());
    }
}