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
package org.objectweb.proactive.examples.jmx.remote.management.log;

import java.util.Enumeration;
import java.util.Vector;

import org.objectweb.proactive.examples.jmx.remote.management.client.entities.RemoteEntity;
import org.objectweb.proactive.examples.jmx.remote.management.events.EntitiesEventListener;


public class EventLog implements EntitiesEventListener {
    private Vector<EventLogListener> listeners = new Vector<EventLogListener>();
    private static EventLog instance;

    private EventLog() {
        //		EntitiesEventManager.getInstance().subscribeAll(this);
    }

    public static EventLog getInstance() {
        if (instance == null) {
            instance = new EventLog();
        }
        return instance;
    }

    public void handleEntityEvent(RemoteEntity entity, String message, Object bundle) {
        String msg = "[" + message + "] " + entity;
        firelisteners(msg);
    }

    private void firelisteners(String message) {
        Enumeration<EventLogListener> e = this.listeners.elements();
        while (e.hasMoreElements()) {
            ((EventLogListener) e.nextElement()).handleLogEvent(message);
        }
    }

    public void addEventLogListener(EventLogListener listener) {
        this.listeners.addElement(listener);
    }

    public void removeEventLogListener(EventLogListener listener) {
        this.listeners.removeElement(listener);
    }

    public void handleEntityEvent(Object source, String message) {
        // TODO Auto-generated method stub
    }
}
