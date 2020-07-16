package nio.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerHandler  extends Thread {
    private SocketChannel socketChannel;

    public ServerHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getId() + ":::" + Thread.currentThread().getName());
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process() throws IOException {
        ByteBuffer readBUffer = ByteBuffer.allocate(2);
        int capacity = readBUffer.capacity();
        byte[] transfer;
        byte[] result = new byte[0];
        int length;
        int count = 0;
        for (; ; ) {
            length = socketChannel.read(readBUffer);
            // 读完成就要flip一下
            readBUffer.flip();
            // remaining和hasRemaining在flip之后凋用才有值，否则都是0
            System.out.println("remaining" + readBUffer.remaining());
            System.out.println("hasRemaining" + readBUffer.hasRemaining());
            // 读取的字节数组暂存
            transfer = readBUffer.array();
            // 如果读取的小于容量那肯定是最后一次
            if (length < capacity) transfer = Arrays.copyOf(transfer, length);
            // 无论如何，有没有的到达流的结尾，都要把每一次读取到的结果放入结果数组
            result = concatByteArr(result, transfer);
            System.out.println("============" + ++count);
            if (length < capacity) break;
        }

        System.out.println("Server received: \n"+new String(result, StandardCharsets.UTF_8) + "\r\n");
        response();
    }

    private void response() throws IOException {
        byte[] bytes = "hello netty from server, this is server's reponse after receiving cients' request".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        // 处理完buffer传入通道中
        socketChannel.write(writeBuffer);
        socketChannel.close();
    }















    private static byte[] concatByteArr(byte[] var1, byte[] var2) {
        if (var1 == null) var1 = new byte[]{};
        if (var2 == null) var2 = new byte[]{};

        byte[] concat = new byte[var1.length + var2.length];
        System.arraycopy(var1, 0, concat, 0, var1.length);
        System.arraycopy(var2, 0, concat, var1.length, var2.length);
        return concat;
    }
}
