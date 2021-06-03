package cn.seayou.decode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DecoderServer {
    public static void main(String[] args) throws Exception {
        // accept 处理连接的线程池
        NioEventLoopGroup acceptGroup = new NioEventLoopGroup();
        // read io 处理数据的线程池
        NioEventLoopGroup readGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(acceptGroup, readGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline pipeline = ch.pipeline();

                                    // 增加解码器
                                    pipeline.addLast(new XDecoder());

                                    // 打印出内容 handdler
                                    pipeline.addLast(new XHandler());
                                }
                            });
            System.out.println("启动成功，端口 7777");
            serverBootstrap.bind(7777).sync().channel().closeFuture().sync();
        } finally {
            acceptGroup.shutdownGracefully();
            readGroup.shutdownGracefully();
        }
    }
}