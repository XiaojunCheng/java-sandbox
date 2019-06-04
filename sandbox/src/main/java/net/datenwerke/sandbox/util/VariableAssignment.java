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

package net.datenwerke.sandbox.util;

import lombok.Getter;

import java.io.Serializable;

/**
 * Utility class to be used to transport variable assignments into sandboxes.
 *
 * @author Arno Mittelbach
 */
@Getter
public class VariableAssignment implements Serializable {

    private static final long serialVersionUID = 4877086339218800266L;

    private final Object value;
    private final Class<?> type;
    private final boolean bridge;

    public VariableAssignment(Object value) {
        this(value, null != value ? value.getClass() : null, true);
    }

    public VariableAssignment(Object value, Class<?> type) {
        this(value, type, true);
    }

    public VariableAssignment(Object value, Class<?> type, boolean bridge) {
        this.value = value;
        this.type = type;
        this.bridge = bridge;
    }

}
