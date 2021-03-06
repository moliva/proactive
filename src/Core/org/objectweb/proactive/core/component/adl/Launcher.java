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
package org.objectweb.proactive.core.component.adl;

import java.util.HashMap;
import java.util.Map;

import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.adl.FactoryFactory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;


/**
 * A class to launch a component from its definition from the command line. <br>
 * Usage: Launcher [-java|-fractal] &lt;definition&gt; [ &lt;itf&gt; ]
 * [deployment-descriptor]) where &lt;definition&gt; is the name of the
 * component to be instantiated and started, &lt;itf&gt; is the name of its
 * Runnable interface, if it has one, and deployment-descriptor the location of
 * the ProActive deployment descriptor to use. <br>
 * It is also possible to use this class directly from its static main method.
 */
public class Launcher {
    static ProActiveDescriptor deploymentDescriptor;

    private Launcher() {
    }

    public static void main(final String[] args) throws Exception {
        String[] pargs = parseArgs(args);
        Object comp = createComponent(pargs);

        if (comp instanceof Component) {
            LifeCycleController lc = null;
            try {
                lc = GCM.getGCMLifeCycleController((Component) comp);
            } catch (NoSuchInterfaceException ignored) {
            }
            if (lc != null) {
                lc.startFc();
            }
            Runnable r = null;
            try {
                r = (Runnable) ((Component) comp).getFcInterface(pargs[2]);
            } catch (NoSuchInterfaceException ignored) {
            }
            if (r != null) {
                r.run();
            }
        } else {
            if (comp instanceof LifeCycleController) {
                ((LifeCycleController) comp).startFc();
            }
            if (comp instanceof Runnable) {
                ((Runnable) comp).run();
            }
        }
    }

    public static Object createComponent(String[] pargs) throws Exception {
        if (pargs[0].equals("-java")) {
            Factory f = FactoryFactory.getFactory(FactoryFactory.JAVA_BACKEND);

            return ((Map) f.newComponent(pargs[1], new HashMap<Object, Object>())).get(pargs[2]);
        } else {
            Factory f;
            if (Fractive.class.getName().equals(CentralPAPropertyRepository.GCM_PROVIDER.getValue())) {
                // return the ProActive factory as defined in
                // org.objectweb.proactive.core.component.adl.FactoryFactory
                f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
            } else {
                f = FactoryFactory.getFactory(FactoryFactory.FRACTAL_BACKEND);
            }

            // PROACTIVE
            if (pargs[3] != null) {
                deploymentDescriptor = PADeployment.getProactiveDescriptor(pargs[3]);
                Map<Object, Object> context = new HashMap<Object, Object>(1);
                context.put("deployment-descriptor", deploymentDescriptor);
                return f.newComponent(pargs[1], context);
            } else {
                try {
                    return f.newComponent(pargs[1], new HashMap<Object, Object>());
                } catch (ClassCastException e) {
                    if (e.getMessage().indexOf("attribute_controller_representative") != (-1)) {
                        System.out
                                .println("Error while parsing the ADL. This could be due to the setting of attributes without implementing AttributeController. ");
                        throw e;
                    } else {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
    }

    private static String[] parseArgs(final String[] args) {
        if ((args.length < 1) || (args.length > 4)) {
            parseError();
        }

        // PROACTIVE added a parameter for the deployment descriptor
        String[] result = new String[4];
        if (args[0].equals("-java") || args[0].equals("-fractal")) {
            if (args.length < 2) {
                parseError();
            }
            result[0] = args[0];
            result[1] = args[1];
            result[2] = ((args.length == 3) || (args.length == 4)) ? args[2] : "run";
            result[3] = (args.length == 4) ? args[3] : null;
        } else {
            result[0] = "-java";
            result[1] = args[0];
            result[2] = (args.length >= 2) ? args[1] : "run";
        }
        return result;
    }

    private static void parseError() {
        System.out.println("Usage: Launcher [-java|-fractal] <definition> [ <itf> ] [deployment-descriptor]");
        System.out.print("where <definition> is the name of the component to be ");
        System.out.print("instantiated and started,\n <itf> is the name of ");
        System.out.println("its Runnable interface, if it has one,");
        System.out
                .println("\nand [deployment-descriptor] is the deployment descriptor that should be used for ProActive");
        System.exit(1);
    }

    public static void killNodes(boolean softly) throws ProActiveException {
        deploymentDescriptor.killall(softly);
    }
}
