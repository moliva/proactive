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
package org.objectweb.proactive.core.mop;

import java.io.Serializable;


/* This class is optimized so that its instances are immutable */
public class MethodCallExceptionContext implements Serializable {

    /**
     * If the caller catches some RuntimeException, we have to wait for all calls
     * generated in the block at its end because any of these calls could throw one.
     */
    private boolean runtimeExceptionHandled;

    /**
     * If the caller told ProActive to catch all the thrown exceptions, we can
     * make the call asynchronous.
     */
    private boolean exceptionAsynchronously;

    /**
     * The default parameters, when the exception mechanism is not used.
     */
    public static final MethodCallExceptionContext DEFAULT = new MethodCallExceptionContext(false, false);

    /**
     * @param runtimeExceptionHandled
     * @param exceptionAsynchronously
     */
    public MethodCallExceptionContext(boolean runtimeExceptionHandled, boolean exceptionAsynchronously) {
        this.runtimeExceptionHandled = runtimeExceptionHandled;
        this.exceptionAsynchronously = exceptionAsynchronously;
    }

    /**
     * @return Returns the exceptionAsynchronously.
     */
    public boolean isExceptionAsynchronously() {
        return exceptionAsynchronously;
    }

    /**
     * @return Returns the runtimeExceptionHandled.
     */
    public boolean isRuntimeExceptionHandled() {
        return runtimeExceptionHandled;
    }

    public static MethodCallExceptionContext optimize(MethodCallExceptionContext context) {
        if (DEFAULT.equals(context)) {
            context = null;
        }

        return context;
    }

    @Override
    public String toString() {
        return "[rt:" + runtimeExceptionHandled + ", async:" + exceptionAsynchronously + "]";
    }
}
