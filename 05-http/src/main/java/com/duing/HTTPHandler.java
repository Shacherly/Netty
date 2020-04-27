package com.duing;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.spdy.SpdyHeaders;

public class HTTPHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer("hello http".getBytes())
        );

        HttpHeaders headers = response.headers();
        // HttpHeaderNames请求头的常用字段
        // HttpHeaderValues请求头常用字段的选项
        headers.add(HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderValues.TEXT_PLAIN + "charset=UTF-8");
        // 确保请求或者响应被完整处理   要设置长度
        headers.add(HttpHeaderNames.CONTENT_LENGTH,
                response.content().readableBytes());
        headers.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        ctx.write(response);
    }

    // 读完成时
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
