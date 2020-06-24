package nio.socket;

import io.socket.Server1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HandleEnhancer extends Thread {
    private SocketChannel socketChannel;

    public HandleEnhancer(SocketChannel socketChannel) {
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
        ByteBuffer readBUffer = ByteBuffer.allocate(256);
        int capacity = readBUffer.capacity();
        byte[] transfer;
        byte[] result = new byte[0];
        int length;
        for (; ; ) {
            length = socketChannel.read(readBUffer);
            readBUffer.flip();
            transfer = readBUffer.array();
            if (length < capacity) transfer = Arrays.copyOf(transfer, length);
            result = concatByteArr(result, transfer);
            if (length < capacity) break;
        }

        System.out.println(new String(result, StandardCharsets.UTF_8));
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
