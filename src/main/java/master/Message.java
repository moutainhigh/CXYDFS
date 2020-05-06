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
    public static final String CONNECT = "CONNECT";//建立连接
    public static final String FROMHOST = "FROMHOST";//源主机IP
    public static final String TOHOST = "TOHOST";//目的主机IP

    /*
    * CONNECT:
    * DST/SOUR
    * */


    //从byte流还原Message
    public static Message buildFromStream(byte[] byteStream) {

        return Message.buildFromString(new String(byteStream));
    }

    //把数据变成byte流
    public static byte[] parsetToStream(Message message) {

        return Message.parseToString(message).getBytes();
    }

    //从string还原Message
    public static Message buildFromString(String str) {

        Message msg = new Message();
        String[] kvs = str.split("\t");
        for(String kv:kvs){
            String[] words = kv.split(":");
            msg.body.put(words[0],words[1]);
        }
        return msg;
    }

    //把数据变成string
    public static String parseToString(Message message) {

        StringBuilder result = new StringBuilder();
        for(Map.Entry<String,String> entry:message.body.entrySet()){
            StringBuilder tmp = new StringBuilder();
            tmp.append(entry.getKey())
                    .append(":")
                    .append(entry.getValue());
            result.append(tmp).append("\t");
        }
        return result.substring(0,result.length()-1);
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


