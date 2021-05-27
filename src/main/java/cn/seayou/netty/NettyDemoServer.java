package cn.seayou.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;


public class NettyDemoServer {
    public static void main(String[] args) throws InterruptedException {
        /*
         * 1、创建两个线程组BossGroup和WorkerGroup，含有子线程NioEventloop的个数默认为CPU核数的两倍
         * bossGroup只是处理连接请求 ,真正的和客户端业务处理，会交给workerGroup完成
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            /*
             * 2、创建服务器启动对象BootStrap
             * Bootstrap 意思是引导，一个 Netty 应用通常由一个 Bootstrap 开始，
             * 主要作用是配置整个 Netty 程序，串联各个组件，Netty 中
             * Bootstrap 类是客户端程序的启动引导类，
             * ServerBootstrap 是服务端启动引导类。
             */
            ServerBootstrap bootstrap = new ServerBootstrap();

            /*
             * 3、配置参数，使用链式编程的思想
             */
            bootstrap.group(bossGroup, workerGroup) //设置两个线程组
                    //设置channel工厂原料，使用NioServerSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    /*
                     * 初始化服务器连接队列大小，服务端处理客户端连接请求是顺序处理的,所以同一时间只能处理一个客户端连接。
                     * 多个客户端同时来的时候,服务端将不能处理的客户端连接请求放在队列中等待处理
                     */
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //创建通道初始化对象，设置初始化参数
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //对workerGroup的SocketChannel设置处理器codec
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("netty server started.");

            /* 绑定一个端口并且同步, 生成了一个ChannelFuture异步对象，通过isDone()等方法可以判断异步 事件的执行情况
             * 启动服务器(并绑定端口)，bind是异步操作，sync方法是等待异步操作执行完毕
             */
            ChannelFuture channelFuture = bootstrap.bind(9000).sync();

            //channelFuture.isSuccess();

            //给cf注册监听器，监听我们关心的事件
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        System.out.println("port 9000 is listened successfully");
                    }else{
                        System.out.println("port 9000 is not listened successfully");
                    }
                }
            });
            /* 对通道关闭进行监听，closeFuture是异步操作，监听通道关闭
             * 通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成
             */
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

class NettyServerHandler extends ChannelInboundHandlerAdapter{
    /*
     * 当客户端向服务端发送数据的时候会触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("netty server thread for reading:"+Thread.currentThread().getName());
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline(); //本质是一个双向链接, 出站入站
        // 将 msg 转成一个 ByteBuf，类似NIO 的 ByteBuffer
        ByteBuf buffer = (ByteBuf) msg;
        System.out.println("get data:"+buffer.toString(CharsetUtil.UTF_8));
    }

    /*
     * 读取客户端数据完毕之后会触发
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buffer = Unpooled.copiedBuffer("Hello i am your server", CharsetUtil.UTF_8);
        ctx.writeAndFlush(buffer);
    }

    /*
     * 处理异常, 一般是需要关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}