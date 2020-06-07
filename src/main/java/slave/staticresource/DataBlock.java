/*
author:chxy
data:2020/6/1
description:节点上的数据块
*/
package slave.staticresource;

/*
*  类DataBlock：
  属性：
   int id;//块id,基本信息
   list nodes;//分布的结点
   atomicinteger nums;//当前数据条数
   atomicinteger activenums;//当前有效数据条数
   FileCollection files;//存储的randomaccessfile文件

  静态属性：
   float THRESHOLD;//阈值，当数据块中有效数据的条数与数据的总条数之比低于此阈值时，触发数据迁移
   long FILESIZE;//randomaccessfile文件大小，

  操作：
   id getID();//获取id
   nodes getnodes();//获取nodes，互斥访问
   addnode(node);//增加一个node,互斥访问
   addnodes(nodes);//增加一批node，互斥访问
   removenode(node);//删除一个node，互斥访问
   Path rootpath;//该block所在文件根路径
*/

import master.staticresource.Block;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class DataBlock extends Block {

    //当数据块中有效数据的条数与数据的总条数之比低于此阈值时，触发数据迁移
    public static final float THRESHOLD = 0.25f;

    public static final long FILELENGTH = 32l*1024l*1024l;//randomaccessfile文件大小，32M

    public static final byte METARECORDLENGTH = 24;//元数据项的长度

    public static final int NUMSPERBLOCK = 1000;

    private final AtomicInteger nums;//当前数据条数

    private final AtomicInteger activeNums;//当前有效数据条数

   // private final FileCollection files;//存储的randomaccessfile文件

    private Path path;//该块的存储路径

    public DataBlock(long id, Path path) throws FileNotFoundException {
        super(id);
        this.nums = new AtomicInteger(0);
        this.activeNums = new AtomicInteger(0);
        this.path = DataPath.ROOTPATH.resolve(Long.toString(id));
       // this.files = new FileCollection(path);

    }

    public AtomicInteger getNums() {
        return nums;
    }

    public AtomicInteger getActiveNums() {
        return activeNums;
    }

    //filecollections可能会被多个线程同时访问
    //它的线程安全性，由它自身去维护
//    public FileCollection getFiles() {
//        return files;
//    }
}


