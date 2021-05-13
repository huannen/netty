package main.java.com.seayou.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class AioTcpClient {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9000));
        socketChannel.write(ByteBuffer.wrap("hello server, i am a client of yours".getBytes()));

        /*
         * write方法也可以传异步方法，的那个write成功之后执行什么逻辑，
         * 但是这个地方客户端是想着得到服务器的返回数据的，并不只是仅仅关注write成功与否
         * 所以这里使用阻塞的方式进行,Future框架的方式
         */
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int len = socketChannel.read(buffer).get();
        if(len != -1){
            System.out.println("client receive from server:" + new String(buffer.array()));
        }
    }
}
