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

import lombok.Getter;

/**
 * 受沙盒监控的线程，包含了所有的线程信息
 * Bean containing all that is to know about a sandboxed thread.
 *
 * @author Arno Mittelbach
 */
@Getter
public class SandboxMonitoredThread {

    private final Thread callingThread;
    private final SandboxedThread monitoredThread;
    private final SandboxContext context;
    private final long startTime;

    public SandboxMonitoredThread(Thread callingThread, SandboxedThread monitoredThread, SandboxContext context) {
        this.callingThread = callingThread;
        this.monitoredThread = monitoredThread;
        this.context = context;
        startTime = System.currentTimeMillis();
    }

    public boolean isAlive() {
        return monitoredThread.isStarted() && monitoredThread.isAlive();
    }
}
