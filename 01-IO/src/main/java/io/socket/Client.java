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
        Socket client = new Socket("localhost", 9091);

        // for (; ; ) {


        OutputStream os = client.getOutputStream();
        InputStream is = client.getInputStream();
        os.write(Const.MSG.getBytes());
        //
        os.flush();
        // TimeUnit.SECONDS.sleep(100000);
        // }
        // os.close();

        // Thread.sleep(2000);

        // os.close();

        // client = new Socket("localhost", 9999);

        byte[] msgBytes = new byte[]{};
        byte[] bytes = new byte[256];
        int length = bytes.length;
        int index = 0;
        for (; ; ) {
            if ((length = is.read(bytes, 0, length)) == -1) break;
            // log.info("for 几次？" + ++index);
            if (length < 256) bytes = Arrays.copyOf(bytes, length);
            msgBytes = concatByteArr(msgBytes, bytes);
        }
        System.out.println(new String(msgBytes, StandardCharsets.UTF_8));
        is.close();
        os.close();
        client.close();
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
