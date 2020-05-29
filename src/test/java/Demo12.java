/*
author:chxy
data:2020/5/26
description:
*/

import java.io.*;

public class Demo12 {

    public static void main(String[] args) throws IOException {

        FileOutputStream fout = new FileOutputStream(new File("E:/data"));
        BufferedOutputStream bout = new BufferedOutputStream(fout);
        DataOutputStream dout = new DataOutputStream(bout);
        dout.writeByte(1);
        dout.writeInt(2);
        dout.writeLong(3l);
        dout.writeUTF("hello");
        System.out.println(dout.size());
        //如果不手动flush，而且缓冲区未满，数据会一直保留在内存
        dout.flush();

        FileInputStream fin = new FileInputStream(new File("E:/data"));
        BufferedInputStream bin = new BufferedInputStream(fin);
        DataInputStream din = new DataInputStream(bin);
        byte b = din.readByte();
        int i = din.readInt();
        double bb = din.readLong();
        String s = din.readUTF();
        System.out.println(s);
    }
}


