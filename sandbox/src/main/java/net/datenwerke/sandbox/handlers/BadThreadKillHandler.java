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

package net.datenwerke.sandbox.handlers;

import net.datenwerke.sandbox.BadKillInfo;
import net.datenwerke.sandbox.SandboxService;

/**
 * Handler that is informed when bad threads were killed.
 * <p>
 * A bad thread is a thread that was stopped and was holding monitors/locks.
 *
 * @author Arno Mittelbach
 */
public interface BadThreadKillHandler extends SandboxHandler {

    /**
     * Called by the {@link SandboxService} when a bad thread was stopped.
     *
     * @param killInfo
     */
    void badThreadKilled(BadKillInfo killInfo);

}
