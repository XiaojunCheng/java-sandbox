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

package net.datenwerke.sandbox.permissions;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Defines a complex class permission containing execution stack checks.
 *
 * @author Arno Mittelbach
 */
@Getter
public class ClassPermission implements Serializable {

    private static final long serialVersionUID = -1665191928435489049L;

    private String name;

    private Collection<StackEntry> entries;

    public ClassPermission(String name, StackEntry entry) {
        this(name, Collections.singleton(entry));
    }

    public ClassPermission(String name, Collection<StackEntry> entries) {
        this.name = name;
        this.entries = entries;
    }

    @Override
    public ClassPermission clone() {
        Collection<StackEntry> clonedEntries = new ArrayList<>();
        for (StackEntry e : entries) {
            clonedEntries.add(e.clone());
        }
        return new ClassPermission(name, clonedEntries);
    }

    @Override
    public String toString() {
        return "WhitelistedClass [name=" + name + ", entries=" + entries + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entries == null) ? 0 : entries.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        ClassPermission other = (ClassPermission) obj;
        if (entries == null) {
            if (other.entries != null) {
                return false;
            }
        } else if (!entries.equals(other.entries)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
