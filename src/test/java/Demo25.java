/*
author:chxy
data:2020/6/5
description:
*/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileBlockReader extends Thread{

    Path file;
    long start;
    long length;

    public FileBlockReader(Path path,long start, long length) {

        this.file = path;
        this.start = start;
        this.length = length;
    }


    @Override
    public void run() {

        try {
            ByteBuffer buffer = ByteBuffer.allocate((int)length);
            byte[] bytes = new byte[(int)length];
            FileChannel channel = new FileInputStream(file.toFile()).getChannel();
            channel.position(start);
            channel.read(buffer);
            buffer.flip();
            buffer.get(bytes);
            System.out.println(Thread.currentThread().getName()+":\t"+new String(bytes)+"\t"+channel.position());
            System.out.println();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


public class Demo25 {

    public static void main(String[] args) {

        Path path = Paths.get("E:","w");
        FileBlockReader reader = new FileBlockReader(path,0l,10l);
        FileBlockReader reader2 = new FileBlockReader(path,10l,10l);
        reader.setName("reader1");
        reader2.setName("reader2");
        reader.start();
        reader2.start();
    }
}


