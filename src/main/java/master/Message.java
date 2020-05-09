/*
author:chxy
data:2020/5/3
description:消息
*/
package master;

import java.util.*;

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

    public static final String PHASE = "PHASE";//注册的阶段

    public static final String SLAVEID = "SLAVEID";//slave id

    public static final String TIMESTAMP = "TIMESTAMP";//slave id

    public static final String COMPLAINTS = "COMPLAINTS";//slave id,被投诉结点

    public static final String FILE = "FILE";//要访问的文件

    public static final String FELLOW = "FELLOW";//同事结点，即结点之间，同时存储了一份文件

    public static final String RANGE = "RANGE";//访问数据的id的上下限-读

    public static final String AMOUNT = "AMOUNT";//访问数据的条数-写



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

    //重载，将fellow信息转换成字符串
    public static String fellowTransform(List<Integer> ids,int useless){
        StringBuilder s = new StringBuilder();
        for(int id:ids){
            s.append(Integer.toString(id));
            s.append(DELIMITERWITHINVALUE);
        }
        return s.substring(0,s.length()-1);
    }

    //根据str解析上、下限
    public static int[] strToRange(String str){

        long l = Long.parseLong(str);
        //无符号右移32位得到下限
        int left = (int)l >>> 32;
        //左移32位再右移32位得到上限
        int right = (int)((l << 32)>>>32);
        return new int[]{left,right};
    }

    //根据上、下限合成str
    public static String rangeToStr(int low,int high){
        //下限左移32位再与上限相与
        long left = (long)low;
        long right = (long)high;
        return Long.toString(((left << 32)|right));
    }

    //热点代码抽取，调换fromhost与tohost
    public static void reverseHost(Message message){
        String tmp = message.get(Message.FROMHOST);
        message.add(Message.FROMHOST,message.get(Message.TOHOST));
        message.add(Message.TOHOST,tmp);
    }

    //将str还原成complaint
    public static String[] strToComplaint(String str){
        if(str == null)
            return new String[0];
        return str.split(Message.DELIMITERWITHINVALUE);
    }


}


