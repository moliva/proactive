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
package org.objectweb.proactive.examples.jmx.remote.management.client.entities;

import java.io.Serializable;
import java.util.ArrayList;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.util.wrapper.GenericTypeWrapper;
import org.objectweb.proactive.examples.jmx.remote.management.events.EntitiesEventManager;
import org.objectweb.proactive.examples.jmx.remote.management.mbean.BundleInfo;
import org.objectweb.proactive.examples.jmx.remote.management.status.Status;
import org.objectweb.proactive.examples.jmx.remote.management.utils.Constants;


/***
 *
 * @author The ProActive Team
 *
 */
public class RemoteBundle extends ManageableEntity implements Serializable {

    /**
     *
     */
    private ArrayList<RemoteService> services = new ArrayList<RemoteService>();
    private ObjectName on;
    private BundleInfo info;
    private long id;
    private RemoteGateway gateway;
    private ManageableEntity parent;
    private ProActiveConnection connection;

    public RemoteBundle() {
    }

    public RemoteBundle(BundleInfo bInfo, RemoteGateway gateway, ManageableEntity parent) {
        this.info = bInfo;
        this.gateway = gateway;
        this.parent = parent;
        if (parent != null) {
            this.connection = parent.getConnection();
        }
        try {
            this.on = new ObjectName(Constants.OSGI_JMX_PATH + "type=bundles,url=" + gateway.getUrl() +
                ",name=" + this.info.getName());
            EntitiesEventManager.getInstance().listenTo(this);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public RemoteBundle(BundleInfo bInfo) {
        this(bInfo, null, null);
    }

    public void setParent(RemoteGateway gateway) {
        this.parent = gateway;
    }

    @Override
    public Object[] getChildren() {
        return this.services.toArray();
    }

    @Override
    public ManageableEntity getParent() {
        return this.parent;
    }

    @Override
    public boolean hasChildren() {
        return this.services.size() > 0;
    }

    public Status start() {
        @SuppressWarnings("unchecked")
        GenericTypeWrapper<Status> status = (GenericTypeWrapper<Status>) this.connection.invokeAsynchronous(
                this.on, "start", new Object[] { this.gateway.getIdTransaction() }, new String[] { Long.class
                        .getName() });
        return status.getObject();
    }

    public Status stop() {
        @SuppressWarnings("unchecked")
        GenericTypeWrapper<Status> status = (GenericTypeWrapper<Status>) this.connection.invokeAsynchronous(
                this.on, "stop", new Object[] { this.gateway.getIdTransaction() }, new String[] { Long.class
                        .getName() });
        return status.getObject();
    }

    public Status update() {
        @SuppressWarnings("unchecked")
        GenericTypeWrapper<Status> status = (GenericTypeWrapper<Status>) this.connection.invokeAsynchronous(
                this.on, "update", new Object[] { this.gateway.getIdTransaction() },
                new String[] { Long.class.getName() });
        return status.getObject();
    }

    public Status uninstall() {
        @SuppressWarnings("unchecked")
        GenericTypeWrapper<Status> status = (GenericTypeWrapper<Status>) this.connection.invokeAsynchronous(
                this.on, "uninstall", new Object[] { this.gateway.getIdTransaction() },
                new String[] { Long.class.getName() });
        return status.getObject();
    }

    @Override
    public void addEntity(ManageableEntity entity) {
        this.services.add((RemoteService) entity);
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub
    }

    @Override
    public String toString() {
        return this.info.getName();
    }

    @Override
    public String getName() {
        return this.info.getName();
    }

    @Override
    public void removeEntity(ManageableEntity entity) {
    }

    public long getID() {
        return this.info.getId();
    }

    public long getState() {
        return this.info.getState();
    }

    @Override
    public ProActiveConnection getConnection() {
        return this.connection;
    }

    @Override
    public ObjectName getObjectName() {
        return this.on;
    }

    @Override
    public String getUrl() {
        return this.parent.getUrl();
    }

    public void setBundleInfo(BundleInfo info) {
        this.info = info;
        EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.BUNDLE_UPDATED);
    }
}
