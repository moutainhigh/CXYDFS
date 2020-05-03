/*
author:chxy
data:2020/5/3
description:消息
*/
package master;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private Map<String, String> body = new HashMap<>();

    public static final String TYPE = "TYPE";//消息类型
    public static final String REGISTER = "REGISTER";//注册
    public static final String HEARTBEAT = "HEARTBEAT";//心跳
    public static final String READ = "READ";//读
    public static final String WRITE = "WRITE";//写
    public static final String MOVE = "MOVE";//文件迁移

    //从byte流还原Message
    public Message buildFromStream(byte[] byteStream) {

        return null;
    }

    //把数据变成byte流
    public byte[] parsetToStream(Message message) {
        return null;
    }

    //从string还原Message
    public Message buildFromString(String str) {

        return null;
    }

    //把数据变成string
    public String parseToString(Message message) {

        return null;
    }

    //获取消息
    public String get(String key){
      return body.get(key);
    }

    //添加消息
    public void add(String key,String value){
        body.put(key,value);
    }


}


