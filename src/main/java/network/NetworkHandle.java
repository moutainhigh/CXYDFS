/*
author:chxy
data:2020/5/4
description:网络通讯接口
*/
package network;

import master.Message;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public abstract class NetworkHandle{

    public static final int MASTERPORT = 12345;//主结点服务器端口
    public static final int SLAVEPORT = 12346;//从节点服务器端口
    public static final int CLIENTPORT = 12347;//客户端服务器端口

    //连接关闭，清理资源
    protected abstract void cleanUp();

    //接受消息
    protected abstract Message rcvMsg(SelectionKey key) throws IOException, Throwable;

    //发送消息
    protected abstract void sendMsg(Message message) throws IOException;

    //请求连接1
    protected abstract void doConnect(String ip , SocketChannel channel) throws IOException;

    //请求连接2
    protected abstract void doConnect(SelectionKey key) throws IOException;

    //接受连接
    protected abstract void doAccept(SelectionKey key) throws IOException;

}


