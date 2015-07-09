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
 * EndActive is related to the finalization of an active object.
 * The finalization of the activity is done only once when the object
 * stops to be active and becomes unusable.
 * In case of a migration, an active object stops its activity
 * before restarting on a new VM automatically without finalization.
 * </P><P>
 * An object implementing this interface can be invoked to perform the
 * finalization work after the activity is ended. The object being
 * reified as an active object can implement this interface or an external
 * class can also be used.
 * </P>
 * <P>
 * It is generally the role of the body of the active object to perform the
 * call on the object implementing this interface.
 * </P>
 * <P>
 * It is hard to ensure that the <code>endActivity</code> method will indeed
 * be invoked at the end of the activity. <code>Error<code>, <code>Exception<code>,
 * customized activity that never ends or sudden death of the JVM can prevents
 * this method to be called by the body of the active object.
 * </P>
 *
 * @author The ProActive Team
 * @version 1.0,  2002/06
 * @since   ProActive 0.9.3
 */
@PublicAPI
public interface EndActive extends Active {

    /**
     * Finalized the active object after the activity has been stopped.
     * @param body the body of the active object being finalized.
     */
    public void endActivity(Body body);
}
