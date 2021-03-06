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
package org.objectweb.proactive.examples.jmx.remote.management.client.entities.Refesher;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.util.wrapper.GenericTypeWrapper;
import org.objectweb.proactive.examples.jmx.remote.management.client.entities.RemoteGateway;
import org.objectweb.proactive.examples.jmx.remote.management.client.jmx.FrameworkConnection;
import org.objectweb.proactive.examples.jmx.remote.management.mbean.BundleInfo;
import org.objectweb.proactive.examples.jmx.remote.management.utils.Constants;


public class GatewayRefresher {

    private RemoteGateway gateway;

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(50, 60, 60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>());
    private FrameworkConnection connection;

    public GatewayRefresher(RemoteGateway gw, FrameworkConnection connection) {
        this.gateway = gw;
        this.connection = connection;
    }

    public void launch() {
        executor.execute(new MyRunnable(gateway));
    }

    private class MyRunnable implements Runnable {
        private RemoteGateway gateway;

        public MyRunnable(RemoteGateway rg) {
            this.gateway = rg;
        }

        public void run() {
            if (connection.isConnected()) {
                ProActiveConnection paConn = connection.getConnection();

                ObjectName onUrl;
                try {
                    onUrl = new ObjectName(Constants.ON_URL);

                    String url = paConn.getAttributeAsynchronous(onUrl, "Url").getObject().toString();
                    gateway.setUrl(url);

                    GenericTypeWrapper<?> gtw = paConn.getAttributeAsynchronous(gateway.getOn(), "Bundles");

                    PAFuture.waitFor(gtw);
                    Object o = gtw.getObject();
                    if (o instanceof List) {
                        gateway.setBundles((List<BundleInfo>) o);
                    } else if (o instanceof Exception) {
                        System.out.println("Exception recue");
                        ((Exception) o).printStackTrace();
                    }
                } catch (MalformedObjectNameException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
