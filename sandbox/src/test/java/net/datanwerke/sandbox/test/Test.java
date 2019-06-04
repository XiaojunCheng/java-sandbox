package net.datanwerke.sandbox.test;

import net.datenwerke.sandbox.SandboxContext;
import net.datenwerke.sandbox.SandboxService;
import net.datenwerke.sandbox.SandboxServiceImpl;
import net.datenwerke.sandbox.jvm.server.SandboxJvmServer;

/**
 * @author chengxiaojun
 * @date 2019-06-04
 */
public class Test {

    public static void main(String[] args) {
        System.out.println(SandboxJvmServer.class.getName());
        SandboxService service = new SandboxServiceImpl();

        SandboxContext context = new SandboxContext();
        context.setRunRemote(true);
        service.runSandboxed(TestSandboxedEnvironment.class, context);
    }
}
