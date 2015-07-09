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
package org.objectweb.proactive;

import org.objectweb.proactive.annotation.PublicAPI;


/**
 * <P>
 * RunActive is related to the activity of an active object.
 * When an active object is started, which means that its
 * active thread starts and serves the requests being sent
 * to its request queue, it is possible to define exactly how
 * the activity (the serving of requests amongst others) will
 * be done.
 * </P><P>
 * An object implementing this interface is invoked to run the
 * activity until an event trigger its end. The object being
 * reified as an active object can directly implement this interface
 * or an external class can also be used.
 * </P>
 * <P>
 * It is the role of the body of the active object to perform the
 * call on the object implementing this interface. For an active object
 * to run an activity, the method <code>runActivity</code> must not end
 * before the end of the activity. When the method <code>runActivity</code>
 * ends, the activity ends too and the <code>endActivity</code> can be invoked.
 * </P>
 * <P>
 * Here is an example of a simple implementation of <code>runActivity</code> method
 * doing a FIFO service of the request queue :
 * </P>
 * <pre>
 * public void runActivity(Body body) {
 *   Service service = new Service(body);
 *   while (body.isActive()) {
 *     service.blockingServeOldest();
 *   }
 * }
 * </pre>
 *
 * @author The ProActive Team
 * @version 1.0,  2002/06
 * @since   ProActive 0.9.3
 */
@PublicAPI
public interface RunActive extends Active {

    /**
     * Runs the activity of the active object.
     * @param body the body of the active object being started
     */
    public void runActivity(Body body);
}
