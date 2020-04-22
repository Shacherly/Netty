package io.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws IOException {
        // 使用线程池
        // 可缓冲的线程池，如果当前线程超过需要使用的线程，会进行回收
        // 如果没有可用的可以扩展，做到自动伸缩，容量是无限大的。
        ExecutorService service = Executors.newCachedThreadPool();
        ServerSocket server = new ServerSocket(9999);
        // 需要无限轮询来接收连接
        for (; ; ) {
            // 这是我所等待的结果，阻塞
            // final保证安全
            final Socket socket = server.accept();
            service.execute(new Runnable() {
                @Override
                public void run() {
                    handler(socket);
                }
            });
        }
    }

    private static void handler(Socket socket) {
        System.out.println(Thread.currentThread().getId() + "---" + Thread.currentThread().getName());
        InputStream is = null;
        try {
            is = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int readLenght = 0;
            for (; ; ) {
                if ((readLenght = is.read(bytes)) == -1) break;
                System.out.println(new String(bytes, 0, readLenght));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
