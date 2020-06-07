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


public class Demo24 {


    public static void main(String[] args) throws IOException {

        Path path = Paths.get("E:","alphabet");
        System.out.println(path.toFile().length());

        System.out.println("----");

        ByteBuffer buffer = ByteBuffer.allocate(10);
        byte[] bytes = new byte[10];
        FileChannel channel = new FileInputStream(path.toFile()).getChannel();
        System.out.println(channel.position());
        channel.read(buffer);
        buffer.flip();
        buffer.get(bytes);
        System.out.println(new String(bytes));
        System.out.println(channel.position());
        FileChannel channel2 = new FileInputStream(path.toFile()).getChannel();

        System.out.println(channel2.position());
    }
}


