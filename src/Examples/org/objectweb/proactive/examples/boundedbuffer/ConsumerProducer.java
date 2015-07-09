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
package org.objectweb.proactive.examples.boundedbuffer;

import org.objectweb.proactive.extensions.annotation.ActiveObject;


/**
 * Simple Active consumer
 */
@ActiveObject
public abstract class ConsumerProducer implements org.objectweb.proactive.RunActive {
    protected String name;
    protected ConsumerProducerListener listener;
    protected BoundedBuffer buffer;
    protected boolean isActive;
    protected boolean isSuspended;
    protected boolean requestChange;

    /**
     * The mandatory empty, no-arg constructor.
     */
    public ConsumerProducer() {
    }

    /**
     * The effective constructor
     */
    public ConsumerProducer(String name, ConsumerProducerListener listener, BoundedBuffer buffer) {
        this.name = name;
        this.buffer = buffer;
        this.listener = listener;
        requestChange = false;
        isSuspended = true;
        isActive = true;
    }

    /**
     * Toggle the consumer or producer 's activity
     */
    public void toggle() {
        requestChange = true;
    }

    /**
     * Kill the consumer/producer
     */
    public void done() {
        isActive = false;
    }

    /**
     * The only synchronization method
     */
    public void runActivity(org.objectweb.proactive.Body body) {
        org.objectweb.proactive.Service service = new org.objectweb.proactive.Service(body);
        while (isActive) {
            // Allow the display to toggle or kill the consumer
            service.serveOldest();
            boolean wasSuspended = isSuspended;
            if (requestChange) {
                isSuspended = !isSuspended;
                requestChange = false;
            }

            // Try to get some datas
            doStuff(wasSuspended);
            long l = 500 + (long) (Math.random() * 1000);

            //listener.displayMessage(name+" now sleeping for "+l+" ms");
            try {
                Thread.sleep(l);
            } catch (InterruptedException e) {
            }
        }
    }

    protected abstract void doStuff(boolean wasSuspended);
}
