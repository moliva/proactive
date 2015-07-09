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
package org.objectweb.proactive.core.process;

public interface MessageSink {

    /**
     * Returns a new message. The method should block until a message is ready.
     * null is returned when no more messages are to be produced.
     * @return the new message
     */
    public String getMessage();

    /**
     * Set a new message to be consumed be getMessage. The method may block if one
     * or several message(s) have been set before and not yet consumed. This is up
     * to the implementation.
     * Setting a null message is the signal that no more message will be produced.
     * @param message the message to be consumed or null to signal the end of the production
     */
    public void setMessage(String message);

    /**
     * Returns true is and only if a message is ready to be consumed (the call to getMessage()
     * won't block). If several threads share this ressource, there is a clear race condition
     * on the consumption of the message.
     * @return  true is and only if a message is ready to be consumed
     */
    public boolean hasMessage();

    /**
     * Returns true is and only if messages are still expected to be produced.
     * It is false after a setMessage is done with a null message.
     * @return true is and only if messages are still expected to be produced
     */
    public boolean isActive();
}
