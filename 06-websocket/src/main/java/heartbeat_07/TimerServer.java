package heartbeat_07;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class TimerServer {

    public static void main(String[] args) {
        // new NioE
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 解决拆包,基于分隔符的解码器
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            // 解决拆包,基字符串解码器
                            ch.pipeline().addLast(new StringDecoder());
                            //
                            ch.pipeline().addLast(new TimerServerHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(0101).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    static class TimerServerHandler extends ChannelInboundHandlerAdapter {
        // 请求次数
        int count;
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // ByteBuf buf = (ByteBuf) msg;
            // // 请求长度设置定长
            // byte[] request = new byte[buf.readableBytes()];
            // buf.readBytes(request);
            //
            // String data = new String(request, StandardCharsets.UTF_8)
            //         .substring(0, request.length - System.getProperty("line.separator").length());
            // 上面增加解码器这里就不需要自己处理了
            String data = (String) msg;
            String current = String.valueOf(LocalDateTime.now());
            String response = "Query time : [ " + data + "]; count is " + ++count + " ||time is :" + current;
            // 输出一下代表读到了数据  就进行一次打印
            System.out.println("服务端接收到了 ：" + response);
            ByteBuf resp = Unpooled.copiedBuffer(response.getBytes());
            ctx.writeAndFlush(resp);

            // super.channelRead(ctx, msg);
        }
    }

}
