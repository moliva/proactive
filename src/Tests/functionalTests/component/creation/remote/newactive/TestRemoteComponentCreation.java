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
package functionalTests.component.creation.remote.newactive;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Assert;
import org.junit.Ignore;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.node.Node;

import functionalTests.ComponentTestDefaultNodes;
import functionalTests.component.creation.ComponentA;
import functionalTests.component.creation.ComponentInfo;


/**
 * @author The ProActive Team
 *
 * creates a primitive component on a remote node with ACs
 */
public class TestRemoteComponentCreation extends ComponentTestDefaultNodes {

    public TestRemoteComponentCreation() throws ProActiveException {

        super(1, 1);
        //        super("Creation of a primitive component on a remote node",
        //                "Test newActiveComponent method for a primitive component on a remote node");
    }

    /**
     * @see testsuite.test.FunctionalTest#action()
     */
    @org.junit.Test
    public void primitiveCreation() throws Exception {
        Component boot = Utils.getBootstrapComponent();
        GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);
        PAGenericFactory cf = Utils.getPAGenericFactory(boot);

        Node remoteNode = super.getANode();
        String remoteHost = remoteNode.getVMInformation().getHostName();
        Assert.assertTrue(remoteHost != null);

        Component componentA = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] { type_factory
                .createFcItfType("componentInfo", ComponentInfo.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE) }), new ControllerDescription(
            "componentA", Constants.PRIMITIVE), new ContentDescription(ComponentA.class.getName(),
            new Object[] { "toto" }), remoteNode);
        //logger.debug("OK, instantiated the component");
        // start the component!
        GCM.getGCMLifeCycleController(componentA).startFc();
        ComponentInfo ref = (ComponentInfo) componentA.getFcInterface("componentInfo");
        String name = ref.getName();
        String nodeUrl = ref.getNodeUrl();

        Assert.assertEquals(name, "toto");
        Assert.assertEquals(remoteNode.getNodeInformation().getURL(), nodeUrl);
    }

    @org.junit.Test
    @Ignore
    public void compositeCreation() throws Exception {
        Component boot = Utils.getBootstrapComponent();
        GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);
        PAGenericFactory cf = Utils.getPAGenericFactory(boot);

        Node remoteNode = super.getANode();
        String remoteHost = remoteNode.getVMInformation().getHostName();
        Assert.assertTrue(remoteHost != null);

        Component primitiveA = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] { type_factory
                .createFcItfType("componentInfo", ComponentInfo.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE) }), new ControllerDescription(
            "componentA", Constants.PRIMITIVE), new ContentDescription(ComponentA.class.getName(),
            new Object[] { "toto" }));

        Component compositetA = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] { type_factory
                .createFcItfType("componentInfo", ComponentInfo.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE) }), new ControllerDescription(
            "compositetA", Constants.COMPOSITE), null, remoteNode);
        //logger.debug("OK, instantiated the component");
        // start the component!
        GCM.getContentController(compositetA).addFcSubComponent(primitiveA);

        GCM.getGCMLifeCycleController(compositetA).startFc();
        ComponentInfo ref = (ComponentInfo) compositetA.getFcInterface("componentInfo");
        String name = ref.getName();
        String nodeUrl = ((ComponentInfo) compositetA.getFcInterface("componentInfo")).getNodeUrl();

        Assert.assertEquals(name, "toto");
        Assert.assertTrue(nodeUrl.indexOf(remoteHost) != -1);
    }
}
