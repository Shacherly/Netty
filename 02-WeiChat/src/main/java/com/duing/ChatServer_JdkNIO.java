package com.duing;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class ChatServer_JdkNIO {
    // 服务端通道
    private ServerSocketChannel channel;
    // 多路复用器
    private Selector selector;


    public ChatServer_JdkNIO() {
        try {
            channel = ServerSocketChannel.open();
            selector = Selector.open();
            channel.socket().bind(new InetSocketAddress(6666));
            channel.configureBlocking(false);
            // 注册通道的可接收事件
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 监听客户端的变化
    public void listen() {
        System.out.println("监听" + Thread.currentThread().getName());

        try {

            // 循环监听（轮询）
            for (; ; ) {
                // 只要num大于零代表有事件处理
                int num = selector.select();
                if (num == 0) continue;
                // 确认有事件要处理，找到选择器中已选择的集合 selectKeys
                // 遍历集合  拿到每一个SelectionKey 去判断当前处理四大事件中的哪种状态
                // selector帮我们管理已选择的事件集合
                Set<SelectionKey> set = selector.selectedKeys();
                Iterator<SelectionKey> iterator = set.iterator();
                for (; ; ) {
                    if (!iterator.hasNext()) break;
                    SelectionKey key = iterator.next();
                    // 拿出来去移除掉  避免重复处理

                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = channel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);

                        // 此时代表客户端和服务端进行连接了
                        System.out.println("澳门在线裸聊业务上线了！");
                        System.out.println("澳门赌场用户【" + clientChannel.socket().getRemoteSocketAddress() + "】上线了！！！");

                    }
                    if (key.isReadable()) {
                        readDate(key);
                    }
                    // 要移除  避免重复处理
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void readDate(SelectionKey key) {
        SocketChannel fromClient = null;


        try {
            fromClient = (SocketChannel) key.channel();
            // 一定要是使用ByteBuffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int readLength = fromClient.read(buffer);
            if (readLength > 0) {
                String msg = new String(buffer.array());
                System.out.println("澳门赌场用户"+fromClient.socket().getRemoteSocketAddress()
                        + "发送消息：" + msg);
                // 广播值其他客户端，不包括此客户端本身
                msg += "测试一下？？？";
                send2Other(msg, fromClient);
            }
        } catch (Exception e) {
            System.out.println("用户" + fromClient.socket().getRemoteSocketAddress() + "下线了");
            // 取消注册关系
            key.channel();
            try {
                // 关闭通道
                fromClient.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            // e.printStackTrace();
        }
    }

    private void send2Other(String msg, SocketChannel selfChannel) throws IOException {
        Set<SelectionKey> set = selector.keys();
        for (SelectionKey key : set) {
            Channel other = key.channel();
            if (other instanceof SocketChannel && other != selfChannel) {
                SocketChannel dest = (SocketChannel) other;
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                dest.write(buffer);
            }
        }

    }


    public static void main(String[] args) {
        ChatServer_JdkNIO server = new ChatServer_JdkNIO();
        server.listen();
    }
}
