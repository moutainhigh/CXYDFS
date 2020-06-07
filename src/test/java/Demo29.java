/*
author:chxy
data:2020/6/7
description:
*/


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileBlockWriter2 extends Thread{

    Path file;
    long start;
    long length;
    String s;

    public FileBlockWriter2(Path path,long start, long length,String s) {

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
            FileChannel channel = new RandomAccessFile(file.toFile(),"rw").getChannel();
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

public class Demo29 {

    public static void main(String[] args) {


        Path path = Paths.get("E:/", "w");
        FileBlockWriter2 writer = new FileBlockWriter2(path, 30l, 10, "aaaaaaaaaa");
        writer.start();

        FileBlockWriter2 writer2 = new FileBlockWriter2(path, 40l, 10, "bbbbbbbbbb");
        writer2.start();

        FileBlockWriter2 writer3 = new FileBlockWriter2(path, 50l, 10, "cccccccccc");
        writer3.start();
    }
}


