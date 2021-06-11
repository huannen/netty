package cn.seayou.netty.chatroom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/*
 * 使用websocket进行编写
 */
public class ChatRoomHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /*
     * 监听客户端的注册
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        for(Channel channel: channels){
            channel.writeAndFlush("<font color='red'>用户"+ctx.channel().remoteAddress()+"已上线<font>");
        }
        channels.add(ctx.channel());
    }

    /*
     * 监听客户端下线
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        for(Channel channel: channels){
            if(channel != ctx.channel()) {
                channel.writeAndFlush("<font color='gray'用户" + ctx.channel().remoteAddress() + "已下线<font>");
            }
        }
        channels.remove(ctx.channel());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        for (Channel channel: channels) {
            if(channel != channelHandlerContext.channel()){
                channel.writeAndFlush(new TextWebSocketFrame("[用户"+channel.remoteAddress()+"]说:"+textWebSocketFrame.text()));
            }else{
                channel.writeAndFlush(new TextWebSocketFrame("我说 :"+ textWebSocketFrame.text()));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
