/*
author:chxy
data:2020/5/19
description:消息
*/
package miscellaneous;

import master.staticresource.Slave;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message implements Cloneable {

    private Map<String, String> body = new HashMap<>();

    public static final String DELIMITERWITHINVALUE = ",";//v内的分隔符
    public static final String DELIMITERWITHKV = "\t";//k-v之间的分隔符
    public static final String DELIMITERWITHINKV = ":";//k-v之内的分隔符

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

    //将fellow信息转换成字符串
    public static String fellowTransform(List<Slave> fellows){

        StringBuilder s = new StringBuilder();
        for(Slave fellow:fellows){
            s.append(Integer.toString(fellow.getId()));
            s.append(DELIMITERWITHINVALUE);
        }
        return s.substring(0,s.length()-1);
    }

    @Override
    public String toString() {
        return "Message{" +
                "body=" + body +
                '}';
    }

    //互换message的 tohost 和 fromhost
    public static void reverseHost(Message msg){

        String holder = msg.get(MessagePool.FROMHOST);
        msg.add(MessagePool.FROMHOST,msg.get(MessagePool.TOHOST));
        msg.add(MessagePool.TOHOST,holder);
    }

    //将str还原成complaint
    public static String[] strToComplaint(String str){
        if(str == null)
            return new String[0];
        return str.split(Message.DELIMITERWITHINVALUE);
    }
}


