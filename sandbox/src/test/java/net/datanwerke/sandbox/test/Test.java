package net.datanwerke.sandbox.test;

import net.datenwerke.sandbox.SandboxContext;
import net.datenwerke.sandbox.SandboxService;
import net.datenwerke.sandbox.SandboxServiceImpl;
import net.datenwerke.sandbox.SandboxedEnvironment;
import net.datenwerke.sandbox.jvm.server.SandboxJvmServer;

import java.io.FileInputStream;

/**
 * @author chengxiaojun
 * @date 2019-06-04
 */
public class Test {

    public static void main(String[] args) {
        System.out.println(SandboxJvmServer.class.getName());
        SandboxService service = new SandboxServiceImpl();

        SandboxedEnvironment<Object> c = () -> {
            FileInputStream fis = new FileInputStream("protect.txt");
            System.out.println(System.getProperty("file.encoding"));
            /* untrusted code */
            return null;
        };

        SandboxContext context = new SandboxContext();
        service.runSandboxed(c.getClass(), context);
    }
}
