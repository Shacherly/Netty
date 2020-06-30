package io;

import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;

public class TestIO_COPY {
    @Test
    public void test() throws Exception {
        File file = new File("io.txt");
        // try {
        //     file.createNewFile();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        // System.out.println(file.getAbsolutePath());

        // stream(file);
        // reader(file);

        copy("io.txt", "io.dd");
    }

    // 字节流
    public void stream(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // 一次读取一个字节
            // int readLength = fis.read();
            byte[] buffer = new byte[1024];
            int readLength = 0;
            for (; ; ) {
                if ((readLength = fis.read(buffer)) == -1) break;
                System.out.println(new String(buffer, 0, readLength));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 字符流
    public void reader(File file) {
        try (FileReader fr = new FileReader(file)) {
            char[] buffer = new char[1024];
            int readLength = 0;
            for (; ; ) {
                if ((readLength = fr.read(buffer)) == -1) break;
                System.out.println(new String(buffer, 0, readLength));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copy(String source, String destination) throws Exception {
        FileInputStream fis = new FileInputStream(source);
        BufferedInputStream bis = new BufferedInputStream(fis);


        File file = new File(destination);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        int readLength = 0;
        for (; ; ) {
            if ((readLength = bis.read()) == -1) break;
            bos.write(readLength);
            // 打印内容看看
            System.out.print((char) readLength);
        }

        bos.close();
        fos.close();

        bis.close();
        fis.close();

    }
}
