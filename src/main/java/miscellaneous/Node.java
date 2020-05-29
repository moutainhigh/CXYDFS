/*
author:chxy
data:2020/5/19
description:抽象结点
*/
package miscellaneous;

import java.io.Serializable;

public abstract class Node implements Serializable {

    private static final long serialVersionUID = 5919843890561794707L;
    protected final int id;//结点唯一标识
    protected final String type;//结点类型，分为MASTER,SLAVE,HOST
    protected final String ip;//分布的主机ip
    protected final int port;//通信端口（用于消息）

    public static final String MASTER = "MASTER";//主结点类型
    public static final String SLAVE = "SLAVE";//从结点类型
    public static final String HOST = "HOST";//host类型

    public static final long TIMEOUT = 1000l*30l;//心跳超时参数设置
    public static final int TIMEOUTNUMS = 2;//心跳超时3次则踢出
    public static final long COMPLAINTSNUMS = 2;//被两个及以上的结点投诉则踢出

    //构造函数
    public Node(int id, String type, String ip, int port) {
        this.id = id;
        this.type = type;
        this.ip = ip;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}


