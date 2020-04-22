package nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8888));


        // 读写数据，都要通过Buffer，先创建buffer再传入通道，向上封装
        ByteBuffer writeBuffer = ByteBuffer.allocate(128);
        writeBuffer.put("hello netty from client".getBytes());
        writeBuffer.flip();
        // 处理完buffer传入通道中
        socketChannel.write(writeBuffer);

        // 读数据
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
        System.out.println("data from server: " + stringBuffer);





    }
}
