package cn.seayou.netty.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class XDecoder extends ByteToMessageDecoder {
    static final int PACKET_SIZE = 128;

    // 用来临时保留没有处理过的请求报文
    ByteBuf tempMsg = Unpooled.buffer();

    /**
     * @param channelHandlerContext
     * @param byteBuf 请求的数据,即输入数据
     * @param list 将粘在一起的报文拆分后的结果保留起来，即decode的输出数据
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        System.out.println(Thread.currentThread() + "收到了一次数据包，长度是：" + byteBuf.readableBytes());

        // 合并报文
        ByteBuf message = null;
        int tmpMsgSize = tempMsg.readableBytes();
        // 如果暂存有上一次余下的请求报文，则合并
        if (tmpMsgSize > 0) {
            message = Unpooled.buffer();
            message.writeBytes(tempMsg);
            message.writeBytes(byteBuf);
            System.out.println("合并：上一数据包余下的长度为：" + tmpMsgSize + ",合并后长度为:" + message.readableBytes());
        } else {
            message = byteBuf;
        }

        int size = message.readableBytes();
        int counter = size / PACKET_SIZE;
        for (int i = 0; i < counter; i++) {
            byte[] request = new byte[PACKET_SIZE];
            // 每次从总的消息中读取128个字节的数据
            message.readBytes(request);

            // 将拆分后的结果放入out列表中，交由后面的业务逻辑去处理
            list.add(Unpooled.copiedBuffer(request));
        }

        // 多余的报文存起来
        // 第一个报文： i+  暂存
        // 第二个报文： 1 与第一次
        size = message.readableBytes();
        if (size != 0) {
            System.out.println("多余的数据长度：" + size);
            // 剩下来的数据放到tempMsg暂存
            tempMsg.clear();
            tempMsg.writeBytes(message.readBytes(size));
        }
    }
}
