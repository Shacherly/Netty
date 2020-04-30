package com.duing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class Person implements Serializable {
    private static final long serialVersionUID = -5347519719101743274L;

    private int id;
    private String name;

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static void main(String[] args) throws IOException {
        Person per = new Person(1, "哈哈哈哈哈");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(per);

        oos.flush();
        oos.close();

        byte[] result = bos.toByteArray();
        System.out.println("原生序列化之后流的长度" + result.length);
        System.out.println("Buffer处理之后的长度" + per.code().length);
    }

    public byte[] code() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        byte[] value = name.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);

        buffer.putInt(id);
        buffer.flip();

        value = null;

        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }
}
