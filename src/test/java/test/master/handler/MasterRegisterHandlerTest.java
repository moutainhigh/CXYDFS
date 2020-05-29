package test.master.handler;

import master.agent.MasterAgent;
import master.dispatcher.MasterDispatcher;
import miscellaneous.Dispatcher;
import miscellaneous.Message;
import miscellaneous.MessagePool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MasterRegisterHandlerTest {

    @Test
    void handle() {
    }

    @Test
    void run() {

        Message msgRegister = new Message();
        msgRegister.add(MessagePool.TYPE,MessagePool.REGISTER);
        msgRegister.add(MessagePool.PHASE,"3");
        msgRegister.add(MessagePool.TOHOST,"tohost");
        msgRegister.add(MessagePool.FROMHOST,"fromhost");
        msgRegister.add(MessagePool.SLAVEID,"0");

        MasterAgent.queue1.add(msgRegister);

        Message msgQuit = new Message();
        msgQuit.add(MessagePool.TYPE,MessagePool.QUIT);
        MasterAgent.queue1.add(msgQuit);

        Dispatcher dispatcher = MasterDispatcher.getInstance();
        Thread t = new Thread(dispatcher);
        t.start();
    }
}