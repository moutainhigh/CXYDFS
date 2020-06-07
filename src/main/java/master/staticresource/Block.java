/*
author:chxy
data:2020/5/19
description:数据块
*/
package master.staticresource;

import master.agent.MasterAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
* 这里有必要讨论一下：对nodes的并发访问可能造成的问题
* 访问的用户有：读数据请求，写数据请求，文件迁移的请求
* 前两种请求只会读nodes，所以不应该加锁
* 后一种请求会改变nodes的状态，
* 读写数据请求会与文件迁移请求发生碰撞：
* 请求读一个数据，去访问该node，在分配的时候，node还是存在于nodes，真正访问的时候，已经不存在了，
* 客户端会等待并超时；
* 写请求发生的后果同读；
*
* */

public class Block implements Serializable {

    private static final long serialVersionUID = -531864731668340684L;
    private final long id;//块id,基本信息
    private List<Integer> nodes;//分布的结点

    public Block(long id) {
        this.id = id;
        this.nodes = new ArrayList<>();
    }

    //获取id
    public long getID(){
        return id;
    }

    //获取nodes，它被多个线程同时访问，从理论上来讲，应该要加入互斥访问控制，
    //但是，仅仅加入synchronized,不仅不能保障数据的一致性，而且多个线程要求读的时候，会降低效率，
    //多个线程读请求，往往又是频率最高的，比如处理处理客户端的读写请求
    //综合考虑，不加锁
    public List<Integer> getnodes(){
        return nodes;
    }

    //增加一个node,互斥访问
    public synchronized void  addnode(int node){
        nodes.add(node);
    }

    //增加一批node，互斥访问
    public synchronized void addnodes(List<Integer> others){
       nodes.addAll(others);
    }

    //删除一个node
    public synchronized void removeNode(Slave node){
        nodes.remove(node);
    }

    //根据keyID 计算出 blockID
    public static int getBlockID(long keyID){

        int id = (int)(keyID/MasterAgent.NUMSPERBLOCK);
        if(keyID%MasterAgent.NUMSPERBLOCK == 0)id -= 1;
        return id;
    }

    @Override
    public String toString() {
        return "Block{" +
                "id=" + id +
                ", nodes=" + nodes +
                '}';
    }
}


