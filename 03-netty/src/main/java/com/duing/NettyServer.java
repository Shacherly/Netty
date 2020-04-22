package com.duing;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {
    public static void main(String[] args) {
        // 4.x之后推出的概念，是管理channel通道的
        // 使用时通常创建一个boss一个worker，两个都会无限循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        // 服务端的启动对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 提供链式编程的方法设置参数
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    // 服务器端的通道实现
                    .channel(NioServerSocketChannel.class)
                    // reactor中的重要角色handler
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 连接服务端的客户端的处理器，客户端对应SocketChannel
                    // 参数为通道初始化的对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 需要在管道pipeline中设置处理器
                            // 区分通道和管道。管道是业务处理
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("服务端初始化完成！！！");

            // 绑定端口，让客户端来连接
            // 阻塞等待连接  可以理解为启动服务端
            ChannelFuture channelFuture = serverBootstrap.bind(8888).sync();

            // 关闭监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 无论是出错还是没出错，最终都要优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
