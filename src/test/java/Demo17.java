/*
author:chxy
data:2020/6/2
description:
*/

import java.nio.ByteBuffer;

public class Demo17 {

    private static ByteBuffer buffer = ByteBuffer.allocate(8);
    //byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static void main(String[] args) {

        //测试 long 转 byte 数组
        long long1 = 2223;
        byte[] bytesLong = longToBytes(long1);
        System.out.println("bytes=" + bytesLong);//bytes=[B@c17164
        //测试 byte 数组 转 long
        long long2 = bytesToLong(bytesLong);
        System.out.println("long2=" + long2);//long2=2223

    }
}


