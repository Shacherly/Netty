package io.socket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * 多线程实现的 BIO，但是实际上是：来一个客户端就开一个线程，并且线程和用户一直对应直接用户退出，显然资源利用不合理
 */
public class ServerBootstrap {
    private static Socket socket;
    private static final int THREAD_COUNTS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws IOException {
        startUp();
    }

    public static void startUp() throws IOException{
        // 使用线程池
        // 可缓冲的线程池，如果当前线程超过需要使用的线程，会进行回收
        // 如果没有可用的可以扩展，做到自动伸缩，容量是无限大的。
        ExecutorService service = new ThreadPoolExecutor(
                THREAD_COUNTS,
                THREAD_COUNTS,
                32,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(THREAD_COUNTS),
                r -> new Thread(r, "Tcp-Server")
        );

        ServerSocket server = new ServerSocket(9999);
        socket = server.accept();
        ServerHandler serverHandler = new ServerHandler(socket);
        serverHandler.start();
    }

    private static void handler() {

    }


    private static void response(byte[] src) {
        String resultXml = new String(src, StandardCharsets.UTF_8);
        System.out.println("response中的结果-=========================\n" + resultXml);
        try {
            OutputStream os = socket.getOutputStream();
            os.write(src);
            os.flush();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        } /*finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }


}
