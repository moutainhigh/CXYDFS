/*
author:chxy
data:2020/6/7
description:
*/

import java.nio.file.Path;
import java.nio.file.Paths;

public class Demo30 {

    public static void main(String[] args) {

        Path path = Paths.get("E:/", "w");
        FileBlockWriter2 writer = new FileBlockWriter2(path, 60l, 10, "aaaaaaaaaa");
        writer.start();

        FileBlockWriter2 writer2 = new FileBlockWriter2(path, 70l, 10, "bbbbbbbbbb");
        writer2.start();

        FileBlockWriter2 writer3 = new FileBlockWriter2(path, 80l, 10, "cccccccccc");
        writer3.start();

        FileBlockReader reader = new FileBlockReader(path,0l,10l);
        FileBlockReader reader2 = new FileBlockReader(path,10l,10l);
        reader.start();
        reader2.start();
    }
}


