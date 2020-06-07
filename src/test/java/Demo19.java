/*
author:chxy
data:2020/6/3
description:
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Demo19 {

    public static void main(String[] args) throws IOException {

        File file = new File("E:/data");

        FileOutputStream fout = new FileOutputStream((file),true);
        String str = "123456789";
        for(int i = 0;i < 1000000;i++)
            fout.write(str.getBytes());

    }
}


