/*
author:chxy
data:2020/5/23
description:
*/

import master.handler.MasterWriteHandler;
import miscellaneous.Handler;
import miscellaneous.Message;

import java.util.HashMap;
import java.util.Map;

public class Demo2 {

    public static void main(String[] args) {
        Message msg = new Message();

       Map<String, Handler> map = new HashMap<>();
       map.put("REGISTER",new MasterWriteHandler(msg));
       MasterWriteHandler handler = (MasterWriteHandler)map.get("REGISTER");
       System.out.println(handler);
    }
}


