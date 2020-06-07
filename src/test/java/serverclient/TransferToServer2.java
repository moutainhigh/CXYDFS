package serverclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;


public class TransferToServer2 {
    ServerSocketChannel listener = null;
    protected void mySetup()
    {
        InetSocketAddress listenAddr =  new InetSocketAddress(9026);

        try {
            listener = ServerSocketChannel.open();
            ServerSocket ss = listener.socket();
            ss.setReuseAddress(true);
            ss.bind(listenAddr);
            System.out.println("Listening on port : "+ listenAddr.toString());
        } catch (IOException e) {
            System.out.println("Failed to bind, is port : "+ listenAddr.toString()
                    + " already in use ? Error Msg : "+e.getMessage());
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        TransferToServer2 dns = new TransferToServer2();
        dns.mySetup();
        dns.readData();
    }

    private void readData()  {
        try {
            while(true) {
                SocketChannel conn = listener.accept();
                System.out.println("Accepted : "+conn);
                conn.configureBlocking(true);
                FileChannel channel = new FileOutputStream(new File("E:/atad")).getChannel();
                FileChannel originalchannel = new FileInputStream(new File("E:/data")).getChannel();

                long counter = 0l,cset = 0l,start = System.currentTimeMillis();
                int count = 0;
                while(counter < originalchannel.size()){
                    cset = channel.transferFrom(conn,counter,originalchannel.size());
                    counter += cset;
                    System.out.println("total bytes transferred--"+cset+" and the counter is"+counter);
                    if(count++ == 30)break;
                }
                System.out.println("time cost is:"+(System.currentTimeMillis()-start));
                System.out.println(originalchannel.size());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
