/*
author:chxy
data:2020/5/21
description:
*/

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Demo1 {

    public static void func(Collection<Integer> list){

    }

    public static void main(String[] args) {

        ConcurrentHashMap<Integer,Integer> map = new ConcurrentHashMap<>();
        func(map.values());
    }
}


