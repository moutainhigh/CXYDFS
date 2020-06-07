/*
author:chxy
data:2020/6/7
description:
*/

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Demo28 {

    public static void main(String[] args) throws IOException {

        Path path = Paths.get("E:/","wx");
        FileChannel channel = new FileOutputStream(path.toFile(),false).getChannel();
        channel.position(10l);
        System.out.println(channel.position());
    }
}


