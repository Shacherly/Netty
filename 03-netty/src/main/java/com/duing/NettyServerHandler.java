package com.duing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.internal.ChannelUtils;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;

/**
 * 自定义的  Handler 需要继承特定的适配器
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 在通道被启动，即为建立连接时触发此方法
     * 通常用于写一些欢迎消息等
     * @param ctx 在处理通道时可能需要使用到的上下问对象
     *            可以获取到 channel、pipeline等
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive done");
        // 写入数据时调用writeAndFlush，代表写入并刷新，只有刷新后才生效
        ctx.writeAndFlush("性感潮汐在线喷水！");
        super.channelActive(ctx);
    }


    /**
     * 读取数据的方法
     * @param ctx
     * @param msg 就是客户端发送过来的数据，默认是Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 对应NIO中的ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        System.out.println(ctx.channel().remoteAddress());
        System.out.println("性感黑鸟，在线喝水！" + buf.toString(CharsetUtil.UTF_8));

        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 写入数据还是使用Bytebuf处理，其中的一个分类Unpooled
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello world, I am handler", CharsetUtil.UTF_8));

        super.channelReadComplete(ctx);
    }
}
