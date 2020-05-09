/*
author:chxy
data:2020/5/3
description:结点
*/
package master;

import network.NetworkHandle;

public class Slave {

    private int id;//唯一标识
    private final String type;//结点类型，分为MASTER和SLAVE
    private final String host;//分布的主机ip
    private final int port;//分布的端口
    private final Heartbeat heartbeat;//心跳信息

  //用于从结点的初始化
   public Slave(String type,String host){
       this.type = type;
       this.host = host;
       this.port = NetworkHandle.SLAVEPORT;
       this.id = -1;//未分配状态
       this.heartbeat = null;
   }

   //用于主结点的初始化
    public Slave(int id, String type, String host, Heartbeat heartbeat){
        this.id = id;
        this.type = type;
        this.host = host;
        this.heartbeat = heartbeat;
        this.port = NetworkHandle.SLAVEPORT;
    }

   /*启动slave结点：
   * 1.启动通讯线程
   * 2.向master注册
   * 3.
   *
   * */


    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    //测试需要
    public String toString() {
        return "Slave{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", heartbeat=" + heartbeat +
                '}';
    }
}


