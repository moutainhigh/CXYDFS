/*
author:chxy
data:2020/5/7
description:
*/
package other;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MyObj{

    private int i = 1;
    private int j = i+10;

    public synchronized void show(){
        System.out.println(i+"\t"+j);
    }

    public synchronized int getDiff() {
        return j-i;
    }

    public synchronized void update(){

        i = j;
        j += 10;
    }
}

class TestThread extends Thread{

    public ConcurrentHashMap<Integer, MyObj> cmap;

    public TestThread(ConcurrentHashMap<Integer,MyObj> map){
        this.cmap = map;
    }

    @Override
    public void run() {

        for(int i = 0;i < 50000;i++){
            MyObj myObj = cmap.get(1);
            myObj.update();
        }
    }
}

public class MapDemo {

    public static void main(String[] args) {

        ConcurrentHashMap<Integer, MyObj> cmap = new ConcurrentHashMap<>();
        MyObj obj2 = new MyObj();
        cmap.put(1, obj2);

        Thread t = new TestThread(cmap);
        t.start();

        for(int k = 0;k < 500;k++) {

            for (Map.Entry<Integer, MyObj> entry : cmap.entrySet()) {
                MyObj value = entry.getValue();
                value.show();
                System.out.println(value.getDiff());
                System.out.println("------");
            }
        }
    }
}


