/*
author:chxy
data:2020/5/23
description:
验证：如果这个msg它本身是null，不指向任何值，在执行克隆的时候，是否需要深拷贝，
不需要
*/

import miscellaneous.Message;


public class Demo3 implements Cloneable{

    private Message msg;

    public Demo3(Message msg) {
        this.msg = msg;
    }

    @Override
    public Object clone(){
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        Demo3 demo = new Demo3(null);
        Demo3 clone = (Demo3)demo.clone();
        clone.msg = new Message();
        clone.msg.add("clone","clone");

        Demo3 clone2 = (Demo3)demo.clone();
        clone2.msg = new Message();
        clone2.msg.add("clone2","clone2");

        System.out.println(demo.msg == null);
        System.out.println(clone.msg);
        System.out.println(clone2.msg);
    }

}


