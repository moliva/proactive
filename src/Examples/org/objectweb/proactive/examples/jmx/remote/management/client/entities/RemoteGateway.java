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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.util.wrapper.GenericTypeWrapper;
import org.objectweb.proactive.examples.jmx.remote.management.client.entities.Refesher.GatewayRefresher;
import org.objectweb.proactive.examples.jmx.remote.management.client.jmx.FrameworkConnection;
import org.objectweb.proactive.examples.jmx.remote.management.events.EntitiesEventManager;
import org.objectweb.proactive.examples.jmx.remote.management.mbean.BundleInfo;
import org.objectweb.proactive.examples.jmx.remote.management.status.Status;
import org.objectweb.proactive.examples.jmx.remote.management.utils.Constants;


/**
 *
 * @author The ProActive Team
 *
 */
public class RemoteGateway extends ManageableEntity implements Serializable, RemoteEntity, Transactionnable {
    private ObjectName on;
    private String url;
    private transient FrameworkConnection fwConnection;
    private ManageableEntity parent;
    private ProActiveConnection connection;
    private long idTransaction = 0;
    private transient List<RemoteBundle> bundles = new ArrayList<RemoteBundle>();
    private transient Map<String, RemoteBundle> namesBundles = new HashMap<String, RemoteBundle>();
    private RemoteTransaction transaction;
    private boolean connected;
    private int port;

    public RemoteGateway() {
    }

    public long getIdTransaction() {
        return this.idTransaction;
    }

    public RemoteGateway(ManageableEntity parent, String url) {
        try {
            this.parent = parent;
            this.url = url;
            this.port = Integer.parseInt(this.url.substring(this.url.lastIndexOf(':') + 1));
            this.parent.addEntity(this);

            this.fwConnection = new FrameworkConnection(this.url);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object[] getChildren() {
        return this.bundles.toArray();
    }

    @Override
    public ManageableEntity getParent() {
        return this.parent;
    }

    @Override
    public boolean hasChildren() {
        return this.bundles.size() > 0;
    }

    @Override
    public Status executeCommand(String command) {
        return null;
    }

    public Status installBundle(String location) throws IOException {
        @SuppressWarnings("unchecked")
        GenericTypeWrapper<Status> ow = (GenericTypeWrapper<Status>) this.connection.invokeAsynchronous(
                this.on, "installBundle", new Object[] { this.idTransaction, location }, new String[] {
                        Long.TYPE.getName(), "java.lang.String" });
        if (ow.getObject().containsErrors()) {
            throw new IOException(ow.getObject().getMessage());
        }

        return ow.getObject();
    }

    @Override
    public void addEntity(ManageableEntity entity) {
        RemoteBundle remoteBundle = (RemoteBundle) entity;
        this.bundles.add(remoteBundle);
        this.namesBundles.put(remoteBundle.getName(), remoteBundle);

        EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.ENTITY_ADDED);
    }

    @Override
    public void connect() throws IOException {
        if (!this.connected) {
            this.fwConnection.connect();
            this.connection = this.fwConnection.getConnection();
            this.connected = true;
            EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.GATEWAY_CONNECTED);
            refresh();
        }
    }

    @Override
    public void refresh() {
        try {
            GatewayRefresher refresher = new GatewayRefresher(this, this.fwConnection);
            refresher.launch();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove() {
        try {
            if (fwConnection.isConnected()) {
                fwConnection.close();
            }
            this.parent.remove(this);

            this.bundles.clear();
            this.namesBundles.clear();
            this.parent.removeEntity(this);
            EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.GATEWAY_REMOVED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the on
     */
    public ObjectName getOn() {
        return on;
    }

    public void setBundles(List<BundleInfo> bInfo) {
        for (BundleInfo info : bInfo) {
            RemoteBundle b = new RemoteBundle(info, this, this);
            addEntity(b);
            this.namesBundles.put(info.getName(), b);
        }
    }

    @Override
    public String getName() {
        return this.url;
    }

    @Override
    public void removeEntity(ManageableEntity entity) {
        // TODO Auto-generated method stu
    }

    @Override
    public String toString() {
        return this.url;
    }

    @Override
    public Status cancelTransaction() {
        try {
            ObjectName tmName = new ObjectName("Transactions:id=" + this.idTransaction);
            @SuppressWarnings("unchecked")
            GenericTypeWrapper<Status> ow = (GenericTypeWrapper<Status>) this.connection.invokeAsynchronous(
                    tmName, "rollback", new Object[] {}, new String[] {});
            return ow.getObject();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Status commitTransaction() {
        try {
            ObjectName tmName = new ObjectName("Transactions:id=" + this.idTransaction);
            @SuppressWarnings("unchecked")
            GenericTypeWrapper<Status> ow = (GenericTypeWrapper<Status>) this.connection.invokeAsynchronous(
                    tmName, "commit", new Object[] {}, new String[] {});
            return ow.getObject();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Status openTransaction() {
        ObjectName tmName;
        try {
            tmName = new ObjectName(Constants.ON_TRANSACTION_MANAGER);
            @SuppressWarnings("unchecked")
            GenericTypeWrapper<Long> ow = (GenericTypeWrapper<Long>) this.connection.invokeAsynchronous(
                    tmName, "openTransaction", new Object[] {}, new String[] {});

            this.idTransaction = ow.getObject().longValue();
            this.transaction = new RemoteTransaction(idTransaction, this.url, this);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Status();
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
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url; // + '(' + this.port + ')' ;

        String path = Constants.OSGI_JMX_PATH + Constants.ON_GATEWAYS + this.url;
        try {
            this.on = new ObjectName(path);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        EntitiesEventManager.getInstance().listenTo(this);
    }

    public void addBundle(BundleInfo b) {
        RemoteBundle bundle = new RemoteBundle(b, this, this);
        addEntity(bundle);
    }

    public void updateBundle(BundleInfo info) {
        try {
            RemoteBundle remoteBundle = this.namesBundles.get(info.getName());
            if (remoteBundle != null) {
                remoteBundle.setBundleInfo(info);
            }

            EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.BUNDLE_UPDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeBundle(BundleInfo info) {
        for (RemoteBundle rb : this.bundles) {
            if (rb.getName().equals(info.getName())) {
                this.bundles.remove(rb);
                this.namesBundles.remove(rb.getName());
                break;
            }
            EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.ENTITY_REMOVED);
        }

        boolean b = this.bundles.remove(info);
    }

    public boolean isConnected() {
        return this.connected;
    }
}
