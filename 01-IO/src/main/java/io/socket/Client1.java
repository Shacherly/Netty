package io.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client1 {
    public static void main(String[] args) throws Exception {
        Socket client = new Socket("localhost",9999);

        OutputStream out = client.getOutputStream();
        InputStream in = client.getInputStream();
        out.write("hello".getBytes());
        out.flush();
        System.out.println("发送完成");

        //接收数据
        int b = 0;
        while (in.available() != 0) {
            b = in.read();
            System.out.println((char)b);
        }
        System.out.println("接收完成");

        in.close();
        out.close();
        client.close();
        System.out.println("客户端已退出");
    }
}
