/*
author:chxy
data:2020/6/4
description:
*/

import java.io.*;

public class Demo22 {

    public static void main(String[] args) throws IOException {

        File file = new File("E:/r");

//        FileOutputStream out = new FileOutputStream(file);
//        String s = "abcdefgh";
//        out.write(s.getBytes());
        byte[] bytes = new byte[6];
        RandomAccessFile rfile = new RandomAccessFile(file,"rw");
        //rfile.seek(4);
        rfile.read(bytes);
        String s = new String(bytes);
        System.out.println(s);


    }
}


