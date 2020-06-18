package nio.socket;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8888));


        // 读写数据，都要通过Buffer，先创建buffer再传入通道，向上封装
        ByteBuffer writeBuffer = ByteBuffer.allocate("hello netty from client".getBytes().length);
        writeBuffer.put("hello netty from client".getBytes());
        writeBuffer.flip();
        // 处理完buffer传入通道中
        socketChannel.write(writeBuffer);

        // 读数据
        // 读
        StringBuffer stringBuffer = new StringBuffer();
        ByteBuffer readBUffer = ByteBuffer.allocate(1);
        List<Byte> list = new ArrayList<>(512);
        int length = readBUffer.capacity();
        for (; ; ) {
            if ((length = socketChannel.read(readBUffer)) == -1) break;
            // socketChannel.read(readBUffer);
            // 读完成
            // readBUffer.flip();
            // int remaining = readBUffer.remaining();
            byte[] tmp = new byte[length];
            readBUffer.get(tmp, 0, length);
            // Object[] b = tmp;
            // list.addAll(Arrays.asList(new Byte[]{tmp}));
            // for (; ; ) {
            //     if (!readBUffer.hasRemaining()) break;
            //     readBUffer.get
            //     stringBuffer.append((char) readBUffer.get());
            // }
        }
        System.out.println("data from server: " + stringBuffer);


    }
}
