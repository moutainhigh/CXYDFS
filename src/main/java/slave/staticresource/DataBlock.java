/*
author:chxy
data:2020/6/1
description:节点上的数据块
*/
package slave.staticresource;

import master.staticresource.Block;
import miscellaneous.Message;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataBlock extends Block {

    public static final float THRESHOLD = 0.25f;//当数据块中有效数据的条数与数据的总条数之比低于此阈值时，触发数据块数据迁移

    public static final int FILELENGTH = 32*1024*1024;//存储文件大小，32M

    public static final byte METARECORDLENGTH = 24;//元数据项的长度

    public static final int NUMSPERBLOCK = 1000;//每个数据块包含的数据条数

    private final AtomicInteger totalNums;//当前数据总条数

    private final AtomicInteger activeNums;//当前有效数据条数

    private final Path path;//该块的存储路径

    private final List<Integer> fellows;//该块分布的结点，用结点id来表示

    private ReentrantReadWriteLock lock;
    private Lock readLock;//读锁
    private Lock writeLock;//写锁

    public DataBlock(long id, Path path,List<Integer> fellows) throws FileNotFoundException {
        super(id);
        this.totalNums = new AtomicInteger(0);
        this.activeNums = new AtomicInteger(0);
        this.path = DataPath.ROOTPATH.resolve(Long.toString(id));

        this.fellows = new ArrayList<>();
        this.fellows.addAll(fellows);

        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public  int getNums() {
        return totalNums.get();
    }

    public int getActiveNums() {
        return activeNums.get();
    }

    //利用fellow信息完成future规定的任务，在任务未完成之前，读锁不可释放；
    //该方法修改fellow
    public void getFellows(Future future){

        readLock.lock();
        List<Integer> fellowsRef = fellows;
        /*
        //start a new thread to get target job finished with fellowsRef
        future.run();
        //wait until the job is finished
        while(future.get());
        * */
        readLock.unlock();

    }

    //利用fellow信息完成future规定的任务，在任务未完成之前，写锁不可释放；
    //该方法修改fellow
    public void setFellows(Future future){

        writeLock.lock();
        List<Integer> fellowsRef = fellows;

        /*
        //start a new thread to get target job finished with fellowsRef
        future.run();
        //wait until the job is finished
        future.get();
        * */
        writeLock.unlock();
    }

    //读取数据
    //直接调用filecollection的api
    public void read(Message message){

    }

    //写入数据
    //先要确保其他其他块具备写入的条件
    public void write(Message message){

    }
}


