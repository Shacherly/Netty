package io.file;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class T_Buffered {
    public static void main(String[] args) {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("D:\\Program Files\\openresty-1.15.8.3-win64\\resources\\static\\filedoc\\templates\\attendance_import.xls"));

            System.out.println(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
