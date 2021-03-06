package com.duing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("Client channelActive done");
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello world  Im  client", CharsetUtil.UTF_8));


        // super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("服务地址" + ctx.channel().remoteAddress());
        System.out.println("接收到服务消息" + buf.toString(CharsetUtil.UTF_8));

    }
}
