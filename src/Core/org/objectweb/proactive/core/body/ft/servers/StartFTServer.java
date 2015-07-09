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
package org.objectweb.proactive.core.body.ft.servers;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import org.objectweb.proactive.core.body.ft.protocols.FTManagerFactory;
import org.objectweb.proactive.core.body.ft.protocols.cic.servers.CheckpointServerCIC;
import org.objectweb.proactive.core.body.ft.protocols.cic.servers.RecoveryProcessCIC;
import org.objectweb.proactive.core.body.ft.protocols.pmlrb.servers.CheckpointServerPMLRB;
import org.objectweb.proactive.core.body.ft.protocols.pmlrb.servers.RecoveryProcessPMLRB;
import org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetector;
import org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetectorImpl;
import org.objectweb.proactive.core.body.ft.servers.location.LocationServer;
import org.objectweb.proactive.core.body.ft.servers.location.LocationServerImpl;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcess;
import org.objectweb.proactive.core.body.ft.servers.resource.ResourceServer;
import org.objectweb.proactive.core.body.ft.servers.resource.ResourceServerImpl;
import org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer;
import org.objectweb.proactive.core.rmi.RegistryHelper;
import org.objectweb.proactive.core.util.ProActiveInet;


/**
 * This class is a main that creates and starts a ft.util.FTServer.
 * Usage : ~>startGlobalFTServer [-proto {cic|pml}] [-name name] [-port portnumber] [-fdperiod faultDetectionPeriod (sec)]
 * @author The ProActive Team
 * @since ProActive 2.2
 */
public class StartFTServer {
    public static void main(String[] args) {
        try {
            int port = 0;
            int fdPeriod = 0;
            String name = "";
            String proto = FTManagerFactory.PROTO_CIC;

            if (args.length == 0) {
                //                System.out
                //                        .println("Usage : startGlobalFTServer [-proto cic|pml] [-name name] [-port portnumber] [-fdperiod faultDetectionPeriod (sec)]");
                System.out
                        .println("Usage : startGlobalFTServer [-proto cic|pml] [-name name] [-port portnumber] [-fdperiod faultDetectionPeriod (sec)]");
            } else {
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-port")) {
                        port = Integer.parseInt(args[i + 1]);
                    } else if (args[i].equals("-fdperiod")) {
                        fdPeriod = Integer.parseInt(args[i + 1]);
                    } else if (args[i].equals("-name")) {
                        name = args[i + 1];
                    } else if (args[i].equals("-proto")) {
                        proto = args[i + 1];
                    }
                }
            }

            if (port == 0) {
                port = FTServer.DEFAULT_PORT;
            }
            if ("".equals(name)) {
                name = FTServer.DEFAULT_SERVER_NAME;
            }
            if (fdPeriod == 0) {
                fdPeriod = FTServer.DEFAULT_FDETECT_SCAN_PERIOD;
            }

            // rmi registry
            RegistryHelper registryHelper = new RegistryHelper();
            registryHelper.setRegistryPortNumber(port);
            registryHelper.initializeRegistry();

            System.setSecurityManager(new RMISecurityManager());

            // server init
            FTServer server = new FTServer();
            LocationServer ls = new LocationServerImpl(server);
            FaultDetector fd = new FaultDetectorImpl(server, fdPeriod);
            ResourceServer rs;

            // protocol specific
            CheckpointServer cs = null;
            RecoveryProcess rp = null;
            if (proto.equals(FTManagerFactory.PROTO_CIC)) {
                cs = new CheckpointServerCIC(server);
                rp = new RecoveryProcessCIC(server);
            } else if (proto.equals(FTManagerFactory.PROTO_PML)) {
                cs = new CheckpointServerPMLRB(server);
                rp = new RecoveryProcessPMLRB(server);
            } else {
                System.err.println("ERROR: " + proto + " is not a valid protocol. Aborting.");
                System.exit(1);
            }

            rs = new ResourceServerImpl(server);

            // init
            server.init(fd, ls, rp, rs, cs);
            server.startFailureDetector();

            String host = ProActiveInet.getInstance().getInetAddress().getHostName();
            Naming.rebind("rmi://" + host + ":" + port + "/" + name, server);
            System.out.println("Fault-tolerance server is bound on rmi://" + host + ":" + port + "/" + name);
        } catch (RemoteException e) {
            System.err.println("** ERROR ** Unable to launch FT server : ");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.err.println("** ERROR ** Unable to launch FT server : ");
            e.printStackTrace();
        }
    }
}
