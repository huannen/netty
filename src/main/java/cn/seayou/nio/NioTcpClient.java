package main.java.cn.seayou.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioTcpClient {
    private Selector selector;


    public static void main(String[] args) throws IOException {
        NioTcpClient client = new NioTcpClient();
        client.initClient("127.0.0.1", 9000);
        client.connect();
    }

    private void initClient(String ip, int port) throws IOException {
        /*
         * 1、创建通道
         */
        SocketChannel socketChannel = SocketChannel.open();

        /*
         * 2、设置通道为非阻塞模式
         */
        socketChannel.configureBlocking(false);

        /*
         * 3、获取一个selector选择器，或者叫做通道管理器
         */
        selector = Selector.open();

        /*
         * 4、利用通道向服务端发起连接
         *   需要注意的是
         */
        socketChannel.connect(new InetSocketAddress(ip, port));

        /*
         * 5、将通道注册到通道管理器selector上，
         */
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    private void connect() throws IOException {
        // 轮询检查selector通道管理器，是不是有操作事件发生
        while(true){
            selector.select();
            Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
            while(selectionKeyIterator.hasNext()){
                SelectionKey selectionKey = selectionKeyIterator.next();
                selectionKeyIterator.remove();
                handle(selectionKey);
            }

        }
    }

    private void handle(SelectionKey selectionKey) throws IOException {
        if(selectionKey.isConnectable()){
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            if(socketChannel.isConnectionPending()){
                socketChannel.finishConnect();
            }
            socketChannel.configureBlocking(false);
            ByteBuffer byteBuffer = ByteBuffer.wrap("hello my server".getBytes());
            socketChannel.write(byteBuffer);
            socketChannel.register(this.selector, selectionKey.OP_READ);
        }else if(selectionKey.isReadable()){
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len = socketChannel.read(byteBuffer);
            if(len != -1){
                System.out.println("receive from server:"+ new String(byteBuffer.array()));
            }

        }

    }
}
