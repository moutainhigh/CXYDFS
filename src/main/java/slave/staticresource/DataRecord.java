/*
author:chxy
data:2020/6/3
description:数据条目，是对文件的再度封装
*/
package slave.staticresource;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;

public class DataRecord {

    private final File file;//存放数据的文件
    private long fileLength;//文件的逻辑长度

    //构造函数
    public DataRecord(Path filePath) throws IOException {

        this.file = filePath.toFile();
        //初始长度为0
        fileLength  = 0l;
    }

    //从start开始，读取length个字节到channel
    public void read(long start, long length) throws IOException {

        //该channel只读不写
        FileChannel channel = new RandomAccessFile(file,"r").getChannel();
        //读锁定
        FileLock fileLock = channel.tryLock(start, length, true);
        long counter = start,readCount;
        while(counter < start+length){
            readCount = channel.transferTo(counter,length,channel);
            counter += readCount;
        }
        fileLock.release();
    }

    //从start开始，写入length个字节到文件
    public void write(long start, long length) throws IOException {

        //该channel只写不读
        FileChannel channel = new RandomAccessFile(file,"w").getChannel();
        //写锁定
        FileLock fileLock = channel.tryLock(start,length,false);
        long counter = start,writeCount;
        while(counter < start+length){
            writeCount = channel.transferFrom(channel,start,length);
            counter += writeCount;
        }
        fileLock.release();
    }

    //返回该文件块当前剩余逻辑空间的长度
    public long getFreeSpaceLength() {
        return DataBlock.FILELENGTH-fileLength;
    }

    //设置该文件快的逻辑长度
    public void setLength(long length){
        this.fileLength = length;
    }

    public long getFileLength() {
        return fileLength;
    }
}


