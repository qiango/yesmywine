package com.yesmywine.test;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by hz on 2/16/17.
 */
public class ImportTest {  //文件删除测试
    public static void main(String[] args) {
        String path = "/home/hz/apps/tomcatwq";
        File f = new File(path);
        try {
            System.out.println(f.getCanonicalPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Sorry,can't get canonical path");
        }
        recurDelete(f);
//            System.out.println(f.list());
    }

    public static void recurDelete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tempFile : files) {
//                    System.out.println(tempFile);
                if (!tempFile.isFile()) {
                    recurDelete(tempFile);
                }
                tempFile.delete();
            }
        }
        file.delete();
    }
    @Test
    public void getget(){
//        for(int i=1;i<2;){
//            int a=
//            System.out.println(++a);
//        }

    }
}


