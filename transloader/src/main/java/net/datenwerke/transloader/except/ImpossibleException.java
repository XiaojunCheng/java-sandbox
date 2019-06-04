/*
*  transloader
*    
*  This file is part of transloader http://code.google.com/p/transloader/ as part
*  of the java-sandbox https://sourceforge.net/p/dw-sandbox/
*
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/

package net.datenwerke.transloader.except;

/**
 * The <code>RuntimeException</code> thrown only in circumstances that should never occur e.g. wrapping checked
 * <code>Exception</code>s, the handling of which is enforced by the compiler, but which can never actually be thrown in
 * the relevant context.
 *
 * @author Jeremy Wales
 */
public final class ImpossibleException extends TransloaderException {
    /**
     * Constructs a new <code>ImpossibleException</code> with the given nested <code>Exception</code>.
     *
     * @param cause the throwable which caused this one to be thrown
     */
    public ImpossibleException(Exception cause) {
        super("This should NEVER happen!", cause);
    }
}
