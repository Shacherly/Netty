package nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        SocketAddress address = new InetSocketAddress("localhost", 8888);
        serverChannel.socket().bind(address);

        // 仍然是接收连接
        for (; ; ) {
            SocketChannel socketChannel = serverChannel.accept();

            // 读写数据，都要通过Buffer，先创建buffer再传入通道，向上封装
            // ByteBuffer writeBuffer = ByteBuffer.allocate(128);
            // writeBuffer.put("hello netty from server".getBytes());
            // writeBuffer.flip();
            // // 处理完buffer传入通道中
            // socketChannel.write(writeBuffer);

            // 读
            ByteBuffer readBUffer = ByteBuffer.allocate(256);
            System.out.println("capacity"+readBUffer.capacity());
            byte[] transfer;
            StringBuffer stringBuffer = new StringBuffer();
            System.out.println(socketChannel.read(readBUffer));
            // 读完成
            readBUffer.flip();
            System.out.println("remaining"+readBUffer.remaining());
            System.out.println("hasRemaining"+readBUffer.hasRemaining());

            int index = 0;
            // for (; ; ) {
            //     System.out.print("   " + ++index);
            //     if (!readBUffer.hasRemaining()) break;
            //     stringBuffer.append((char) readBUffer.get());
            // }
            transfer = readBUffer.array();
            String message = new String(transfer, StandardCharsets.UTF_8);
            stringBuffer.append(message);
            // System.out.println("data from client: " + stringBuffer.append(message));

            int length = socketChannel.read(readBUffer);
            System.out.println(length);
            readBUffer.flip();
            /*for (; ; ) {
                // System.out.print("   " + ++index);
                if (!readBUffer.hasRemaining()) break;
                stringBuffer.append((char) readBUffer.get());
            }*/
            transfer = readBUffer.array();
            if (length < readBUffer.capacity()) transfer = Arrays.copyOf(transfer, length);
            message = new String(transfer, StandardCharsets.UTF_8);
            stringBuffer.append(message);
            System.out.println("data from client: " + stringBuffer);
            System.out.println("remaining"+readBUffer.remaining());
            System.out.println("hasRemaining"+readBUffer.hasRemaining());
            // System.out.println(socketChannel.read(readBUffer));


            ByteBuffer writeBuffer = ByteBuffer.allocate(128);
            writeBuffer.put("hello netty from server, this is server's reponse after receiving cients' request".getBytes());
            writeBuffer.flip();
            // 处理完buffer传入通道中
            socketChannel.write(writeBuffer);


            socketChannel.close();
            // serverChannel.close();
        }
    }
}
