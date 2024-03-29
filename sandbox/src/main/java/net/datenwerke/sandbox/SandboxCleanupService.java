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


import net.datenwerke.sandbox.exception.SandboxedTaskKilledException;

import com.google.inject.ImplementedBy;

/**
 * Service to perform killing of bad threads.
 *
 * @author Arno Mittelbach
 */
@ImplementedBy(SandboxCleanupServiceImpl.class)
public interface SandboxCleanupService {

    /**
     * Takes care of thread elimination.
     *
     * @param monitor   The monitor object monitoring the to be killed thread.
     * @param exception
     * @return
     */
    BadKillInfo kill(SandboxMonitoredThread monitor, SandboxedTaskKilledException exception);
}
