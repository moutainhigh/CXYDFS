/*
author:chxy
data:2020/6/1
description:元数据条目，每个条目等长，记录了每条数据在block中的存储信息
*/
package slave.staticresource;

import java.nio.file.Path;
import java.util.List;

public class MetaRecord {

    //封装起始地址和长度的二元组
    public static class Pair{
        private int start;
        private int length;

        public Pair(int start, int length) {
            this.start = start;
            this.length = length;
        }

        public int getStart() {
            return start;
        }

        public int getLength() {
            return length;
        }
    }


    private final long dataID;//数据id
    private final List<Path> file;//所在文件的相对路径,有多个文件
    private final long pos;//在文件中的起始地址和长度，合并为一个long类型

    public MetaRecord(long dataID, List<Path> file, long pos) {
        //this.flag = flag;
        this.dataID = dataID;
        this.file = file;
        this.pos = pos;
    }

    //由起始地址和长度，合并为一个long类型
    public static long compose(Pair pair){
        //起始地址在高32位，
        return 0l;
    }

    //由一个long类型，解析出起始地址和长度
    public static Pair decompose(){
        return null;
    }

    public static MetaRecord buildFromBytes(byte[] bytes){
        return null;
    }


}


