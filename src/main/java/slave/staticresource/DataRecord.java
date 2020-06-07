/*
author:chxy
data:2020/6/3
description:数据条目，是对文件的再度封装
*/
package slave.staticresource;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class DataRecord {

    private final RandomAccessFile file;//存放数据的文件
    private final FileChannel channelIn;//从文件中读取该文件的channel
    private final FileChannel channelOut;//向文件中写入该文件的channel
    /*要确保在发生io异常的时候能够释放读写锁*/
    private final Lock readLock;//读锁
    private final Lock writeLock;//写锁
    private final AtomicLong pointer;//文件指针的当前位置,指示下一个写入的位置

    //构造函数
    public DataRecord(Path filePath, ReadWriteLock lock) throws IOException {

        this.file = new RandomAccessFile(filePath.toFile(),"rw");
        //设定文件长度
        file.setLength(DataBlock.FILELENGTH);
        //设定文件指针
        pointer = new AtomicLong(0l);
        file.seek(pointer.get());

        //用于外部对文件的读取和写入
        this.channelIn = new FileInputStream(filePath.toFile()).getChannel();
        this.channelOut = new FileOutputStream(filePath.toFile()).getChannel();

        //用于访问的并发控制
        readLock = lock.readLock();
        writeLock = lock.writeLock();

    }

    //从start开始，读取length个字节到channel
    public void read(long start, long length, SocketChannel channel) throws IOException {
        //首先要获取读锁
        readLock.lock();
        long counter = start,readCount;
        while(counter < start+length){
            readCount = channelIn.transferTo(counter,length,channel);
            counter += readCount;
        }
        //释放读锁
        readLock.unlock();
    }

    //从start开始，写入length个字节到文件
    public void write(long start, long length, SocketChannel channel) throws IOException {

        //首先要获取写锁
        writeLock.lock();
        long counter = start,writeCount;
        while(counter < start+length){
            writeCount = channelOut.transferFrom(channel,start,length);
            counter += writeCount;
        }
        //更新文件指针
        pointer.getAndAdd(length);
        file.seek(pointer.get());
        //释放写锁
        writeLock.lock();
    }

    //获取文件剩余空间的长度
    public long getFreeSpacceLength(){

        return DataBlock.FILELENGTH-pointer.get();
    }
}


