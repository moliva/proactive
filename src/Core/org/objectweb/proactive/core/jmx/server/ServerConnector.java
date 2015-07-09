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
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.jmx.ProActiveJMXConstants;


/**
 * Creates and register a ProActive JMX Connector Server
 * @author The ProActive Team
 *
 */
public class ServerConnector {
    private MBeanServer mbs;
    private JMXConnectorServer cs;
    private String serverName;

    /**
     * Creates and register a ProActive JMX Connector attached to the platform MBean Server.
     *
     */
    public ServerConnector() {
        /*Retrieve the Platform MBean Server */
        this("serverName");
    }

    public ServerConnector(String serverName) {
        this.mbs = ManagementFactory.getPlatformMBeanServer();
        this.serverName = serverName;

        String url = "service:jmx:proactive:///jndi/proactive://localhost/" +
            ProActiveJMXConstants.SERVER_REGISTERED_NAME + "_" + this.serverName;
        JMXServiceURL jmxUrl;
        try {
            jmxUrl = new JMXServiceURL(url);
            Thread.currentThread().setContextClassLoader(ServerConnector.class.getClassLoader());
            cs = JMXConnectorServerFactory.newJMXConnectorServer(jmxUrl,
                    ProActiveJMXConstants.PROACTIVE_JMX_ENV, this.mbs);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the JMX Connector
     * @throws IOException
     */
    public void start() throws IOException {
        this.cs.start();
    }

    public UniqueID getUniqueID() {
        return ((ProActiveConnectorServer) cs).getUniqueID();
    }

    public ProActiveConnectorServer getConnectorServer() {
        return (ProActiveConnectorServer) cs;
    }
}
