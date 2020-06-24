package nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerEnhancer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        SocketAddress address = new InetSocketAddress("192.168.37.162", 8888);
        serverChannel.socket().bind(address);


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

        for (; ; ) {
            SocketChannel socketChannel = serverChannel.accept();
            HandleEnhancer handler = new HandleEnhancer(socketChannel);
            service.execute(handler);
            // handler.start();
        }
    }

}
