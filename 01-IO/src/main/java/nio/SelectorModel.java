package nio;

import java.io.IOException;
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
 */
public class SelectorModel {
    public static void main(String[] args) throws IOException {
        // 创建一个端服务端管道，open是打开通道的意思
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定IP和端口
        InetSocketAddress address = new InetSocketAddress("localhost", 8888);
        serverSocketChannel.socket().bind(address);

        serverSocketChannel.configureBlocking(false);
        // 打开一个选择器
        Selector selector = Selector.open();
        // 把通道注册到选择器中,声明选择器监听的事件
        // 将通道选择器和该通道绑定，并为该通道注册 SelectionKey.OP_ACCEPT 事件，注册该事件后
        // 当该事件到达时，selector.select()会返回，如果该事件没有到达，则 selector.select() 会一直阻塞
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server starting...");

        ByteBuffer writeBuffer = ByteBuffer.allocate(128);
        ByteBuffer readBuffer = ByteBuffer.allocate(128);
        writeBuffer.put("welcome to JDK_NIO server, it is a new world!!!".getBytes());

        // 选择器管理通道
        // 不断的获取select的返回值
        // 采取轮询的方式监听 selector 上是否有需要处理的事件，有则做相应的处理
        for (; ; ) {
            // 返回“需要执行操作的通道”的个数，置空大于0就是有
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

                // 处理客户端连接事件
                if (key.isAcceptable()) {
                    System.out.println("there was new client connected...");
                    // 获得和客户端连接的通道
                    // ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) key.channel();

                    // 完成下面操作，意味着完成 tcp 三次握手，tcp 物理链路正式建立
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_WRITE);

                } else if (key.isWritable()) {

                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    writeBuffer.flip();
                    socketChannel.write(writeBuffer);
                    // 写完之后要读
                    key.interestOps(SelectionKey.OP_READ);


                } else if (key.isReadable()) {// 处理读事件
                    // 获取事件的读写通道
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    readBuffer.clear();
                    int len = socketChannel.read(readBuffer);
                    readBuffer.flip();

                    // StringBuffer stringBuffer = new StringBuffer();
                    // while (readBuffer.hasRemaining()) {
                    //     stringBuffer.append((char) readBuffer.get());
                    // }
                    if (len > 0) {
                        System.out.println("received client msg: ");
                        System.out.println(new String(readBuffer.array(), StandardCharsets.UTF_8));
                    }


                } /*else if (key.isConnectable()) {

                } */else {
                    System.out.println("客户端关闭...");
                    // SelectionKey 对象会失效，这意味着 Selector 再也不会监控与它相关的事件
                    key.cancel();
                }
            }
        }
    }

    private void handler(SelectionKey selectionKey) throws IOException {

    }

}
