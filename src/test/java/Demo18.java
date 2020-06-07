/*
author:chxy
data:2020/6/2
description:
*/

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Demo18 {

    public static void main(String[] args) {

        ReadWriteLock lock = new ReentrantReadWriteLock();
        final Lock readLock = lock.readLock();
        final Lock writeLock = lock.writeLock();
        final Lock readLock2 = lock.readLock();
    }
}


