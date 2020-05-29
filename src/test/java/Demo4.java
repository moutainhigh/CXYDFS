/*
author:chxy
data:2020/5/23
description:
验证：如果这个msg它本身指向一个确切值，在执行克隆的时候，是否需要深拷贝
需要，如果是浅拷贝，三个对象指向的还是同一个值
*/

import miscellaneous.Message;

public class Demo4 implements Cloneable {

    private Message msg;

    public Demo4(Message msg) {
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

        Demo4 demo = new Demo4(new Message());
        Demo4 clone = (Demo4)demo.clone();
        clone.msg.add("clone","clone");

        Demo4 clone2 = (Demo4)demo.clone();
        clone2.msg.add("clone2","clone2");

        System.out.println(demo.msg == null);
        System.out.println(clone.msg);
        System.out.println(clone2.msg);
    }
}


