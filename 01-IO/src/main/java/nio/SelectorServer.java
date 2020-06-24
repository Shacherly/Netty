package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 *
 */
public class SelectorServer {
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
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer writeBuffer = ByteBuffer.allocate(128);
        ByteBuffer readBuffer = ByteBuffer.allocate(128);
        writeBuffer.put("hello, from server!".getBytes());

        // 选择器管理通道
        // 不断的获取select的返回值
        for (; ; ) {
            // 返回“需要执行操作的通道”的个数，置空大于0就是有
            int readys = selector.select();
            if (readys == 0) continue;
            // 否则获取哪些通道要执行，集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 遍历集合
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            for (; ; ) {
                if (!iterator.hasNext()) break;
                SelectionKey key = iterator.next();
                iterator.remove();// 拿到之后就要移除
                if (key.isAcceptable()) {

                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_WRITE);

                } else if (key.isWritable()) {

                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    writeBuffer.flip();
                    socketChannel.write(writeBuffer);
                    // 写完之后要读
                    key.interestOps(SelectionKey.OP_READ);

                } else if (key.isReadable()) {

                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    readBuffer.clear();
                    socketChannel.read(readBuffer);
                    readBuffer.flip();

                    StringBuffer stringBuffer = new StringBuffer();
                    while (readBuffer.hasRemaining()) {
                        stringBuffer.append((char) readBuffer.get());
                    }

                    System.out.println("client data : " + stringBuffer);

                } else if (key.isConnectable()) {

                }
            }
        }
    }

    public static void getAction() {

    }

}
