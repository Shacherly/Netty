package heartbeat_07;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HeartBeatClient {
    int port;
    Random random;

    public HeartBeatClient(int port) {
        this.port = port;
        random = new Random();
    }

    public static void main(String[] args) {
        new HeartBeatClient(1177).start();
    }

    /**
     * 启动的方法
     */
    public void start() {

        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new HeartBeatInitializer());

            Channel channel = bootstrap.connect("192.168.37.134", port).sync().channel();

            // 要发送的数据
            String msg = "I am alive";
            for (; ; ) {
                if (!channel.isActive()) break;
                int i = random.nextInt(10);
                System.out.println("wait time:" + i);
                TimeUnit.SECONDS.sleep(i);
                channel.writeAndFlush(msg);
            }


        } catch (Exception e) {

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }


    static class HeartBeatInitializer extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new StringDecoder())
                    .addLast(new StringEncoder())
                    .addLast(new HeartBeatClientHandler());

        }
    }

    static class HeartBeatClientHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("client reveive msg:" + msg);
            if (msg != null && msg.equals("u are out")) {
                System.out.println("client will close");
                ctx.channel().closeFuture();
            }
        }
    }


}
