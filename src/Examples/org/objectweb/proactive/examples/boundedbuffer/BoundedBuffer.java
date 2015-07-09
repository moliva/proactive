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
 * Active BoundedBuffer
 */
@ActiveObject
public class BoundedBuffer implements org.objectweb.proactive.RunActive {
    private String[] buffer; // The buffer containing the datas
    private int size; // The buffer's capacity
    private int count; // The number of used cells
    private int in; // The next cell to be read
    private int out; // The next cell to be written
    private ActiveDisplay display;

    /**
     * The mandatory no-args constructor.
     */
    public BoundedBuffer() {
    }

    /**
     * The effective constructor
     */
    public BoundedBuffer(int size, ActiveDisplay display) {
        buffer = new String[size];
        count = 0;
        in = 0;
        out = 0;
        this.size = size;
        this.display = display;
    }

    /**
     * Put data in the buffer
     * Note that this method doesn't contain _any_ synchronization code
     */
    public String put(String str) {
        buffer[in] = str;
        count++;
        display.update(in, str);
        in = (in + 1) % size;
        display.setIn(in);
        return "ok";
    }

    /**
     * Get data from the buffer
     * Note that this method doesn't contain _any_ synchronization code
     */
    public String get() {
        String str;
        str = buffer[out];
        buffer[out] = null;
        display.update(out, null);
        out = (out + 1) % size;
        display.setOut(out);
        count--;
        return str;
    }

    /**
     * The only synchronization method
     */
    public void runActivity(org.objectweb.proactive.Body body) {
        org.objectweb.proactive.Service service = new org.objectweb.proactive.Service(body);
        while (body.isActive()) {
            if (count == 0) {
                // if the buffer is empty
                service.blockingServeOldest("put"); // Serve the first buffer.put call
            } else if (count == size) {
                // if the buffer is full
                service.blockingServeOldest("get"); // Serve the first buffer.get call
            } else {
                // if the buffer is neither empty nor full
                service.blockingServeOldest(); // Serve the first buffer.xxx call
            }
        }
    }
}
