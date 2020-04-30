package com.duing;

import com.google.protobuf.InvalidProtocolBufferException;

public class ProtobufTest {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        // 建造者模式
        PersonModel.Person.Builder builder = PersonModel.Person.newBuilder();

        builder.setId(3);
        builder.setName("Sunny");

        PersonModel.Person person = builder.build();

        System.out.println(person);

        System.out.println("Person byte=====>>>");
        for (byte b : person.toByteArray()) {
            System.out.print(b);
        }
        System.out.println("<<<=====反序列化");

        byte[] bytes = person.toByteArray();
        PersonModel.Person p2 = PersonModel.Person.parseFrom(bytes);

        System.out.println(p2);

    }
}
