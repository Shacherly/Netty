package nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        SocketAddress address = new InetSocketAddress("localhost", 8888);
        serverChannel.socket().bind(address);

        // 仍然是接收连接
        SocketChannel socketChannel = serverChannel.accept();

        // 读写数据，都要通过Buffer，先创建buffer再传入通道，向上封装
        ByteBuffer writeBuffer = ByteBuffer.allocate(128);
        writeBuffer.put("hello netty from server".getBytes());
        writeBuffer.flip();
        // 处理完buffer传入通道中
        socketChannel.write(writeBuffer);

        // 读
        ByteBuffer readBUffer = ByteBuffer.allocate(128);
        socketChannel.read(readBUffer);
        // 读完成
        readBUffer.flip();
        StringBuffer stringBuffer = new StringBuffer();
        for (; ; ) {
            if (!readBUffer.hasRemaining()) break;
            stringBuffer.append((char) readBUffer.get());
        }
        System.out.println("data from client: " + stringBuffer);

        socketChannel.close();
        serverChannel.close();

    }
}
