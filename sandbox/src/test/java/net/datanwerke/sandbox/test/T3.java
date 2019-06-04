package net.datanwerke.sandbox.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author chengxiaojun
 * @date 2019-06-04
 */
public class T3 {

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("SecurityManager: " + System.getSecurityManager());

        FileInputStream fis = new FileInputStream("protect.txt");

        System.out.println(System.getProperty("file.encoding"));
    }

}
