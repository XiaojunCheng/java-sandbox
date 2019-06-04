/*
 *  java-sandbox
 *  Copyright (c) 2012 datenwerke Jan Albrecht
 *  http://www.datenwerke.net
 *
 *  This file is part of the java-sandbox: https://sourceforge.net/p/dw-sandbox/
 *
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General  License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General  License for more details.
 *
 *  You should have received a copy of the GNU General  License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.datenwerke.sandbox.jvm.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.datenwerke.sandbox.SandboxContext;
import net.datenwerke.sandbox.SandboxedCallResult;
import net.datenwerke.sandbox.SandboxedEnvironment;
import net.datenwerke.sandbox.jvm.JvmTask;

public interface SandboxRemoteServer extends Remote {

    String NAME = "SandboxRemoteServer";

    boolean isAlive() throws RemoteException;

    void destroy() throws RemoteException;

    String getName() throws RemoteException;

    SandboxedCallResult execute(JvmTask task) throws RemoteException;

    void init(SandboxContext context) throws RemoteException;

    SandboxedCallResult runInContext(Class<? extends SandboxedEnvironment> call, Object... args) throws RemoteException;

    SandboxedCallResult runSandboxed(Class<? extends SandboxedEnvironment> call, Object... args) throws RemoteException;

    void registerContext(String name, SandboxContext context) throws RemoteException;

    void reset() throws RemoteException;
}
