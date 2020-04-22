package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
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

        ByteBuffer writeBUffer = ByteBuffer.allocate(128);
        ByteBuffer readBUffer = ByteBuffer.allocate(128);
        writeBUffer.put("hello, from server!".getBytes());

        // 选择器管理通道
        for (; ; ) {
            int readys = selector.select();
            if (readys == 0) continue;
            // 否则获取哪些通道要执行
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 遍历集合
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            for (; ; ) {
                if (!iterator.hasNext()) break;
                SelectionKey key = iterator.next();
                iterator.remove();// 拿到之后就要移除
            }
        }
    }

    public static void getAction() {

    }

}
