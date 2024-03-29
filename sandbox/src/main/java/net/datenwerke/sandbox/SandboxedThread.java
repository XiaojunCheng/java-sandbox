/*
 *  java-sandbox
 *  Copyright (c) 2012 datenwerke Jan Albrecht
 *  http://www.datenwerke.net
 *
 *  This file is part of the java-sandbox: https://sourceforge.net/p/dw-sandbox/
 *
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.datenwerke.sandbox;

import java.lang.reflect.Method;

import net.datenwerke.sandbox.exception.SandboxedTaskKilledException;

/**
 * 由 {@link SandboxService} 创建，用于运行 untrusted 代码
 * Thread created by the {@link SandboxService} to run untrusted code.
 *
 * @author Arno Mittelbach
 * @see SandboxContext#setRunInThread(boolean)
 */
public class SandboxedThread extends Thread {

    private final Method method;
    private final Object instance;
    private final SandboxContext context;
    private final SandboxService service;
    private final boolean runInContext;

    private boolean success = false;
    private Object result;

    private Exception exception;

    private boolean killed;
    private boolean safe;
    private boolean started = false;

    public SandboxedThread(SandboxService service,
                           Method method,
                           Object instance,
                           SandboxContext context,
                           boolean runInContext) {
        this.service = service;
        this.method = method;
        this.instance = instance;
        this.context = context;
        this.runInContext = runInContext;
        setName("sandbox-" + context.getName());
    }

    @Override
    public void run() {
        started = true;
        try {
            //FIXME: 是不是搞反了
            if (runInContext) {
                result = method.invoke(instance);
            } else {
                String pw = service.restrict(context);
                try {
                    result = method.invoke(instance);
                } finally {
                    service.releaseRestriction(pw);
                }
            }
            success = true;
        } catch (Exception e) {
            this.exception = e;
        }
    }

    public Object getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public Exception getException() {
        return exception;
    }

    public void setKilled(boolean safe, SandboxedTaskKilledException exception) {
        this.killed = true;
        this.safe = safe;
        this.result = null;
        this.exception = exception;
    }

    public boolean isKilled() {
        return killed;
    }

    public boolean isKilledSafely() {
        return safe;
    }

    public boolean isStarted() {
        return started;
    }

}
