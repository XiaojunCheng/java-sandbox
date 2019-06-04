package net.datanwerke.sandbox.test;

import net.datenwerke.sandbox.SandboxedEnvironment;

import java.io.FileInputStream;
import java.lang.management.ManagementFactory;

/**
 * @author chengxiaojun
 * @date 2019-06-04
 */
public class TestSandboxedEnvironment implements SandboxedEnvironment<Object> {

    @Override
    public Object execute() throws Exception {
        FileInputStream fis = new FileInputStream("protect.txt");
        System.out.println(System.getProperty("file.encoding"));
        /* untrusted code */
        return "hello: " + ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }
}
