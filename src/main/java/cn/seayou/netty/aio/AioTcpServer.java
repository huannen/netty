package cn.seayou.netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;

public class AioTcpServer {
    public static void main(String[] args) throws Exception {
        /*
         * 0、通道可以创建通道组（线程池）
         */
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(4));

        /*
         *  1、创建AsynchronousServerSocketChannel,如果没有通道组，group参数可以不传入
         */
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(group);

        /*
         * 2、绑定端口，配置server socket channel
         */
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 9000));

        /*
         * 3、直接监听接受服务端的请求
         */
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

            /*
             * 异步方法，等请求接收成功，就会来执行这个方法
             */
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
                /*
                 * 接收下一个请求，在这必须要接受客户的下一个连接，否则后面客户端会连接不上服务器
                 */
                serverSocketChannel.accept(attachment, this);
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                socketChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>(){

                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        buffer.flip();
                        socketChannel.write(ByteBuffer.wrap("Hello client".getBytes()));
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        exc.printStackTrace();
                    }
                });
            }

            /*
             * 异步方法，等请求接收失败，就会来执行这个方法
             */
            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        /*
         * 因为异步的，如果不等一会，那服务器就结束了
         */
        Thread.sleep(Integer.MAX_VALUE);
    }


}
