/*
author:chxy
data:2020/5/30
description:
*/

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Demo15 {

    public static void main(String[] args) throws IOException {

        Path filePath = Paths.get("E:","demo","demodir","data");
        filePath.toFile().createNewFile();
        RandomAccessFile file = new RandomAccessFile(filePath.toFile(),"rw");

    }
}


