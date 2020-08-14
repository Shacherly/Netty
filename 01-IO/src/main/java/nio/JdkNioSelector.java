package nio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * JDK自带的NIO API
 * 测试NIO的线程模型，基于事件驱动的
 * 虽然是单线程的，但是性能已经很高了
 * https://mp.weixin.qq.com/s?subscene=19&__biz=MzU4MDUyMDQyNQ==&mid=2247483697&idx=1&sn=f59b399a445b3ee058a087a1d5f6fe37&chksm=fd54d1b7ca2358a1c30fed803dcef60ced43d43c15cda2795bd97e2adda3a8e9fc859cab7e4f&scene=7&ascene=65&devicetype=android-29&version=27001035&nettype=WIFI&abtest_cookie=AAACAA%3D%3D&lang=en&exportkey=AUJhNxX4mbAkxI%2FbLuaQyFI%3D&pass_ticket=Eb9rrJN5BBM%2BqnpwswJsSU461L%2BcbkSp3jBNsFvyiUADGQV4GrLluLkNfnO7lOJj&wx_header=1
 *
 *
 * 单线程的  基于 Reactor的多路复用模型
 */
public class JdkNioSelector {
    private Selector selector;

    public static void main(String[] args) throws Exception {
        Thread thread = Thread.currentThread();
        Class<? extends Thread> aClass = thread.getClass();
        Field name = aClass.getDeclaredField("name");
        name.setAccessible(true);
        name.set(thread, "666666666666666");

        JdkNioSelector jdkNioSelector = new JdkNioSelector();
        jdkNioSelector.init(8888);
        jdkNioSelector.listen();
    }

    public void init(int port) throws IOException {
        // 创建一个端服务端管道，open是打开通道的意思
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定IP和端口
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        serverSocketChannel.socket().bind(address);

        serverSocketChannel.configureBlocking(false);
        /** 通道管理器，多用户共用 */
        selector = Selector.open();
        // 把通道注册到选择器中,声明选择器监听的事件
        // 将通道选择器和该通道绑定，并为该通道注册 SelectionKey.OP_ACCEPT 事件，注册该事件后
        // 当该事件到达时，selector.select()会返回，如果该事件没有到达，则 selector.select() 会一直阻塞，但也可以设置非阻塞的方法
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server starting...");

        ByteBuffer writeBuffer = ByteBuffer.allocate(128);
        ByteBuffer readBuffer = ByteBuffer.allocate(128);
    }

    private void listen() throws IOException {
        // 选择器管理通道
        // 不断的获取select的返回值
        // 采取轮询的方式监听 selector 上是否有需要处理的事件，有则做相应的处理
        for (; ; ) {
            // 返回“需要执行操作的通道”的个数，大于0就是有  return  The number of keys
            int readys = selector.select();
            // nio 作为非阻塞式，上面这一步，我们是可以设置为非阻塞的，代码如下
            // selector.select(1000); // 无论是否有读写事件，selector 每隔 1s 被唤醒
            // selector.selectNow(); // 无论是否有读写事件，直接返回

            if (readys == 0) continue;
            // 否则获取哪些通道要执行，集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 遍历集合
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            for (; ; ) {
                if (!iterator.hasNext()) break;
                SelectionKey key = iterator.next();
                // 拿到之后就要移除
                iterator.remove();
                handler(key);
            }
        }
    }

    private void handler(SelectionKey key) {
        // 处理客户端连接事件
        if (key.isAcceptable()) {
            System.out.println("there was new client connected...");
            // 获得和客户端连接的通道，和上面定义的通道是同一个对象
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            // System.out.println("external serverSocketChannel" + serverSocketChannel);
            // System.out.println("internal serverSocketChannel1" + serverSocketChannel1);
            // System.out.println("deep equals?" + (serverSocketChannel == serverSocketChannel1));// true
            // 完成下面操作，意味着完成 tcp 三次握手，tcp 物理链路正式建立
            SocketChannel socketChannel = null;
            try {
                socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (key.isWritable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer writeBuffer = ByteBuffer.allocate(256);
            // 放一个欢迎消息
            try {
                writeBuffer.put("welcome to JDK_NIO server, it is a new world!!!是的\r\n".getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            writeBuffer.flip();
            try {
                socketChannel.write(writeBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 写完之后要读
            key.interestOps(SelectionKey.OP_READ);


        } else if (key.isReadable()) {// 处理读事件
            // 获取事件的读写通道
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            readBuffer.clear();
            int len = 0;

            try {
                len = socketChannel.read(readBuffer);

                readBuffer.flip();

                // StringBuffer stringBuffer = new StringBuffer();
                // while (readBuffer.hasRemaining()) {
                //     stringBuffer.append((char) readBuffer.get());
                // }
                if (len > 0) {
                    System.out.println("received client msg: ");
                    System.out.println(new String(readBuffer.array(), StandardCharsets.UTF_8));

                    // 回写数据
                    ByteBuffer writeBackBuffer = ByteBuffer.wrap("server received data-->\r\n".getBytes("GBK"));
                    // 将消息回传给客户端
                    socketChannel.write(writeBackBuffer);
                } else {
                    System.out.println("客户端关闭...");
                    // SelectionKey 对象会失效，这意味着 Selector 再也不会监控与它相关的事件
                    key.cancel();
                }
            } catch (IOException e) {
                System.out.println("客户端关闭...");
                // SelectionKey 对象会失效，这意味着 Selector 再也不会监控与它相关的事件
                key.cancel();
            }

        }
    }

}
