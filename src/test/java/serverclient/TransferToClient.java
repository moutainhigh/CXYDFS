package serverclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class TransferToClient {

    public static void main(String[] args) throws IOException{
        TransferToClient sfc = new TransferToClient();
        sfc.testSendfile();
    }
    public void testSendfile() throws IOException {
        String host = "localhost";
        int port = 9026;
        SocketAddress sad = new InetSocketAddress(host, port);
        SocketChannel sc = SocketChannel.open();
        sc.connect(sad);
        sc.configureBlocking(true);

        String fname = "E:/data";

        FileChannel fc = new FileInputStream(fname).getChannel();
        long start = System.currentTimeMillis();
        long  curnset = 0;

//        System.out.println("position before:"+fc.position());
//        curnset =  fc.transferTo(0, fsize, sc);
//        System.out.println("position after:"+fc.position());
//        System.out.println("total bytes transferred--"+curnset+" and time taken in MS--"+(System.currentTimeMillis() - start));
        long counter = 0l;
        while(counter < fc.size()){
            curnset =  fc.transferTo(counter, fc.size(), sc);
            counter += curnset;
            System.out.println("total bytes transferred--"+curnset+" and the counter is"+counter);
        }

        System.out.println("time cost is:"+(System.currentTimeMillis()-start));

    }


}
