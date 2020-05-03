/*
author:chxy
data:2020/5/3
description:文件
*/
package master;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class File {

    private final String name;//文件名
    private final int num;//副本数
    private List<Slave> slaves;//分布的结点
    private AtomicInteger id;//下一个id值

    File(String name,int num){
        this.name = name;
        this.num = num;
        /*初始化node*/
        this.id = new AtomicInteger(1);

    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public List<Slave> getSlaves() {
        return slaves;
    }

    public AtomicInteger getId() {
        return id;
    }

    public void setSlaves(List<Slave> slaves) {
        this.slaves = slaves;
    }

    public void setId(AtomicInteger id) {
        this.id = id;
    }
}


