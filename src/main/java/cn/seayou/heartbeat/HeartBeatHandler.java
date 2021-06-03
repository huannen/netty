package cn.seayou.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatHandler extends SimpleChannelInboundHandler {

    private int readIdleCount = 0;
    private int readIdleTimes = 5;

    public HeartBeatHandler(int readIdleTimes){
        this.readIdleTimes = readIdleTimes;
    }

    public HeartBeatHandler(){}

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("server received data from client:"+o.toString());
        if("SYN".equals(o.toString())){
            channelHandlerContext.channel().writeAndFlush("FIN");
        }else{
            System.out.println("other info");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

        String eventType = null;
        switch (idleStateEvent.state()){
            case READER_IDLE:
                eventType = "read idle";
                readIdleCount++;
                break;
            case WRITER_IDLE:
                eventType = "write idle";
                break;
            case ALL_IDLE:
                eventType = "read and write idle";
                break;
        }
        System.out.println(ctx.channel().remoteAddress() + ": " +eventType);
        if(readIdleCount >= readIdleTimes){
            System.out.println("read idle counts over, socket will be closed");
            ctx.channel().writeAndFlush("idle connection close");
            ctx.channel().close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " is active.");
    }
}
