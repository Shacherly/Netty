package io.file;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestZip {
    public static void main(String[] args) throws Exception {
        String desPath = "C:\\Users\\Mufasa\\Desktop\\TestZIP.rar";
        zip("D:\\Project\\DUYI_EDU\\JdbcPool\\src\\dbconfig.properties", desPath);

        // File file = new File("D:\\Project\\DUYI_EDU\\JdbcPool\\src\\test\\");
        // System.out.println(emptyChilds(file));
    }


    public static void zip(String srcFileName, String desFileName) throws Exception {
        System.out.println("Start compressing……");
        File desFile = new File(desFileName);
        File srcFile = new File(srcFileName);

        File compress;
        if (srcFile.isDirectory()) {
            if (emptyChilds(srcFile)) {
                throw new Exception("空文件夹，你压缩NM呢？");
            } else {
                File[] files = srcFile.listFiles();
                assert files != null;
                String generateFile = files[0].getParent();
                compress = new File(generateFile);
            }
        } else {
            compress = srcFile;
        }

        // String generateFile = srcFile.getParent();

        // if (!compress.exists()) {
        //     compress.mkdirs();
        // }
        String baseName = compress.getName();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(desFile))) {
            try {
                boolean created = desFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            zip(zos, compress, baseName);
            // TimeUnit.SECONDS.sleep(3);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished");

    }

    private static void zip(ZipOutputStream zos, File srcFile, String fileName) throws IOException {
        // 如果是文件夹
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            // 打包下一级目录
            if (files != null) {
                zos.putNextEntry(new ZipEntry(fileName + "/"));
                fileName = fileName.length() == 0 ? "" : fileName + "/";
                for (File childFile : files) {
                    zip(zos, childFile, fileName + childFile.getName());
                }
                return;
            }
        }

        // 否则是文件
        zos.putNextEntry(new ZipEntry(fileName));
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
        BufferedOutputStream bos = new BufferedOutputStream(zos);

        byte[] bufferArr = new byte[2048];
        int readLength = bufferArr.length;
        for (; ; ) {
            if ((readLength = bis.read(bufferArr, 0, readLength)) == -1) return;
            // 为了防止有读入多余的输入流，write到最后会乱码
            bos.write(bufferArr, 0, readLength);
            // 写一次推一次
            bos.flush();
        }
        // while () {
        //     // if (temp == bufferArr.length) {
        //     //     bos.write(bufferArr);
        //     // } else {
        //     // 为了防止有读入多余的输入流，write到最后会乱码
        //     bos.write(bufferArr, 0, readLength);
        //     // 写一次推一次
        //     bos.flush();
        //     // }
        // }

        // bos.close();
    }

    // 所有子文件夹都为空
    private static boolean emptyChilds(File file) {
        if (file == null) return true;
        File[] files = file.listFiles();
        if (files == null || files.length == 0) return true;
        for (File child : files) {
            if (!child.isDirectory()) return false;
            emptyChilds(child);
        }
        return true;
    }
}
