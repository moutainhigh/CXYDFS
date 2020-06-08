/*
author:chxy
data:2020/6/1
description:
*/
package slave.staticresource;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileCollection {

    //id生成器，文件名是一个整数转八字节字符串
    private final AtomicInteger fileID;

    //block的元数据，存储dataid到块的映射，到块中位置的映射
    private final File metaFile;

    //数据块
    private final List<DataRecord> dataRecords;

    //元数据和数据块存放的根路径
    private final Path rootPath;

    //读写锁
    private Lock lock;
    private Lock readLock = null;
    private Lock writeLock = null;

    private AtomicLong blocklLength = null;//块的当前逻辑长度
    private int totalBlockNums;//已创建的块数
    private int invalidBlockNums;//已回收（销毁）的块数


    //通过文件存储的根路径来构造filecollection
    public FileCollection(Path rootPath) throws IOException {
        fileID = new AtomicInteger(0);
        this.rootPath = rootPath;
        metaFile = rootPath.resolve(DataPath.METAPATH).toFile();

        this.dataRecords = new ArrayList<>();

        this.lock = new ReentrantLock();
    }

    //读取数据
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
    读取数据不会对block的结构产生任何影响
    * */
    public  MetaRecord locate(long dataID) throws IOException {

        byte[] bytes = new byte[DataBlock.METARECORDLENGTH];
        int pos = (int)(dataID% DataBlock.NUMSPERBLOCK-1);


        return MetaRecord.buildFromBytes(bytes);
    }

   //根据数据长度，分配内存空间
    public MetaRecord allocateSpace(long dataID,long length) throws IOException {

        MetaRecord metaRecord = null;

        lock.lock();

        //首先要定位出块
        int pos = totalBlockNums-invalidBlockNums-1;
        DataRecord dataRecord = dataRecords.get(pos);

        //如果“热块”已经没有存储空间了，要申请新的内存空间
        if(dataRecord.getFreeSpaceLength() <= 0) {
            String id = getIDFromInt(fileID.getAndIncrement());
            Path filePath = rootPath.resolve(id);
            DataRecord datqRecord = new DataRecord(filePath);
            //并且及时修改totalBlockNums和invalidBlockNums
            totalBlockNums += 1;
        }

        //判断当前块的剩余容量是否大于等于length
        if(dataRecord.getFreeSpaceLength() >= length){//不需要再创建新块，在本块分配存储空间
            metaRecord = new MetaRecord(dataID,blocklLength.getAndAdd(length),length);
            //更新fileLength
            dataRecord.setLength(dataRecord.getFileLength()+length);
        }else{//需要创建新块，还需要在其他块分配存储空间
            metaRecord = new MetaRecord(dataID,blocklLength.getAndAdd(length),length);
            length -= dataRecord.getFileLength();
            dataRecord.setLength(DataBlock.FILELENGTH);

            while(length > 0){

                MetaRecord record = allocateSpace(dataID, length);
                length -= record.getLength();
                metaRecord.merge(record);
            }
        }

        lock.unlock();

        return metaRecord;
    }

    //从int型的id中构建出一个八位字符串id
    private static String getIDFromInt(int id){
        return null;
    }
}


