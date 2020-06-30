import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TtyServer {
    public static void main(String[] args) {
        // 创建2个Reactor，构建主从Reactor模型
        // 用来管理通道Channel的
        // master 对应接收事件，slaver对应读写事件 ，都是无限循环的事件组（线程池）
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 服务端引导程序
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 设置相关的参数
        serverBootstrap.group(bossGroup, workerGroup)
                // 设置当前使用的通道
                .channel(NioServerSocketChannel.class)
                // 设置前面通道的处理器
                .handler(new LoggingHandler(LogLevel.INFO))
                // 定义客户端连接处理器的使用
                // 由于要处理的是客户端的通道，因此泛型使用SocketChannel
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new TtyServerHandler());
                    }
                });
        System.out.println("Server init...");

        try {
            ChannelFuture future = serverBootstrap.bind(9999).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
