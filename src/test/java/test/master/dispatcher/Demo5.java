/*
author:chxy
data:2020/5/24
description:
*/
package test.master.dispatcher;

interface In{
    void doSomething();
}

public class Demo5  implements In,Cloneable{

    @Override
    public void doSomething() {

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

        In i = new Demo5();
       // i.clone();

    }
}


