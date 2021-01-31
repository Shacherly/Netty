package io.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class TestIO_And_BufferedIO {
    static byte[] data = "123456\n".getBytes();
    static String path = "D:\\MUFASA\\Project\\Arithgram\\src\\java\\test\\io";

    public static void basicFileIO() throws Exception {
        File file = new File(path);
        FileOutputStream out = new FileOutputStream(file);
        for (; ; ) {
            Thread.sleep(10);
            out.write(data);
        }
    }

    public static void bufferedFileIO()throws Exception {
        File file = new File(path);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        for (; ; ) {
            Thread.sleep(10);
            out.write(data);
        }
    }
}
