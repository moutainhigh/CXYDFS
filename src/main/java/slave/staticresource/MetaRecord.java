/*
author:chxy
data:2020/6/1
description:元数据条目，每个条目等长，记录了每条数据在block中的存储信息
*/
package slave.staticresource;

import java.nio.file.Path;
import java.util.List;

public class MetaRecord {

    private final long dataID;//数据id
    private  long start;//在block中的逻辑起始地址
    private  long length;//自身长度

    public MetaRecord(long dataID, long start,long length){

        this.dataID = dataID;
        this.start = start;
        this.length = length;
    }

    public static MetaRecord buildFromBytes(byte[] bytes){
        return null;
    }

    public long getDataID() {
        return dataID;
    }

    public long getStart() {
        return start;
    }

    public long getLength() {
        return length;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void merge(MetaRecord record){

    }
}


