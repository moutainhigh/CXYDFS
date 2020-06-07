/*
author:chxy
data:2020/6/1
description:专门定义文件存储路径
*/
package slave.staticresource;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DataPath {

    public static final Path ROOTPATH = Paths.get("E:","cxydfs","slave");
    public static final Path METAPATH = Paths.get("meta");
    public static final Path DATAPATH = Paths.get("data");

    static{
        ROOTPATH.toFile().mkdirs();
    }
}


