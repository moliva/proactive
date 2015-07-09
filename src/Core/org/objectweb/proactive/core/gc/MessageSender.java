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
package org.objectweb.proactive.core.gc;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.LocalBodyStore;


/**
 * When many objects in a VM reference a same remote object, instead
 * of having each referencer spawn a thread to send a GC Message, the
 * GC messages are aggregated and sent once. This reduces the load.
 *
 * When the target object is in the same VM, we don't go through RMI
 * or anything else, we directly deliver the message.
 */
public class MessageSender {
    static void sendMessages(Collection<GCSimpleMessage> messages) {
        Map<Referenced, GCMessage> queue = new TreeMap<Referenced, GCMessage>();
        for (GCSimpleMessage m : messages) {
            Referenced destination = m.getReferenced();
            UniqueID destId = destination.getBodyID();
            Body body = LocalBodyStore.getInstance().getLocalBody(destId);
            if (body != null) {

                /* Local body, directly deliver */
                GCSimpleResponse resp = ((AbstractBody) body).getGarbageCollector().receiveSimpleGCMessage(m);
                destination.setLastResponse(resp);
                continue;
            }

            GCMessage compound = queue.get(destination);
            if (compound == null) {
                compound = new GCMessage();
                queue.put(destination, compound);
            }

            compound.add(m);
        }

        for (Map.Entry<Referenced, GCMessage> entry : queue.entrySet()) {
            entry.getKey().sendMessage(entry.getValue());
        }
    }
}
