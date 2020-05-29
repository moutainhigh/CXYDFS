import miscellaneous.Message;

import java.util.HashMap;
import java.util.Map;

/*
author:chxy
data:2020/5/24
description:
*/
abstract class demo implements Cloneable{

    protected Message msg;

    public abstract void handle();

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
class Demo7 extends demo{


    public Demo7(Message msg) {
        this.msg = msg;
    }

    public void handle(){
        System.out.println(msg);
    }
}


class Demo8 extends demo{


    public Demo8(Message msg) {
        this.msg = msg;
    }

    public void handle(){
        System.out.println(msg);
    }
}

class Demo9 {
    public static void main(String[] args) throws CloneNotSupportedException {

//        demo d = new Demo7("demo7");
//        Demo7 d2 = (Demo7)d.clone();
//        System.out.println(d2.name);
//
//        demo d3 = new Demo8("demo8");
//        Demo8 d4 = (Demo8)d3.clone();
//        System.out.println(d4.name);

        Map<String,demo> map = new HashMap<>();
        map.put("demo7",new Demo7(null));
        map.put("demo8",new Demo8(null));

        demo d7 = (demo)map.get("demo7").clone();
        Message msg = new Message();
        msg.add("key","demo7");
        d7.msg = msg;


        demo d8 = (demo)map.get("demo8").clone();
        Message msg2 = new Message();
        msg2.add("key","demo8");
        d8.msg = msg2;

        demo d7_1 = (demo)map.get("demo7").clone();
        Message msg3 = new Message();
        msg3.add("key","demo7_1");
        d7_1.msg = msg3;

        d7.handle();;
        d8.handle();
        d7_1.handle();
    }
}


