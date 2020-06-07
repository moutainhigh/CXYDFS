/*
author:chxy
data:2020/5/29
description:序列化工具类
*/
package util;

import master.staticresource.Block;
import master.staticresource.Slave;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SeriUtil {

    //应保证在执行序列化的时候，没有其他线程正在修改slaves
    public static void saveSlaves(File file, ConcurrentHashMap<Integer, Slave> slaves) throws Exception {

        FileOutputStream fout = null;
        ObjectOutputStream oout = null;
        try {
            fout = new FileOutputStream(file, false);//每次写入会覆盖之前的内容
            oout = new ObjectOutputStream(fout);

            for (Map.Entry<Integer, Slave> entry : slaves.entrySet()) {
                Slave slave = entry.getValue();
                oout.writeObject(slave);
            }

            //写入一个null，作为文件结束标志
            oout.writeObject(null);
        }catch(IOException e){
            Exception ae = new Exception("error occur in saving slaves");
            ae.initCause(e);
            throw ae;
        }finally{
            if(fout != null)
                fout.close();
            if(oout != null)
                oout.close();
        }
    }

    //应保证在执行序列化的时候，没有其他线程正在修改blocks
    public static void saveBlocks(File file, ConcurrentHashMap<Long, Block> blocks) throws Exception {

        FileOutputStream fout = null;
        ObjectOutputStream oout = null;
        try {
            fout = new FileOutputStream(file, false);//每次写入会覆盖之前的内容
            oout = new ObjectOutputStream(fout);
            for (Map.Entry<Long, Block> entry : blocks.entrySet()) {
                Block block = entry.getValue();
                oout.writeObject(block);
            }

            //写入一个null，作为文件结束标志
            oout.writeObject(null);
        }catch(IOException e){
            Exception ae = new Exception("error occur in saving blocks");
            ae.initCause(e);
            throw ae;
        }finally{
            if(fout != null)
                fout.close();
            if(oout != null)
                oout.close();
        }
    }

    //应保证在执行序列化的时候，没有其他线程正在修改ids
    //序列化ids,第一个位置存放dataid,第二个位置存放blockid，第三个位置存放slaveid
    public static void saveIDs(File file, List<Object> ids) throws Exception {

        FileOutputStream fout = null;
        DataOutputStream dout = null;
        try {
           fout = new FileOutputStream(file,false);
           dout = new DataOutputStream(fout);

            dout.writeLong(((AtomicLong)ids.get(0)).get());
            dout.writeLong(((AtomicLong)ids.get(1)).get());
            dout.writeInt(((AtomicInteger)ids.get(2)).get());
        }catch(IOException e){
            Exception ae = new Exception("error occur in saving ids");
            ae.initCause(e);
            throw ae;
        }finally {
            if(fout != null)
                fout.close();
            if(dout != null)
                dout.close();
        }
    }



}


