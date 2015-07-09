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
package org.objectweb.proactive.core.jmx.listeners;

import java.io.IOException;

import javax.management.NotificationBroadcasterSupport;
import javax.management.remote.JMXConnectionNotification;

import org.objectweb.proactive.core.jmx.server.ProActiveConnector;


/**
 * This Notification emitter send informations about the state of the connection between connector client and
 * connector server.
 * @author The ProActive Team
 *
 */
public class ProActiveConnectionNotificationEmitter extends NotificationBroadcasterSupport {
    private static long sequenceNumber;
    private ProActiveConnector connector;

    /**
     * Constructor for the emitter
     * @param connector  This emitter is sending notifications about this connector
     */
    public ProActiveConnectionNotificationEmitter(ProActiveConnector connector) {
        this.connector = connector;
        addNotificationListener(connector, null, null);
    }

    /**
     * Sends a "Connection opened"  notification to listeners
     */
    public void sendConnectionNotificationOpened() {
        JMXConnectionNotification notification = new JMXConnectionNotification(
            JMXConnectionNotification.OPENED, connector, getConnectionId(), getNextNotificationNumber(),
            "Connection opened", null);
        sendNotification(notification);
    }

    /**
     *  Sends a "Connection closed" Notification to listeners
     */
    public void sendConnectionNotificationClosed() {
        JMXConnectionNotification notification = new JMXConnectionNotification(
            JMXConnectionNotification.CLOSED, connector, getConnectionId(), getNextNotificationNumber(),
            "Connection closed", null);
        sendNotification(notification);
    }

    /**
     *  sends a "Connection failed" notification to listeners
     */
    public void sendConnectionNotificationFailed() {
        JMXConnectionNotification notification = new JMXConnectionNotification(
            JMXConnectionNotification.FAILED, connector, getConnectionId(), getNextNotificationNumber(),
            "Connection failed", null);
        sendNotification(notification);
    }

    private long getNextNotificationNumber() {
        synchronized (ProActiveConnectionNotificationEmitter.class) {
            return sequenceNumber++;
        }
    }

    private String getConnectionId() {
        try {
            return connector.getConnectionId();
        } catch (IOException x) {
            return null;
        }
    }
}
