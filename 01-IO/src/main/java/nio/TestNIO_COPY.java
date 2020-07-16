package nio;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TestNIO_COPY {

    @Test
    public void test() throws IOException {
        copy("nio.txt", "new_nio.dd");

    }


    public void copy(String source, String destination) throws IOException {
        File src = new File(source);
        FileInputStream fis = new FileInputStream(source);
        FileChannel in = fis.getChannel();

        MappedByteBuffer buffer = in.map(FileChannel.MapMode.READ_ONLY, 0, src.length());

        File des = new File(destination);
        if (!des.exists()) des.createNewFile();
        FileOutputStream fos = new FileOutputStream(des);
        FileChannel out = fos.getChannel();

        out.write(buffer);
        buffer.clear();

        in.close();
        fis.close();
        out.close();
        fos.close();

    }

    /**
     * 测试 capacity  limit  position 三个概念以及相关方法
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        CharBuffer buffer = CharBuffer.allocate(8);
        System.out.println("capacity：" + buffer.capacity());
        System.out.println("limit：   " + buffer.limit());
        System.out.println("position：" + buffer.position());
        System.out.println("remaining:" + buffer.remaining());
        System.out.println("hasRem:   " + buffer.hasRemaining());

        buffer.put('a');
        buffer.put('b');
        System.out.println("放入数据position向后移动");
        System.out.println("capacity：" + buffer.capacity());
        System.out.println("limit：   " + buffer.limit());
        System.out.println("position：" + buffer.position());
        System.out.println("remaining:" + buffer.remaining());
        System.out.println("hasRem:   " + buffer.hasRemaining());

        buffer.flip();
        System.out.println("执行flip翻转position置0，并使limit = capacity");
        System.out.println("capacity：" + buffer.capacity());
        System.out.println("limit：   " + buffer.limit());
        System.out.println("position：" + buffer.position());
        System.out.println("remaining:" + buffer.remaining());
        System.out.println("hasRem:   " + buffer.hasRemaining());
        buffer.put('c');

        System.out.println(buffer.get());
        System.out.println("执行get，position向前移动");
        System.out.println(buffer.position());
        System.out.println("remaining:" + buffer.remaining());
        System.out.println("hasRem:   " + buffer.hasRemaining());

        buffer.clear();
        System.out.println("remaining:" + buffer.remaining());
        System.out.println("hasRem:   " + buffer.hasRemaining());
        System.out.println("执行clear，清空position索引，limit = capacity但是数据还在");
        System.out.println(buffer.position());
        System.out.println("索引0" + buffer.get(0));
        System.out.println("索引1" + buffer.get(1));
        System.out.println("remaining:" + buffer.remaining());
        System.out.println("hasRem:   " + buffer.hasRemaining());
        // System.out.println(buffer.get());
        System.out.println(buffer.position());
    }

    @Test
    public void test1() throws IOException {
        File file = new File("nio.txt");
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);

        FileChannel channel = fos.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        String str = "hello nio";
        buffer.put(str.getBytes());// 放入缓冲，对应IO的read
        buffer.flip();// 翻转方向的，在输入与输出之间切换方向
        channel.write(buffer);// 对应IO的write(buffer数组)

        channel.close();
        fos.close();

    }

}
