package miscellaneous;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public abstract class Dispatcher implements Runnable{

    protected static Map<String,AbstractHandler> map = new HashMap<>();

    //如何初始化map对象，map对象决定了调度的方式选择
    public abstract void initialize();

    //分发消息
    public abstract void dispatch();
}
