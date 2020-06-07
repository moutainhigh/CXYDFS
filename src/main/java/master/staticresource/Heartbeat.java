/*
author:chxy
data:2020/5/19
description:结点心跳
*/
package master.staticresource;

import miscellaneous.Node;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Heartbeat implements Serializable {

    private static final long serialVersionUID = 8291064588438083796L;
    private long lastContact;//上次握手时间,取机器时间
    private long thisContact;//本次握手时间，取机器时间
    private int lost;//失联次数
    //投诉者集，当其他结点无法与该结点取得联系时，
    // 该结点会被投诉，投诉结点的id会存放在本集合,暂时不做
    private final Set<Integer> complaints;

    public Heartbeat() {
        this.thisContact = System.currentTimeMillis();
        this.lastContact = this.thisContact-(long)(Node.TIMEOUT*0.75);
        this.lost = 0;
        this.complaints = new HashSet<>();
    }

    //获取失联次数
    public int getLost() {
        return lost;
    }

    //增加失联次数
    public void addLost(){
        lost++;
    }

    //设置失联次数
    public void setLost(int t){
        this.lost = t;
    }

    //同步方法，更新接触时间
    public synchronized void updateContact(long newVal){

        lastContact = thisContact;
        thisContact = newVal;
    }

    //同步方法，获取接触时间之间的差值
    public synchronized long getDiff(){

        long diff = thisContact-lastContact;
        lastContact = thisContact;
        return diff;
    }

    //新增投诉结点,不重复
    public synchronized void addComplaint(int complaint){
        complaints.add(complaint);
    }

    //获取投诉结点的个数
    public synchronized int getComplaintNums(){
        return complaints.size();
    }

    //测试需要
    @Override
    public String toString() {
        return "Heartbeat{" +
                ", lastContact=" + lastContact +
                ", thisContact=" + thisContact +
                ", lost=" + lost +
                ", complaint=" + complaints +
                '}';
    }


}


