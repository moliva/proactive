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
package org.objectweb.proactive.core.body.proxy;

import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.Proxy;


public abstract class AbstractProxy implements Proxy, java.io.Serializable {
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public AbstractProxy() {
    }

    //
    // -- METHODS ----------------------------------------------------
    //

    /**
     * Checks if the given <code>Call</code> object <code>c</code> can be
     * processed with a future semantics, i-e if its returned object
     * can be a future object.
     *
     * Two conditions must be met : <UL>
     * <LI> The returned object is reifiable
     * <LI> The invoked method does not throw any exceptions
     * </UL>
     * @return true if and only if the method call can be asynchronous
     */
    protected static boolean isAsynchronousCall(MethodCall mc) {
        return mc.isAsynchronousWayCall();
    }
}
