package io.file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestNIO {
    public static void main(String[] args) throws IOException {
        File file = new File("Nio.java");
        file.createNewFile();

        FileInputStream fis = new FileInputStream(
                new File("D:\\Project\\DUYI_EDU\\JdbcPool\\src\\com\\jdbc\\sqlsession\\SqlSessionFactory.java")
        );
        FileChannel inChannel = fis.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        FileOutputStream fos = new FileOutputStream(file);
        FileChannel channel = fos.getChannel();

        while (inChannel.read(buffer) != -1) {
            // 读完数据  切换模式  从写模式切换为读模式的重要方法（本质上更改了索引位置）
            buffer.flip();
            channel.write(buffer);
            // 写完数据切换为读模式
            buffer.flip();
        }
        inChannel.close();
        fis.close();
        channel.close();
        fos.close();

        BufferedOutputStream bos = new BufferedOutputStream(fos);
    }
}
