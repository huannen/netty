package cn.seayou.netty.heartbeat;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HeartBeatClientHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("client receive data from server:" + o.toString());
        if("idle connection close".equals(o.toString())){
            System.out.println("server has closed");
            channelHandlerContext.channel().close();
        }
    }
}
