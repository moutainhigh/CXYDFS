/*
author:chxy
data:2020/5/23
description:
*/
package master.dispatcher;

import master.agent.MasterAgent;
import master.handler.MasterHeartbeatHandler;
import master.handler.MasterReadHandler;
import master.handler.MasterRegisterHandler;
import master.handler.MasterWriteHandler;
import miscellaneous.*;
import org.apache.log4j.Logger;

public class MasterDispatcher extends Dispatcher {


    private static Logger logger = Logger.getLogger(MasterDispatcher.class);

    //构造函数私有，单例模式
   private MasterDispatcher(){
       initialize();
   }

    //利用静态内部类实现单例
   private static class DispatcherHolder{
       private static MasterDispatcher sigletonInstance = new MasterDispatcher();
   }

   //提供单例对象的接口
   public static Dispatcher getInstance(){
       return DispatcherHolder.sigletonInstance;
   }



    @Override
    //初始化map对象
    public void initialize() {
        map.put(MessagePool.READ,new MasterReadHandler(null));
        map.put(MessagePool.WRITE,new MasterWriteHandler(null));
        map.put(MessagePool.HEARTBEAT,new MasterHeartbeatHandler(null));
        map.put(MessagePool.REGISTER,new MasterRegisterHandler(null));
    }

    @Override
    //分发消息到不同的handler，将handler投入到线程池
    public void dispatch() {

       while(true) {
           try {
               Message msg = MasterAgent.queue1.take();
               String type = msg.get(MessagePool.TYPE);

               if(MessagePool.QUIT.equals(type))
                   break;

               //这里假定，消息类型只有五种：READ,WRITE,HEARTEBAT,REGISTER,QUIT,不存在第六种
               AbstractHandler handler = (AbstractHandler) map.get(type).clone();
               handler.setMsg(msg);
               MasterAgent.engine.submit(handler);

           } catch (InterruptedException e) {
               logger.error("error occur in dispatcher!");
               System.out.println(e.getMessage());
           }
       }
      logger.info(" master dispatcher has been stop!");
    }

    @Override
    public void run() {
       dispatch();
    }

}


