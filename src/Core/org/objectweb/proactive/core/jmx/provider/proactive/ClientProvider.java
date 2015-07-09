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
package org.objectweb.proactive.core.jmx.provider.proactive;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXServiceURL;

import org.objectweb.proactive.core.jmx.ProActiveJMXConstants;
import org.objectweb.proactive.core.jmx.server.ProActiveConnector;


/**
 * <p>A provider for creating JMX API connector clients using a given
 * protocol.  Instances of this interface are created by {@link
 * JMXConnectorFactory} as part of its {@link
 * JMXConnectorFactory#newJMXConnector(JMXServiceURL, Map)
 * newJMXConnector} method.</p>
 *
 * @author The ProActive Team
 */
public class ClientProvider implements JMXConnectorProvider {

    /**
     * <p>Creates a new connector client that is ready to connect
     * to the connector server at the given address.  Each successful
     * call to this method produces a different
     * <code>JMXConnector</code> object.</p>
     *
     * @param serviceURL the address of the connector server to connect to.
     *
     * @param environment a read-only Map containing named attributes
     * to determine how the connection is made.  Keys in this map must
     * be Strings.  The appropriate type of each associated value
     * depends on the attribute.</p>
     *
     * @return a <code>JMXConnector</code> representing the new
     * connector client.  Each successful call to this method produces
     * a different object.
     *
     * @exception NullPointerException if <code>serviceURL</code> or
     * <code>environment</code> is null.
     *
     * @exception IOException if the connection cannot be made because
     * of a communication problem.
     */
    public JMXConnector newJMXConnector(JMXServiceURL serviceURL, Map env) throws IOException {
        if (!serviceURL.getProtocol().equals(ProActiveJMXConstants.PROTOCOL)) {
            throw new MalformedURLException("Protocol not proactive: " + serviceURL.getProtocol());
        }

        return new ProActiveConnector(serviceURL, env);
    }
}
