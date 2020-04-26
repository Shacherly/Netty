package com.duing;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Client2 {
    private SocketChannel clientChannel;
    // 多路复用器   管理通道和事件之间的注册关系
    private Selector selector;

    public Client2() {
        try {
            selector = Selector.open();
            clientChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 6666));
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("用户" + clientChannel.getLocalAddress() + "上线了");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 发送聊天数据
    public void chat(String msg) {

        // ByteBuffer buffer = ByteBuffer.allocate(1024);
        // buffer.put(msg.getBytes());
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        try {
            clientChannel.write(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 接收服务端的推送数据
    private void acceptPublishMsg() {
        try {
            int num = selector.select();
            if (num == 0) return;
            Set<SelectionKey> set = selector.selectedKeys();
            Iterator<SelectionKey> iterator = set.iterator();
            for (; ; ) {
                if (!iterator.hasNext()) break;
                SelectionKey key = iterator.next();
                if (key.isReadable()) {
                    // 得到对应的通道
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    // 声明要处理的Buffer
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    socketChannel.read(byteBuffer);
                    String msg = new String(byteBuffer.array());
                    System.out.println(msg);

                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client2 client1 = new Client2();
        // 创建Client对象
        // 循环从服务端读取的数据  设置时间间隔   比如2秒
        new Thread(() -> {
            for (; ; ) {
                client1.acceptPublishMsg();

                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        // 客户端要能输入消息
        Scanner in = new Scanner(System.in);
        for (; ; ) {
            if (!in.hasNextLine()) break;
            String msg = in.nextLine();
            client1.chat(msg);
        }

    }
}
