/*
author:chxy
data:2020/6/1
description:
*/
package slave.staticresource;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileCollection {

    //id生成器，文件名是一个整数转八字节字符串
    private final AtomicInteger fileID;

    //block的元数据，存储dataid到块的映射，到块中位置的映射
    private final RandomAccessFile metaFile;

    //数据块
    private final List<DataRecord> dataFiles;

    //元数据和数据块存放的根路径
    private final Path rootPath;

    //读写锁
    private ReadWriteLock lock;
    private Lock readLock = null;
    private Lock writeLock = null;


    //通过文件存储的根路径来构造filecollection
    public FileCollection(Path rootPath) throws IOException {
        this.rootPath = rootPath;
        Path metaPath = rootPath.resolve(DataPath.METAPATH);
        metaPath.toFile().mkdirs();

        this.fileID = new AtomicInteger(0);
        this.metaFile = new RandomAccessFile(metaPath.resolve("meta").toFile(),"rw");
        //默认初始时有个新块
        this.dataFiles = new ArrayList<>();
        dataFiles.add(new DataRecord(null,null));

        this.lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    //读取数据，
    //实际的数据读取操作通过封装一个task外包出去了
    public static void read(MetaRecord record){

    }

    //写入数据
    public static void write(MetaRecord record){

    }

    //根据数据的id定位数据,用于读
    /*这里的定位方式基于简单的数学计算
    假设dataID = 1001,每个块可容纳1000条数据，
    那么显然该条数据是本块的第一条数据，而每一个metarecord又是等长的，这里是24个字节，
    所以它的起始地址从0开始，到23；
    其他数据对应的metarecord的存储位置也是确定的；

    如果有一个dataID序列：
    1001 1003 1004 1005；1002确实，那么中间就会空缺24个字节
    *
    读取数据不会block的结构产生任何影响
    * */
    public  MetaRecord locate(long dataID) throws IOException {

        byte[] bytes = new byte[DataBlock.METARECORDLENGTH];
        int pos = (int)(dataID% DataBlock.NUMSPERBLOCK-1);


        return MetaRecord.buildFromBytes(bytes);
    }

   //根据数据长度，分配内存空间
    public  MetaRecord allocateSpace(long dataID,long length){
        writeLock.lock();
        DataRecord dataRecord = dataFiles.get(dataFiles.size() - 1);
        long freeLength = dataRecord.getFreeSpacceLength();
        while(freeLength < length){
            //dataRecord
        }
        return null;
    }


}


