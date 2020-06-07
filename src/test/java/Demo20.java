/*
author:chxy
data:2020/6/4
description:
*/

import java.io.*;
import java.nio.channels.FileChannel;

public class Demo20 {

    public static void main(String[] args) throws IOException {

        File file = new File("E:/data");
        FileChannel channel1 = new FileInputStream(file).getChannel();
        System.out.println(channel1.size());

        FileChannel channel2 = new FileOutputStream(file).getChannel();
        System.out.println(channel2.size());

        String fname = "E:/data";
        FileChannel channel3 = new FileInputStream(fname).getChannel();
        System.out.println(channel3.size());
    }
}


