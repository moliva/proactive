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
package org.objectweb.proactive.extensions.masterworker.interfaces.internal;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * The interface of the worker objects <br/>
 * A worker is connected to a TaskProvider (i.e. Master) which will provide tasks to execute <br/>
 * @author The ProActive Team
 *
 */
public interface Worker {

    /**
     * Returns the name of this worker
     * @return name of the worker
     */
    String getName();

    /**
     * tells that this worker is alive
     * @return a boolean wrapper cause we need to wait at some point until all have answered 
     */
    BooleanWrapper heartBeat();

    /**
     * terminates this worker
     * @return true if the object terminated successfully
     */
    BooleanWrapper terminate();

    /**
     * Asks the worker to wake up
     */
    void wakeup();

    /**
     * Asks the worker to clear its activity
     */
    void clear();
}
