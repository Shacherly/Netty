package io.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket client = new Socket("localhost", 8888);

        // for (; ; ) {

        OutputStream os = client.getOutputStream();
        InputStream is = client.getInputStream();
        os.write(Const.MSG.getBytes());
        //
        os.flush();
        client.shutdownOutput();

        byte[] msgBytes = new byte[]{};
        byte[] bytes = new byte[2];
        int length = bytes.length;
        int index = 0;
        for (; ; ) {
            if ((length = is.read(bytes, 0, length)) == -1) break;
            System.out.println("for 几次？" + ++index);
            if (length < 256) bytes = Arrays.copyOf(bytes, length);
            msgBytes = ServerHandler.concatByteArr(msgBytes, bytes);
        }
        System.out.println(new String(msgBytes, StandardCharsets.UTF_8));
        is.close();
        os.close();
        client.close();
    }

}
