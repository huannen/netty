package cn.seayou.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class NettyByteBufTest {
    public static void main(String[] args) {
        readAndWrite();

        read();
    }

    private static void read() {
        //用Unpooled工具类创建ByteBuf
        ByteBuf byteBuf2 = Unpooled.copiedBuffer("hello,zhuge!", CharsetUtil.UTF_8);
        //使用相关的方法
        if (byteBuf2.hasArray()) {
            byte[] content = byteBuf2.array();
            //将 content 转成字符串
            System.out.println(new String(content, CharsetUtil.UTF_8));
            System.out.println("byteBuf2=" + byteBuf2);

            System.out.println(byteBuf2.getByte(0)); // 获取数组0这个位置的字符h的ascii码，h=104

            int len = byteBuf2.readableBytes(); //可读的字节数  12
            System.out.println("len=" + len);

            //使用for取出各个字节
            for (int i = 0; i < len; i++) {
                System.out.print((char) byteBuf2.getByte(i));
            }

            //范围读取
            System.out.println(byteBuf2.getCharSequence(0, 6, CharsetUtil.UTF_8));
            System.out.println(byteBuf2.getCharSequence(6, 6, CharsetUtil.UTF_8));
        }
    }

    private static void readAndWrite() {
        ByteBuf byteBuf = Unpooled.buffer(10);
        System.out.println("byteBuf:"+byteBuf);
        for(int i=0; i<8; i++){
            byteBuf.writeByte(i);
        }
        System.out.println("byteBuf:"+byteBuf);
        for (int i = 0; i<8; i++){
            byteBuf.readByte();
        }
        System.out.println("byteBuf:"+byteBuf);
        for (int i = 0; i<5; i++){
            byteBuf.getByte(i);
        }
        System.out.println("byteBuf:"+byteBuf);

        // 会抛异常，读大于写
//        byteBuf.readByte();
    }
}
