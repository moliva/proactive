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
package org.objectweb.proactive.core.event;

/**
 * <p>
 * A class implementating this interface is listener of <code>MessageEvent</code>.
 * </p>
 *
 * @see MessageEvent
 * @author The ProActive Team
 * @version 1.0,  2001/10/23
 * @since   ProActive 0.9
 *
 */
public interface MessageEventListener extends ProActiveListener {

    /**
     * Signals that the reply encapsulated in the event <code>event</code>
     * has been received
     * @param event the message event that details the targeted message
     */
    public void replyReceived(MessageEvent event);

    /**
     * Signals that the reply encapsulated in the event <code>event</code>
     * has been sent
     * @param event the message event that details the targeted message
     */
    public void replySent(MessageEvent event);

    /**
     * Signals that the request encapsulated in the event <code>event</code>
     * has been received
     * @param event the message event that details the targeted message
     */
    public void requestReceived(MessageEvent event);

    /**
     * Signals that the request encapsulated in the event <code>event</code>
     * has been sent
     * @param event the message event that details the targeted message
     */
    public void requestSent(MessageEvent event);

    /**
     * Signals that the request encapsulated in the event <code>event</code>
     * has been served without needing a reply
     * @param event the message event that details the targeted message
     */
    public void voidRequestServed(MessageEvent event);

    /**
     * Signals that serving of the request encapsulated in the event <code>event</code>
     * has started
     * @param event the message event that details the targeted message
     */
    public void servingStarted(MessageEvent event);
}
