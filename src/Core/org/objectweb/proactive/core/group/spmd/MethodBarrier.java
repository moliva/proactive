/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.group.spmd;

import java.io.Serializable;


/**
 * This class describes the state of a method-based barrier.
 *
 * @author The ProActive Team
 */
public class MethodBarrier implements Serializable {
    private String[] methodNames;
    private boolean[] arrivedMethods;

    /**
     * Constructor
     * @param methodNames - the names of the methods awaited to release the barrier
     */
    public MethodBarrier(String[] methodNames) {
        this.methodNames = methodNames;
        this.arrivedMethods = new boolean[this.methodNames.length];
        for (int i = 0; i < this.arrivedMethods.length; i++) {
            this.arrivedMethods[i] = false;
        }
    }

    /**
     * Tests if the barrier is achived or not.
     * @return <code>true</code> if the barrier is ready to be released
     */
    public boolean barrierOver() {
        boolean result = true;
        for (int i = 0; ((i < this.arrivedMethods.length) && (result)); i++) {
            result &= this.arrivedMethods[i];
        }
        return result;
    }

    /**
     * Tests if this barrier wait for the specified method. In that case tag,
     * the method no more awaited and return <code>true</code>.
     * @param methodName a method name
     * @return <code>true</code> if the method was awaited by this barrier,
     * else <code>false</code>
     */
    public boolean checkMethod(String methodName) {
        int index = this.indexOf(methodName);
        if ((index < 0) || this.arrivedMethods[index]) {
            return false;
        } else {
            this.arrivedMethods[index] = true;
            return true;
        }
    }

    /**
     * Returns the index of the specified method not yet recieved
     * @param methodName - the name of the method
     * @return the index of the method, <code>-1</code> if the method is not
     * member of this barrier or already received
     */
    private int indexOf(String methodName) {
        boolean find = false;
        int i;
        for (i = 0; ((i < this.methodNames.length) && (!find)); i++) {
            find = this.methodNames[i].equals(methodName) && !this.arrivedMethods[i];
        }
        if (find) {
            return i - 1;
        } else {
            return -1;
        }
    }
}
