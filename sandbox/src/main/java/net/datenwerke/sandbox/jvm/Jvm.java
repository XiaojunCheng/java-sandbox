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

package net.datenwerke.sandbox.jvm;

import java.rmi.RemoteException;

import net.datenwerke.sandbox.SandboxContext;
import net.datenwerke.sandbox.SandboxedCallResult;
import net.datenwerke.sandbox.SandboxedEnvironment;
import net.datenwerke.sandbox.jvm.exceptions.JvmKilledUnsafeThreadException;
import net.datenwerke.sandbox.jvm.exceptions.JvmServerDeadException;
import net.datenwerke.sandbox.jvm.exceptions.RemoteTaskExecutionFailed;
import net.datenwerke.sandbox.jvm.server.SandboxJvmServer;

/**
 * Describes a remote agent handler that accesses the remote agent via RMI. See {@link SandboxJvmServer}
 * for the implementation of the remote agent.
 *
 * @author Arno Mittelbach
 * @see SandboxJvmServer
 */
interface Jvm {

    /**
     * Returns the jvm process
     *
     * @return
     */
    Process getProcess();

    /**
     * Kills the jvm process
     */
    void destroy();

    /**
     * Returns the URL under which the remote jvm can be accessed
     *
     * @return
     */
    String getRmiUrl();

    /**
     * Runs a {@link JvmTask} on the remote agent.
     *
     * @param task
     * @return
     * @throws JvmServerDeadException
     * @throws RemoteTaskExecutionFailed
     * @throws JvmKilledUnsafeThreadException
     */
    SandboxedCallResult execute(JvmTask task) throws JvmServerDeadException, RemoteTaskExecutionFailed, JvmKilledUnsafeThreadException;

    /**
     * Returns true if the process was destroyed.
     *
     * @return
     * @see #destroy()
     */
    boolean isDestroyed();

    /**
     * Initializes the remote agent with a {@link SandboxContext} for multiple usage.
     * <p>
     * This is mainly used by {@link JvmFreelancer}s.
     *
     * @param context
     * @throws JvmServerDeadException
     * @throws RemoteException
     */
    void init(SandboxContext context) throws JvmServerDeadException, RemoteException;

    /**
     * Resets the remote machine.
     * <p>
     * This is mainly used by {@link JvmFreelancer}s.
     *
     * @throws JvmServerDeadException
     * @throws RemoteException
     */
    void reset() throws JvmServerDeadException, RemoteException;

    /**
     * Runs the {@link SandboxedEnvironment} on the remote machine. For this it first needs
     * to be initialized with a {@link SandboxContext}.
     * <p>
     * This is mainly used by {@link JvmFreelancer}s.
     *
     * @param clazz
     * @param args
     * @return
     * @throws JvmServerDeadException
     * @throws RemoteTaskExecutionFailed
     * @throws JvmKilledUnsafeThreadException
     * @see #init(SandboxContext)
     */
    SandboxedCallResult runInContext(Class<? extends SandboxedEnvironment> clazz, Object... args) throws JvmServerDeadException, RemoteTaskExecutionFailed, JvmKilledUnsafeThreadException;

    /**
     * Runs the {@link SandboxedEnvironment} on the remote machine in a sandbox. For this it first needs
     * to be initialized with a {@link SandboxContext}.
     * <p>
     * This is mainly used by {@link JvmFreelancer}s.
     *
     * @param clazz
     * @param args
     * @return
     * @throws JvmServerDeadException
     * @throws RemoteTaskExecutionFailed
     * @throws JvmKilledUnsafeThreadException
     */
    SandboxedCallResult runSandboxed(Class<? extends SandboxedEnvironment> clazz, Object... args) throws JvmServerDeadException, RemoteTaskExecutionFailed, JvmKilledUnsafeThreadException;

    /**
     * Returns the port under which the RMI service runs.
     *
     * @return
     */
    int getPort();

    /**
     * Returns the hostname of the remote agent.
     *
     * @return
     */
    String getHost();

}
