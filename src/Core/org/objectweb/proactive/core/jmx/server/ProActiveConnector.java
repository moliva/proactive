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
package org.objectweb.proactive.core.jmx.server;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.jmx.ProActiveJMXConstants;
import org.objectweb.proactive.core.jmx.listeners.ProActiveConnectionNotificationEmitter;
import org.objectweb.proactive.core.util.URIBuilder;


/**
 * The ProActiveConnector thats exposes the active object responsible of  creating the ProActiveConnections
 * @author The ProActive Team
 *
 */
public class ProActiveConnector implements JMXConnector, Serializable, NotificationListener {
    private static final int CLOSED = 0;
    private static final int OPEN = 1;
    private ProActiveConnection connection;
    private ProActiveServerImpl paServer;
    private JMXServiceURL jmxServiceURL;
    private transient ProActiveConnectionNotificationEmitter emitter;
    private Map env;
    private int state = CLOSED;

    static {
        ProActiveConfiguration.load();
    }

    /**
     * Empty no arg constructor
     *
     */
    public ProActiveConnector() {
    }

    /*
     * creates a ProActive Connector
     */
    private ProActiveConnector(ProActiveServerImpl paServer, JMXServiceURL address, Map environment) {
        if ((paServer == null) && (address == null)) {
            throw new IllegalArgumentException("proactive server jmxServiceURL both null");
        }
        this.emitter = new ProActiveConnectionNotificationEmitter(this);
        this.paServer = paServer;
        this.jmxServiceURL = address;
        this.env = environment;
    }

    /**
     * Creates a ProActiveConnector
     * @param url the url of the connector
     * @param environment the environment of the connector Server
     */
    public ProActiveConnector(JMXServiceURL url, Map environment) {
        this(null, url, environment);
    }

    /**
     * @see javax.management.remote.JMXConnector#connect()
     */
    public void connect() throws IOException {
        connect(null);
    }

    /**
     * @see javax.management.remote.JMXConnector#connect(java.util.Map)
     */
    public void connect(Map arg0) throws IOException {
        try {
            String hostname = this.jmxServiceURL.getHost();
            int port = this.jmxServiceURL.getPort();
            String path = this.jmxServiceURL.getURLPath();
            int index = path.indexOf(ProActiveJMXConstants.SERVER_REGISTERED_NAME);
            String serverName = path.substring(index);
            String protocol = CentralPAPropertyRepository.PA_COMMUNICATION_PROTOCOL.getValue();
            String lookupUrl = URIBuilder.buildURI(hostname, serverName, protocol, port).toString();
            ProActiveServerImpl paServer = PAActiveObject.lookupActive(ProActiveServerImpl.class, lookupUrl);

            this.connection = paServer.newClient();
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
            this.emitter.sendConnectionNotificationFailed();
            throw new IOException(e.getMessage());
        } catch (IOException e) {
            this.emitter.sendConnectionNotificationFailed();
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        this.state = OPEN;
        emitter.sendConnectionNotificationOpened();
    }

    /**
     * @see javax.management.remote.JMXConnector#getMBeanServerConnection()
     */
    public synchronized MBeanServerConnection getMBeanServerConnection() throws IOException {
        return getMBeanServerConnection(null);
    }

    /**
     * @see javax.management.remote.JMXConnector#getMBeanServerConnection(javax.security.auth.Subject)
     */
    public synchronized MBeanServerConnection getMBeanServerConnection(Subject delegationSubject)
            throws IOException {
        return connection;
    }

    /**
     * @see javax.management.remote.JMXConnector#close()
     */
    public void close() throws IOException {
        this.state = CLOSED;
        emitter.sendConnectionNotificationClosed();
    }

    /**
     * @see javax.management.remote.JMXConnector#addConnectionNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
     */
    public void addConnectionNotificationListener(NotificationListener listener, NotificationFilter filter,
            Object handback) {
        this.listeners.addElement(listener);
        this.emitter.addNotificationListener(this, filter, handback);
    }

    /**
     * @see javax.management.remote.JMXConnector#removeConnectionNotificationListener(javax.management.NotificationListener)
     */
    public void removeConnectionNotificationListener(NotificationListener listener)
            throws ListenerNotFoundException {
        this.emitter.removeNotificationListener(listener);
    }

    /**
     * @see javax.management.remote.JMXConnector#removeConnectionNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
     */
    public void removeConnectionNotificationListener(NotificationListener listener,
            NotificationFilter filter, Object handback) throws ListenerNotFoundException {
        this.emitter.removeNotificationListener(listener, filter, handback);
    }

    /**
     * @see javax.management.remote.JMXConnector#getConnectionId()
     */
    public String getConnectionId() throws IOException {
        return "" + this.hashCode();
    }

    private Vector<NotificationListener> listeners = new Vector<NotificationListener>();

    public void handleNotification(Notification notification, Object handback) {
        Enumeration<NotificationListener> e = listeners.elements();
        while (e.hasMoreElements()) {
            e.nextElement().handleNotification(notification, handback);
        }
    }
}
