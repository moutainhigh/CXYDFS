/*
author:chxy
data:2020/5/3
description:结点
*/
package master;

public class Slave {

    private  int id;//唯一标识
    private final String type;//结点类型，分为MASTER和SLAVE
    private final String host;//分布的主机ip
    private final int port;//分布的端口


   public Slave(String type){
       this.type = type;
       this.host = "localhost";
       this.port = 1994;
       /*id*/
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
}


