/*
author:chxy
data:2020/5/7
description:
*/
package other;

import java.util.*;

public class TimerDemo {

    public static void main(String[] args) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("periodicaly print message! and the current timestamp is:\t"+System.currentTimeMillis());
            }
        },1000l,1000l);//进入另外一个线程执行

        Scanner sca = new Scanner(System.in);
        String msg;
        while(!"quit".equals(msg = sca.next()));
        timer.cancel();
        System.out.println("timer has cancelled");

        HashSet<Integer> set = new HashSet<>();

    }
}


