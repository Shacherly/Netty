package heartbeat_07;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class TimeClient {

    public static void main(String[] args) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 解决拆包,基于分隔符的解码器
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            // 解决拆包,基字符串解码器
                            ch.pipeline().addLast(new StringDecoder());

                            ch.pipeline().addLast(new TimeClientHandler());

                        }
                    });

            Channel channel = bootstrap.connect("127.0.0.1", 0101).sync().channel();
            channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    static class TimeClientHandler extends ChannelInboundHandlerAdapter {

        private byte[] request;
        private int count;

        public TimeClientHandler() {
            request = ("query time " + System.getProperty("line.separator")).getBytes();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("method channelActive");
            ByteBuf message;

            // 客户端希望发送100次 query time 加上分隔符  这样的业务包
            // 实际上服务端接收的并不是预料到的，实际上服务端只读到了两次，因为将多个包进行了粘包
            for (int i = 0; i < 100; i++) {
                message = Unpooled.buffer(request.length);
                message.writeBytes(request);
                ctx.writeAndFlush(message);
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // ByteBuf buf = (ByteBuf) msg;
            // byte[] tmp = new byte[buf.readableBytes()];
            // buf.readBytes(tmp);
            //
            // String data = new String(tmp, StandardCharsets.UTF_8);

            String data = (String) msg;
            System.out.println("data is :" + data + "; count is" + ++count);
        }
    }

}
