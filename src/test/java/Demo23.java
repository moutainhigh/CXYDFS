/*
author:chxy
data:2020/6/4
description:
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;

public class Demo23 {

    public static void main(String[] args) throws FileNotFoundException {

        File file = new File("E:/data");
        System.out.println(file.getUsableSpace());
        System.out.println(file.getFreeSpace());
        System.out.println(file.getTotalSpace());

    }
}


