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
package org.objectweb.proactive.extensions.pamr.router.processor;

import java.nio.ByteBuffer;

import org.objectweb.proactive.extensions.pamr.exceptions.MalformedMessageException;
import org.objectweb.proactive.extensions.pamr.protocol.AgentID;
import org.objectweb.proactive.extensions.pamr.protocol.message.DataMessage;
import org.objectweb.proactive.extensions.pamr.protocol.message.DataReplyMessage;
import org.objectweb.proactive.extensions.pamr.protocol.message.Message.MessageType;
import org.objectweb.proactive.extensions.pamr.router.Client;
import org.objectweb.proactive.extensions.pamr.router.RouterImpl;


/** Asynchronous handler for {@link MessageType#DATA_REPLY}
 * 
 * @since ProActive 4.1.0
 */
public class ProcessorDataReply extends Processor {

    public ProcessorDataReply(ByteBuffer messageAsByteBuffer, RouterImpl router) {
        super(messageAsByteBuffer, router);
    }

    @Override
    public void process() throws MalformedMessageException {
        try {
            DataReplyMessage replyMsg = new DataReplyMessage(this.rawMessage.array(), 0);

            AgentID sender = replyMsg.getSender();
            Client sendClient = this.router.getClient(sender);
            if (sendClient != null) {
                sendClient.updateLastSeen();
            }

            AgentID recipient = replyMsg.getRecipient();
            Client destClient = this.router.getClient(recipient);

            if (destClient != null) {
                destClient.sendMessageOrCache(this.rawMessage);
            } else {
                /* unknown recipient => malformed message(or attack)
                 * anyway, inform the sender, maybe the next message will be a valid one
                 * and we will unlock the recipient
                 */
                throw new MalformedMessageException("Invalid data reply message " + replyMsg +
                    " : unknown recipient.");
            }
        } catch (MalformedMessageException e) {
            AgentID sender;
            AgentID recipient;
            try {
                sender = DataMessage.readSender(this.rawMessage.array(), 0);
            } catch (MalformedMessageException e1) {
                // don't know the sender
                sender = null;
            }
            try {
                recipient = DataMessage.readRecipient(this.rawMessage.array(), 0);
            } catch (MalformedMessageException e1) {
                // don't know the recipient
                recipient = null;
            }
            throw new MalformedMessageException(e, sender, recipient);
        }
    }
}
