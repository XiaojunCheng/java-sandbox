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

package net.datenwerke.sandbox.jvm;

/**
 * Bean to configure a {@link JvmPool}
 *
 * @author Arno Mittelbach
 */
public interface JvmPoolConfig {

    /**
     * The {@link JvmInstantiator} to use when creating jvms.
     *
     * @return
     */
    JvmInstantiator getInstantiator();

    /**
     * The number of jvms in the pool.
     *
     * @return
     */
    int getPoolSize();

    /**
     * The number of jvms to be used as {@link JvmFreelancer}. Note that
     * these are not taken from the pool.
     *
     * @return
     */
    int getFreelancerSize();
}
