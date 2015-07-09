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
package org.objectweb.proactive.core.runtime;

import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.URIBuilder;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * <p>
 * This class is a utility class allowing to start a ProActiveRuntime with a JVM.
 * </p><p>
 * This class is mainly used with ProActiveDescriptor to start a ProActiveRuntime
 * on a local or remote JVM.
 * </p>
 *
 * @author The ProActive Team
 * @version 1.0,  2002/08/29
 * @since   ProActive 0.9
 *
 */
public class StartRuntime {
    static Logger logger = null;

    /** The VirtualNode that started this ProActive Runtime */
    protected String creatorID;

    /** The URL of the parent ProActive Runtime */
    protected String defaultRuntimeURL;

    /** Name of the associated VirtualMachine */
    protected String vmName;

    protected StartRuntime() {
    }

    private StartRuntime(String[] args) {
        if (args.length != 0) {
            this.creatorID = args[0].trim();
            this.defaultRuntimeURL = URIBuilder.removeUsername(args[1]);
            this.vmName = args[2];
        }
    }

    public static void main(String[] args) {
        // CentralProperties cannot be used since we do not want to use log4j
        String defaultInitOverride = System.getProperty("log4j.defaultInitOverride");
        String log4jFile = System.getProperty("log4j.configuration");

        if ("true".equals(defaultInitOverride) && (log4jFile != null)) {
            // configure log4j here to avoid classloading problems with log4j classes
            try {
                URI log4jConfigurationURI = URI.create(log4jFile);
                PropertyConfigurator.configure(log4jConfigurationURI.toURL());
            } catch (IOException e) {
                System.out.println("Error : incorrect path for log4j configuration : " + log4jFile);
            }
        }
        //        System.out.println("log4j is ready");
        logger = ProActiveLogger.getLogger(Loggers.RUNTIME);

        ProActiveConfiguration.load();

        logger.info("**** Starting jvm on " +
            URIBuilder.getHostNameorIP(ProActiveInet.getInstance().getInetAddress()));

        if (logger.isDebugEnabled()) {
            logger.debug("**** Starting jvm with classpath " + System.getProperty("java.class.path"));
            logger.debug("****              with bootclasspath " + System.getProperty("sun.boot.class.path"));
        }

        new StartRuntime(args).run();
        if (CentralPAPropertyRepository.PA_RUNTIME_STAYALIVE.isTrue()) {
            Object o = new Object();
            synchronized (o) {
                try {
                    o.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * <i><font size="-1" color="#FF0000">**For internal use only** </font></i>
     * Runs the complete creation and registration of a ProActiveRuntime and creates a
     * node once the creation is completed.
     */
    private void run() {
        ProActiveRuntimeImpl impl = ProActiveRuntimeImpl.getProActiveRuntime();
        impl.setVMName(this.vmName);

        if (this.defaultRuntimeURL != null) {
            ProActiveRuntime PART;
            try {
                PART = RuntimeFactory.getRuntime(this.defaultRuntimeURL);

                register(PART);
                impl.setParent(PART);

                Object o = new Object();
                synchronized (o) {
                    try {
                        o.wait();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (ProActiveException e) {
                e.printStackTrace();
                //				 if we cannot get the parent, this jvm is useless
                System.exit(0);
            }
        }
    }

    /**
     * <i><font size="-1" color="#FF0000">**For internal use only** </font></i>
     * Performs the registration of a ProActiveRuntime on the runtime that initiated the creation
     * of ProActiveDescriptor.
     */
    private void register(ProActiveRuntime PART) {
        try {
            ProActiveRuntime proActiveRuntime = RuntimeFactory
                    .getProtocolSpecificRuntime(CentralPAPropertyRepository.PA_COMMUNICATION_PROTOCOL
                            .getValue());

            PART.register(proActiveRuntime, proActiveRuntime.getURL(), this.creatorID,
                    CentralPAPropertyRepository.PA_COMMUNICATION_PROTOCOL.getValue(), this.vmName);
        } catch (ProActiveException e) {
            e.printStackTrace();

            // if we cannot register, this jvm is useless
            System.exit(0);
        }
    }
}
