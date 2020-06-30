package io.socket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.*;

public class ServerBootstrap {
    private static Socket socket;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // byte[] result = new byte[0];
        // result = startUp();

        // FutureTask<byte[]> taskServer = new FutureTask<>(Server::startUp);
        // new Thread(taskServer).start();
        // byte[] bytes = taskServer.get();
        // System.out.println("Main:::::" + bytes.length);

        startUp();
        // System.out.println("Main:::::\n" + bytes.length);
    }

    public static void startUp() throws IOException, ExecutionException, InterruptedException {
        // 使用线程池
        // 可缓冲的线程池，如果当前线程超过需要使用的线程，会进行回收
        // 如果没有可用的可以扩展，做到自动伸缩，容量是无限大的。
        ExecutorService service = new ThreadPoolExecutor(
                32,
                64,
                64,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(32),
                r -> new Thread(r, "Tcp-Server")
        );

        ServerSocket server = new ServerSocket(9999);

        // 轮询来接收连接
        // for (; ; ) {
            // 这是我所等待的结果，阻塞
            // final保证安全
            socket = server.accept();
            // FutureTask<byte[]> taskServer = new FutureTask<>(() -> handler(socket));
            // service.execute(Server::handler);
            // byte[] result = taskServer.get();
            // System.out.println("result:::\n" + new String(result, StandardCharsets.UTF_8));
            // service.shutdown();
            // return result;
            // service.execute(() -> response(socket, result));
            ServerHandler serverHandler = new ServerHandler(socket);
            serverHandler.start();
        // }

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
