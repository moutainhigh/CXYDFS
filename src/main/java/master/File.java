/*
author:chxy
data:2020/5/3
description:文件
*/
package master;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/*
* 需要对slaves属性加锁访问：
* slavemanager线程可能访问该属性以确定那个结点应该接受访问请求，
* 同时，另一个执行文件迁移的线程（处理掉线）可能也会更新这个属性
* 但是也可以不加，因为它没有严重的后果，顶多会导致客户端访问了一个已经
* 被移除的结点，访问失败，超时之后重新访问。
*
* */


public class File {

    private final String name;//文件名
    private final int num;//副本数
    private final List<Integer> slaves;//分布的结点
    private AtomicInteger id;//下一个id值

    File(String name,int num){
        this.name = name;
        this.num = num;
        slaves = new ArrayList<>();
        this.id = new AtomicInteger(1);
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public List<Integer> getSlaves() {
        return slaves;
    }

    public AtomicInteger getId() {
        return id;
    }


    public void setId(AtomicInteger id) {
        this.id = id;
    }
}


