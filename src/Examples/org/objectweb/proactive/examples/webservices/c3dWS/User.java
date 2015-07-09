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
package org.objectweb.proactive.examples.webservices.c3dWS;

/** Methods which are to be implemented by users.
 * Represents which services are available for Objects unrelated with User GUIs. */
public interface User {

    /** shows a String as a log */
    public void log(String s_message);

    /** Shows a String as a message to this user*/
    public void message(String s_message);

    /**
     * Informs the user that a new user has joined the party!!
     * @param  nUser The new user's ID
     * @param sName The new user's name
     */
    public void informNewUser(int nUser, String sName);

    /**
     * Informs the user that another user left
     * @param nUser The id of the old user
     */
    public void informUserLeft(String sName);

    /**
     * Display an interval of newly calculated pixels
     * @param newpix        The pixels as int array
     * @param interval        The interval
     */
    public void setPixels(Image2D image);

    /**
     * Reflect a change on the dispatcher host.
     * @param os the Name of the OS supporting the dispatcher
     * @param machine the name of the physical machine hosting the dispatcher
     */
    public void setDispatcherMachine(String machine, String os);
}
