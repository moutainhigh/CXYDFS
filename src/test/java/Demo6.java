/*
author:chxy
data:2020/5/24
description:
*/

import miscellaneous.Message;

import java.util.HashMap;
import java.util.Map;

public class Demo6 {

    Message msg;

    public Demo6(Message msg) {
        this.msg = msg;
    }

    public static void main(String[] args) {

        Map<String,Demo6> map = new HashMap<>();
        map.put("a",new Demo6(null));
        Demo6 demo = map.get("a");
        demo.msg = new Message();

        Demo6 demo2 = map.get("a");
        System.out.println(demo2.msg == null);

    }
}


