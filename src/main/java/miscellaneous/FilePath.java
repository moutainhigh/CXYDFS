/*
author:chxy
data:2020/5/29
description:专门用于定义文件存储路径
*/
package miscellaneous;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePath {

    public static final Path ROOTPATH = Paths.get("E:","cxydfs");
    public static final Path SLAVEPATH = Paths.get("slavedir","slaves");
    public static final Path BLOCKPATH = Paths.get("blockdir","blocks");
    public static final Path IDPATH = Paths.get("idsdir","ids");


}


