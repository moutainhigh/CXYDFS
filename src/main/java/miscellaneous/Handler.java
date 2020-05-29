package miscellaneous;
/*
author:chxy
data:2020/5/19
description:处理消息的接口
*/
public interface Handler {

    //处理消息，不允许抛出异常，要在方法内部“消化异常”
    public void handle();
}
