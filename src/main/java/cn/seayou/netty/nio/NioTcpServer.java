package cn.seayou.netty.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioTcpServer {

    public static void main(String[] args) throws IOException {
        // 1、在本地创建一个ServerSocketChannel，并且绑定对应端口
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9000));

        // 2、设置通道为非阻塞模式，因为NIO中的selector选择器是非阻塞模式的，否则注册到selector的时候会报错
        serverSocketChannel.configureBlocking(false);

        // 3、创建一个selector
        Selector selector = Selector.open();

        // 4、将通道绑定到selector上去，并且设定感兴趣的操作（监听的操作事件）
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 5、循环监听客户端的请求事件
        while(true){
            System.out.println("waiting for the request from clients");
            int select  = selector.select();
            System.out.println("got a request!!!");


            Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
            while(selectionKeyIterator.hasNext()){
                SelectionKey selectionKey = selectionKeyIterator.next();

                // 6、一定要先删除当前的监听到的操作，不要重复处理
                selectionKeyIterator.remove();

                // 7、处理操作事件
                handle(selectionKey);
            }
        }

    }

    private static void handle(SelectionKey selectionKey) throws IOException {
        if(selectionKey.isAcceptable()){
            // 1、获取到当前监听到的操作事件所注册的通道channel，因为通信数据都在通道里面
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();

            /*
             * 2、accept方法本身是个阻塞的方法，当时如果判定了当前的操作是Acceptable的话，
             * 那肯定就不需要在等待，方法会立即执行完
             */
            SocketChannel socketChannel = serverSocketChannel.accept();

            /*
             * 3、记住在获取到通道之后一定要记得设置为非阻塞模式，否则一定会在注册的时候报如下错：
             * Exception in thread "main" java.nio.channels.IllegalBlockingModeException
             */
            socketChannel.configureBlocking(false);

            /*
             * 4、已经accept了请求，连接已经建立了，
             * 那接下来，这个通道selector对这个通道感兴趣的操作应该是：读事件
             */
            socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
        }else if(selectionKey.isReadable()){
            /* 1、根据这个选择队列里的SelectionKey，
             * 获取到对应监听通道(这个不再是服务端SocketChannel)，数据得通过通道去读
             */
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

            /*
             * 2、从channel中读取数据，channel里面封装了数据流对象，可以直接read
             */
            System.out.println("the data from client: ");
            ByteBuffer bufffer = ByteBuffer.allocate(1024);
            int len = -1;
            /* NIO的事件触发采用的是水平触发监控数据的，准确说是条件触发，
             * 思考：那这里还需要写循环体去读通道的数据吗
             * 水平触发的情况：比如读了50字节的数据，重新调用API进入IO等待，
             * 这时条件触发的API会立即发送新的时间通知：read ready notification通知应用程序可读。
             */
            while((len = socketChannel.read(bufffer)) != -1){
                System.out.print(new String(bufffer.array(), 0, len));
            }
            System.out.println();

            /*
             * 3、反馈客户端，告知数据已成功获取，即将成功提示信息写入channel，这也表示channel是可读可写的
             */
            ByteBuffer bufferToWrite = ByteBuffer.wrap("Success".getBytes());
            socketChannel.write(bufferToWrite);

            /*
             * 4、修改通道感兴趣的操作点
             */
            selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }else if(selectionKey.isWritable()){

            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

            /*
             * 处理写事件，写入数据
             */

            /*
             * 因为NIO事件触发机制采用的是水平触发，如果应用没有数据要写，那就不应该关注socket的写事件
             * 否则会导致无限次的立即返回write ready notification，最终可能导致CPU100%的情况
             * 如果没有数据要写入的时候，就应该取消这个写事件，等到有数据往外写入的时候再注册写事件
             */
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }


}
