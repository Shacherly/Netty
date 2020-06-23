package io.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerHandler extends Thread {
    private Socket socket;
    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getId() + "---" + Thread.currentThread().getName());
        byte[] msgBytes = new byte[]{};
        InputStream is = null;
        try {
            is = socket.getInputStream();
            byte[] bytes = new byte[256];
            int readLenght = bytes.length;
            for (; ; ) {
                if ((readLenght = is.read(bytes, 0, readLenght)) == -1) break;
                if (readLenght < bytes.length) bytes = Arrays.copyOf(bytes, readLenght);
                // System.out.println(new String(bytes, 0, readLenght));
                msgBytes = concatByteArr(msgBytes, bytes);
                System.out.println("for里面：" + msgBytes.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(msgBytes.length);
        System.out.println(new String(msgBytes, StandardCharsets.UTF_8));
        // return msgBytes;
        response(msgBytes);
        try {
            is.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void response(byte[] src) {
        String resultXml = new String(src, StandardCharsets.UTF_8);
        System.out.println("response中的结果-=========================\n" + resultXml);
        try {
            OutputStream os = socket.getOutputStream();
            os.write(src);
            os.flush();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        } /*finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
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
