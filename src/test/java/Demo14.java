/*
author:chxy
data:2020/5/29
description:
*/

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Demo14 {

    public static void main(String[] args) throws FileNotFoundException {

        Path FILEPATH = Paths.get("E:","demo");
        Path slavePath = FILEPATH.resolve("slaves");

        FileOutputStream fout = new FileOutputStream(slavePath.toFile());
        System.out.println(slavePath);
    }
}


