/*
author:chxy
data:2020/6/7
description:
*/

class DemoThread extends Thread{

    @Override
    public void run() {
        while(true){

        }
    }
}
public class Demo26 {

    public static void main(String[] args) {

        DemoThread demo1 = new DemoThread();
        demo1.setName("demo1");
        demo1.start();

        DemoThread demo2 = new DemoThread();
        demo1.setName("demo2");
        demo2.start();
    }
}


