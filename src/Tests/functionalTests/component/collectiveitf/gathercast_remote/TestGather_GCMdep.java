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
package functionalTests.component.collectiveitf.gathercast_remote;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.etsi.uri.gcm.util.GCM;
import org.junit.After;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.proactive.core.component.adl.Registry;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.utils.OperatingSystem;

import functionalTests.ComponentTest;
import functionalTests.GCMFunctionalTest;


public class TestGather_GCMdep extends ComponentTest {

    protected GCMApplication newDeploymentDescriptor = null;
    protected ProActiveDescriptor oldDeploymentDescriptor = null;

    @org.junit.Test
    public void testRemoteGathercast() throws Exception {

        URL descriptorPath = TestGather_GCMdep.class
                .getResource("/functionalTests/component/deployment/applicationDescriptor.xml");

        VariableContractImpl vc = super.getVariableContract();
        vc.setVariableFromProgram(GCMFunctionalTest.VC_HOSTCAPACITY, Integer.toString(4),
                VariableContractType.DescriptorDefaultVariable);
        vc.setVariableFromProgram(GCMFunctionalTest.VC_VMCAPACITY, Integer.toString(1),
                VariableContractType.DescriptorDefaultVariable);

        newDeploymentDescriptor = PAGCMDeployment.loadApplicationDescriptor(descriptorPath, vc);

        newDeploymentDescriptor.startDeployment();

        useRemoteGathercastItf(newDeploymentDescriptor);
    }

    protected void useRemoteGathercastItf(Object deploymentDesc) throws Exception {
        Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
        Map<String, Object> context = new HashMap<String, Object>();

        context.put("deployment-descriptor", deploymentDesc);
        Component gatherCmpServer, gatherCmpClient;

        // instantiate components
        System.out.println("\nInstantiate components...");
        gatherCmpClient = (Component) f.newComponent(
                "functionalTests.component.collectiveitf.gathercast_remote.GatherCmp", context);
        gatherCmpServer = (Component) f.newComponent(
                "functionalTests.component.collectiveitf.gathercast_remote.GatherCmp", context);

        // bind components
        System.out.println("\nBind components...");
        BindingController bc = GCM.getBindingController(gatherCmpClient);
        // binding
        bc.bindFc("sender", gatherCmpServer.getFcInterface("receiver"));

        // start and launch components
        System.out.println("\nLaunch components...");
        ((Runnable) gatherCmpClient.getFcInterface("main")).run();

        System.out.println("\nStart components...");
        GCM.getGCMLifeCycleController(gatherCmpClient).startFc();
        GCM.getGCMLifeCycleController(gatherCmpServer).startFc();
    }

    @After
    public void endTest() throws Exception {
        Registry.instance().clear();
        if (newDeploymentDescriptor != null) {
            newDeploymentDescriptor.kill();
        }

        if (oldDeploymentDescriptor != null) {
            oldDeploymentDescriptor.killall(false);
        }
    }

}
