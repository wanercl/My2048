package com.example.my2048.util.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Converter {
    public static byte[] objectToBytes(Object obj) throws IOException {
        //把对象转换成字节数据
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            return bos.toByteArray();
        } finally {
            if (oos != null) oos.close();
        }
    }

    public static Object bytesToObject(byte[] bytes) {
        //把字节数组转换成对象
        Object t = null;
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream sIn;
        try {
            sIn = new ObjectInputStream(in);
            t = sIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
}
