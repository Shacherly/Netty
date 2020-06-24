package io.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Server1 {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9999);
        System.out.println("服务已经启动……");
        Socket s = server.accept();

        InputStream in = s.getInputStream();
        OutputStream out = s.getOutputStream();

        //接收数据
        // int b = 0;
        // while (in.available() != 0) {
        //     b = in.read();
        //     System.out.print((char)b);
        // }
        byte[] bytes = new byte[256];
        byte[] msgBytes = new byte[]{};
        int readLenght = bytes.length;
        for (; ; ) {
            if ((readLenght = in.read(bytes, 0, readLenght)) == -1) break;

            if (readLenght < bytes.length) bytes = Arrays.copyOf(bytes, readLenght);
            // System.out.println(new String(bytes, 0, readLenght));
            msgBytes = concatByteArr(msgBytes, bytes);
            System.out.println("for里面：" + msgBytes.length);
        }
        System.out.println("接收完成:" + new String(msgBytes, StandardCharsets.UTF_8));
        //发送数据
        out.write("world".getBytes());
        out.flush();
        System.out.println("发送完成");
        out.close();
        in.close();
        server.close();
        System.out.println("服务已停止");
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
