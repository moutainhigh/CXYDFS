/*
author:chxy
data:2020/6/7
description:
*/

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileBlockWriter extends Thread{

    Path file;
    long start;
    long length;
    String s;

    public FileBlockWriter(Path path,long start, long length,String s) {

        this.file = path;
        this.start = start;
        this.length = length;
        this.s = s;
    }


    @Override
    public void run() {

        try {
            ByteBuffer buffer = ByteBuffer.allocate((int)length);
            byte[] bytes = s.getBytes();
            buffer.put(bytes);
            buffer.flip();
            FileChannel channel = new FileOutputStream(file.toFile(),false).getChannel();
            System.out.println(Thread.currentThread().getName()+"position before:"+channel.position());
            channel.position(start);
            channel.write(buffer);
            System.out.println(Thread.currentThread().getName()+"position after:"+channel.position());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
public class Demo27 {

    public static void main(String[] args) {

        Path path = Paths.get("E:/","w");
        FileBlockWriter writer = new FileBlockWriter(path,10l,10,"klmnopqrst");
        writer.start();

        FileBlockWriter writer2 = new FileBlockWriter(path,0l,10,"abcdefghij");
        writer2.start();

        FileBlockWriter writer3 = new FileBlockWriter(path,20l,10,"1234567890");
        writer3.start();
    }
}


