/*
author:chxy
data:2020/5/29
description:
*/

import java.io.*;

class MyObj implements Serializable {

    int i;
    long l;
    String s;

    public MyObj(int i, long l, String s) {
        this.i = i;
        this.l = l;
        this.s = s;
    }
}
public class Demo13 {

    public static void main(String[] args) throws IOException, ClassNotFoundException {


        ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream("E:/data"));
        MyObj obj = new MyObj(1,2l,"obj");
        oout.writeObject(obj);

        MyObj obj2 = new MyObj(1,2l,"obj2");
        oout.writeObject(obj2);
        MyObj obj3 = new MyObj(1,2l,"obj3");
        oout.writeObject(obj3);

        ObjectInputStream oin = new ObjectInputStream(new FileInputStream("E:/data"));
        MyObj sobj = (MyObj)oin.readObject();
        System.out.println(sobj.s);

        MyObj sobj2 = (MyObj)oin.readObject();
        System.out.println(sobj2.s);

        MyObj sobj3 = (MyObj)oin.readObject();
        System.out.println(sobj3.s);


    }
}


