/*
author:chxy
data:2020/5/19
description:常用消息
*/
package miscellaneous;

public class MessagePool {

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

    public static final String DATAID = "DATAID";//要访问的数据id

    public static final String BLOCKID = "BLOCKID";

    public static final String FELLOW = "FELLOW";//同事结点，即结点之间，同时存储了一份文件

    public static final String RANGE = "RANGE";//访问数据的id的上下限-读

    public static final String AMOUNT = "AMOUNT";//访问数据的条数-写

    public static final String ADDBLOCK = "ADDBLOCK";//增加新块

    public static final String QUIT = "QUIT";//终止信号


}


