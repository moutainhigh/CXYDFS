/*
author:chxy
data:2020/5/29
description:反序列化工具类
*/
package util;

import master.staticresource.Block;
import slave.Slave;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Deseri {

    //反序列化slaves
    public static ConcurrentHashMap<Integer, Slave> restoreSlaves(File file) throws Exception {

        FileInputStream fin = null;
        ObjectInputStream oin = null;
        try {
            fin = new FileInputStream(file);
            oin = new ObjectInputStream(fin);

            ConcurrentHashMap<Integer, Slave> slaves = new ConcurrentHashMap<>();
            Slave slave = (Slave) oin.readObject();
            while (slave != null) {
                slaves.put(slave.getId(), slave);
                slave = (Slave) oin.readObject();
            }

            return slaves;
        } catch (IOException | ClassNotFoundException e) {
            Exception ae = new Exception("error occur in initializing slaves");
            ae.initCause(e);
            throw ae;
        } finally {
            if (fin != null)
                fin.close();

            if (oin != null)
                oin.close();
        }
    }

    //反序列化blocks
    public static ConcurrentHashMap<Long, Block> restoreBlocks(File file) throws Exception {

        FileInputStream fin = null;
        ObjectInputStream oin = null;
        try {
            fin = new FileInputStream(file);
            oin = new ObjectInputStream(fin);

            ConcurrentHashMap<Long, Block> blocks = new ConcurrentHashMap<>();
            Block block = (Block) oin.readObject();
            while (block != null) {
                blocks.put(block.getID(), block);
                block = (Block) oin.readObject();
            }

            return blocks;
        } catch (IOException | ClassNotFoundException e) {
            Exception ae = new Exception("error occur in initializing slaves");
            ae.initCause(e);
            throw ae;
        } finally {
            if (fin != null)
                fin.close();

            if (oin != null)
                oin.close();
        }
    }

    //反序列化ids,第一个位置存放dataid,第二个位置存放blockid，第三个位置存放slaveid
    public static List<Object> restoreIds(File file) throws Exception {

        FileInputStream fin = null;
        DataInputStream din = null;
        List<Object> ids = new ArrayList<>();
        try {
            fin = new FileInputStream(file);
            din = new DataInputStream(fin);

            long dataid = din.readLong();
            ids.add(new AtomicLong(dataid));
            long blockid = din.readLong();
            ids.add(new AtomicLong(blockid));
            int slaveid = din.readInt();
            ids.add(new AtomicInteger(slaveid));

        } catch (IOException e) {
            Exception ae = new Exception("error occur in initializing slaves");
            ae.initCause(e);
            throw ae;
        } finally {
            if (fin != null)
                fin.close();
            if (din != null)
                din.close();
        }
        return ids;
    }


    }



