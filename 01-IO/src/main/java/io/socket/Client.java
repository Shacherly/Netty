package io.socket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9999);

        OutputStream fos = socket.getOutputStream();
        String msg = "花Qman";
        fos.write(msg.getBytes());

        fos.flush();
        fos.close();
        socket.close();

    }
}
