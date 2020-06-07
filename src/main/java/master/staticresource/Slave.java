/*
author:chxy
data:2020/5/19
description:数据结点
*/
package master.staticresource;

import miscellaneous.Node;

import java.util.ArrayList;
import java.util.List;

public class Slave extends Node {

    private Heartbeat heartbeat;//结点的心跳信息
    private List<Long> blocks;//结点存储了哪些文件

   //有参构造方法
    public Slave(int id, String type, String ip, int port) {
        super(id, type, ip, port);
        this.heartbeat = new Heartbeat();
        this.blocks = new ArrayList<>();
    }


    //获取心跳，这是一个非同步方法；
    //可能会有多个线程调用此方法，heartbeat数据的一致性，由它自己来维护
    public Heartbeat getHeartbeat() {
        return heartbeat;
    }

    //获取该结点存储了哪些块,互斥访问
    public synchronized List<Long> getBlocks(){
        return blocks;
    }

    //该结点新增一个块,互斥访问
    public synchronized void addBlock(long id){
        blocks.add(id);
    }

    //该结点删除一个块,互斥访问
    public synchronized void removeBlock(int id){
        blocks.remove(id);
    }


    @Override
    public String toString() {
        return "Slave{" +
                ", blocks=" + blocks +
                ", id=" + id +
                ", type='" + type + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}


