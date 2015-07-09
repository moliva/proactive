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
package org.objectweb.proactive.examples.jmx.remote.management.events;

import java.io.IOException;
import java.io.Serializable;

import javax.management.InstanceNotFoundException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.examples.jmx.remote.management.client.entities.ManageableEntity;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


@ActiveObject
public class ActiveNotificationListener implements Serializable, NotificationListener {

    /**
     *
     */
    public ActiveNotificationListener() {
    }

    public void handleNotification(Notification notification, Object handback) {
        try {
            EntitiesEventManager.getInstance().handleNotification(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenTo(ManageableEntity entity) {
        ObjectName name = entity.getObjectName();
        ProActiveConnection connection = entity.getConnection();
        try {
            connection.addNotificationListener(name, (NotificationListener) PAActiveObject.getStubOnThis(),
                    null, null);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
