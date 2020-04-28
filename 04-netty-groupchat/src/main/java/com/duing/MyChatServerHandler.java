package com.duing;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;

public class MyChatServerHandler extends SimpleChannelInboundHandler {
    // 当多个通道传入Handler  netty提供了channel组的管理方法
    // 区分通道和管道，管道是做业务逻辑的，通道是管理用的
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(
            GlobalEventExecutor.INSTANCE
    );

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 能接受客户端的消息  再广播给其他客户端
        Channel selfChannel = ctx.channel();
        // channelGroup.stream().filter()
        Iterator<Channel> iterator = channelGroup.iterator();
        for (; ; ) {
            if (!iterator.hasNext()) break;
            Channel channel = iterator.next();
            if (selfChannel != channel) {
                if (((String) msg).length() == 0) {
                    continue;
                }
                channel.writeAndFlush("[客户端用户 - " + selfChannel.remoteAddress() +
                        " ]说： " + msg + "\n");
                continue;
            }
            String answer;
            if (((String) msg).length() == 0) {
                answer = "Please say something\r\n";
            } else {
                answer = "Did you say " + msg + "? \r\n";
            }
            channel.writeAndFlush(answer);
        }
    }

    // 刚刚建立连接时第一个被执行的方法
    // 常常用于将channel 加入channel组
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ctx.writeAndFlush("[来自服务器] - " + channel.remoteAddress() + " - 建立连接\n");

        channelGroup.add(channel);
    }

    // 连接不饿一处或者被断开   最后执行的方法
    // 自动将channel从channelGroup中移除
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ctx.writeAndFlush("[服务器] - " + channel.remoteAddress() + " - 断开连接\n");
        System.out.println("[channel group size] - " + channelGroup.size());

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 写多条时  先全部写完再flush
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "聊天室!\r\n");
        ctx.write("It is " + new Date() + " now. \r\n");
        ctx.flush();
        System.out.println("用户IP：" + channel.remoteAddress() + "上线");

    }

    // 通道不活跃
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "下线");

    }

    // 重写异常的捕获
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
