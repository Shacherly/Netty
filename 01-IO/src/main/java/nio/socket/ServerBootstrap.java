package nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerBootstrap {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        SocketAddress address = new InetSocketAddress("localhost", 8888);
        serverChannel.socket().bind(address);
        System.out.println("server started...");

        ExecutorService service = new ThreadPoolExecutor(
                1,
                8,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(8),
                new ThreadFactory() {
                    final AtomicInteger index = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "Tcp-Server" + index.getAndIncrement());
                    }
                }
        );

        /**
         * 这里仍未用到 Selector，采用的是无限轮询客户端是否 数据包发送过来，创建Handler交给线程池去处理
         */
        for (; ; ) {
            SocketChannel socketChannel = serverChannel.accept();
            ServerHandler handler = new ServerHandler(socketChannel);
            service.execute(handler);
            // handler.start();
        }
    }

}
