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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package functionalTests.pamr.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extensions.pamr.client.AgentImpl;
import org.objectweb.proactive.extensions.pamr.client.MessageHandler;
import org.objectweb.proactive.extensions.pamr.exceptions.PAMRException;
import org.objectweb.proactive.extensions.pamr.protocol.AgentID;
import org.objectweb.proactive.extensions.pamr.protocol.MagicCookie;
import org.objectweb.proactive.extensions.pamr.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extensions.pamr.protocol.message.DebugMessage;
import org.objectweb.proactive.extensions.pamr.protocol.message.DebugMessage.DebugType;
import org.objectweb.proactive.extensions.pamr.protocol.message.Message;
import org.objectweb.proactive.extensions.pamr.remoteobject.util.socketfactory.PAMRPlainSocketFactory;
import org.objectweb.proactive.utils.Sleeper;

import functionalTests.pamr.BlackBox;


public class ClientIOException extends BlackBox {
    int port;

    @Before
    public void before() throws IOException {
    }

    @Test
    public void test() throws UnknownHostException, ProActiveException, PAMRException {
        InetAddress localhost = InetAddress.getLocalHost();
        Agent agent = new Agent(localhost, super.router.getPort(), FakeMessageHandler.class);
        AgentID agentId = agent.getAgentID();

        Message message;

        message = new DebugMessage(agentId, 1, DebugType.DEB_NOOP);
        agent.sendMsg(message);
        message = new DebugMessage(agentId, 1, DebugType.DEB_DISCONNECT);
        agent.sendMsg(message);

        new Sleeper(1000).sleep();
        message = new DebugMessage(agentId, 1, DebugType.DEB_NOOP);
        agent.sendMsg(message);

        new Sleeper(500000).sleep();
        System.out.println("toto");
    }

    static public class FakeMessageHandler implements MessageHandler {

        public FakeMessageHandler(AgentImpl agentV2Internal) {
        }

        public void pushMessage(DataRequestMessage message) {
            // Mock
        }
    }

    static public class Agent extends AgentImpl {

        public Agent(InetAddress routerAddr, int routerPort,
                Class<? extends MessageHandler> messageHandlerClass) throws ProActiveException {
            super(routerAddr, routerPort, null, new MagicCookie(), messageHandlerClass,
                    new PAMRPlainSocketFactory());
        }

        public void sendMsg(Message message) throws PAMRException {
            super.internalSendMsg(message);
        }

    }
}
