package cn.seayou.netty.chatroom;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class ChatRoomServer {
    private int port;

    public ChatRoomServer(int port){
        this.port = port;
    }

    public void start() {
        // 1、启动类
        ServerBootstrap bootstrap = new ServerBootstrap();

        // 2、LoopGroup
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try{
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<ServerChannel>() {
                        @Override
                        protected void initChannel(ServerChannel serverChannel) throws Exception {
                            /*
                             * 设置管道，对数据进行处理，包装等操作
                             */
                            //websocket基于http协议，需要对数据进行拆包，HttpServerCodec中核心的方法decode
                            serverChannel.pipeline().addLast(new HttpServerCodec());

                            //http消息组装
                            serverChannel.pipeline().addLast(new HttpObjectAggregator(65535));

                            //websocket通信支持
                            serverChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));

                            //自定义聊天信息处理器
                            serverChannel.pipeline().addLast(new ChatRoomHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(port).sync();

            channelFuture.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
