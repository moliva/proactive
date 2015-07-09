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
package org.objectweb.proactive.examples.migration;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


/**
 * This class is a client for a migratable Agent
 */
public class AgentClient {
    static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    public static void main(String[] args) {
        Agent myServer;
        String nodeName;
        String hostName;
        GCMApplication proActiveDescriptor = null;
        ProActiveConfiguration.load();
        try {
            proActiveDescriptor = PAGCMDeployment.loadApplicationDescriptor(new File(args[0]));
            proActiveDescriptor.startDeployment();

            GCMVirtualNode agent = proActiveDescriptor.getVirtualNode("Agent");
            agent.waitReady();
            List<Node> nodeList = agent.getCurrentNodes();

            // Create an active server within this VM
            myServer = (Agent) org.objectweb.proactive.api.PAActiveObject.newActive(Agent.class.getName(),
                    new Object[] { "local" });
            // Invokes a remote method on this object to get the message
            hostName = myServer.getName();
            nodeName = myServer.getNodeName();
            logger.info("Agent is on: host " + hostName + " Node " + nodeName);

            for (Node node : nodeList) {
                // Prints out the message
                myServer.moveTo(node);
                nodeName = myServer.getNodeName();
                hostName = myServer.getName();
                logger.info("Agent is on: host " + hostName + " Node " + nodeName);
            }
            myServer.endBodyActivity();
        } catch (Exception e) {
            logger.error("Could not reach/create server object");
            e.printStackTrace();
        } finally {
            if (proActiveDescriptor != null) {
                proActiveDescriptor.kill();
            }

            PALifeCycle.exitSuccess();
        }

    }
}
