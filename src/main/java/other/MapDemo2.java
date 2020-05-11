/*
author:chxy
data:2020/5/8
description:
*/
package other;

import java.util.concurrent.ConcurrentHashMap;

public class MapDemo2 {

    public static void main(String[] args) {

        ConcurrentHashMap<Integer, Integer> cmap = new ConcurrentHashMap<>();
        MyObj obj2 = new MyObj();
        cmap.put(1, 1);
        System.out.println(cmap);
    }
}


