package com.duing;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    // 设置客户端通道的实现类
                    .channel(NioSocketChannel.class)
                    // 设置通道对应的处理器   仍然使用通道初始化器
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            System.out.println("客户端初始化完成");
            ChannelFuture channelFuture = bootstrap.connect("192.168.1.100", 8888).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {

            group.shutdownGracefully();
        }
    }
}
