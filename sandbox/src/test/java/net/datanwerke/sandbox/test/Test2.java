package net.datanwerke.sandbox.test;

import net.datenwerke.sandbox.SandboxContext;
import net.datenwerke.sandbox.SandboxService;
import net.datenwerke.sandbox.SandboxServiceImpl;

/**
 * @author chengxiaojun
 * @date 2019-06-04
 */
public class Test2 {

    public static void main(String[] args) {
        SandboxService service = new SandboxServiceImpl();
        SandboxContext context = new SandboxContext();

        String pw = service.restrict(context);
        try {
            /* put untrusted code here */
        } finally {
            service.releaseRestriction(pw);
        }
    }

}
