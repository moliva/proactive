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
package functionalTests.component.wsbindings;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extensions.webservices.AbstractWebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.gcmdeployment.GCMApplication;

import functionalTests.GCMFunctionalTest;


/**
 * Test CXF web service binding.
 *
 * @author The ProActive Team
 */
// TODO Uncomment multicast parts (in this file and in adl/Client.fractal and adl/Composite.fractal) when PROACTIVE-743 has been fixed
public class TestCXFWSBindings extends CommonSetup {
    @Before
    public void setUpAndDeploy() throws Exception {
        wsf = AbstractWebServicesFactory.getWebServicesFactory("cxf");
        super.setUpAndDeploy();
    }

    @Test
    public void testCXFWebServiceBindingsWithPrimitiveComponent() throws Exception {
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        GCM.getBindingController(client).bindFc(Client.SERVICES_NAME,
                url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME);
        for (int i = 0; i < NUMBER_SERVERS; i++) {
            GCM.getBindingController(client).bindFc(
                    Client.SERVICEMULTICASTREAL_NAME,
                    url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + i + "_" +
                        SERVER_SERVICEMULTICAST_NAME);
        }
        GCM.getGCMLifeCycleController(client).startFc();
        Runner runner = (Runner) client.getFcInterface("Runner");
        Assert.assertTrue("Failed to invoke web services with primitive component", runner.execute()
                .getBooleanValue());
    }

    @Test
    public void testCXFWebServiceBindingsWithCompositeComponent() throws Exception {
        Component composite = gf.newFcInstance(componentType, new ControllerDescription("Composite",
            Constants.COMPOSITE), null);
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        GCM.getContentController(composite).addFcSubComponent(client);
        GCM.getBindingController(composite).bindFc("Runner", client.getFcInterface("Runner"));
        GCM.getBindingController(client).bindFc(Client.SERVICES_NAME,
                composite.getFcInterface(Client.SERVICES_NAME));
        //        GCM.getBindingController(client).bindFc(Client.SERVICEMULTICASTFALSE_NAME,
        //                composite.getFcInterface(Client.SERVICEMULTICASTREAL_NAME));
        GCM.getBindingController(composite).bindFc(Client.SERVICES_NAME,
                url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME);
        //        for (int i = 0; i < NUMBER_SERVERS; i++) {
        //            GCM.getBindingController(composite).bindFc(
        //                    Client.SERVICEMULTICASTREAL_NAME,
        //                    url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + i + "_" +
        //                        SERVER_SERVICEMULTICAST_NAME);
        //        }
        GCM.getGCMLifeCycleController(composite).startFc();
        Runner runner = (Runner) composite.getFcInterface("Runner");
        Assert.assertTrue("Failed to invoke web services with composite component", runner.execute()
                .getBooleanValue());
    }

    @Test
    public void testCXFWebServiceBindingsWithADL() throws Exception {
        Factory factory = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
        Map<Object, Object> context = new HashMap<Object, Object>();
        Component composite = (Component) factory.newComponent(
                "functionalTests.component.wsbindings.adl.Composite", context);
        GCM.getGCMLifeCycleController(composite).startFc();
        Runner runner = (Runner) composite.getFcInterface("Runner");
        Assert.assertTrue("Failed to invoke web services with composite component", runner.execute()
                .getBooleanValue());
    }

    @Test
    public void testMigrationWithCXFWebServiceBindings() throws Exception {
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        GCM.getBindingController(client).bindFc(Client.SERVICES_NAME,
                url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME);
        for (int i = 0; i < NUMBER_SERVERS; i++) {
            GCM.getBindingController(client).bindFc(
                    Client.SERVICEMULTICASTREAL_NAME,
                    url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + i + "_" +
                        SERVER_SERVICEMULTICAST_NAME);
        }
        GCM.getGCMLifeCycleController(client).startFc();
        URL descriptorPath = functionalTests.component.deployment.Test.class
                .getResource("/functionalTests/component/deployment/applicationDescriptor.xml");
        VariableContractImpl vc = super.getVariableContract();
        vc.setVariableFromProgram(GCMFunctionalTest.VC_HOSTCAPACITY, Integer.valueOf(4).toString(),
                VariableContractType.DescriptorDefaultVariable);
        vc.setVariableFromProgram(GCMFunctionalTest.VC_VMCAPACITY, Integer.valueOf(1).toString(),
                VariableContractType.DescriptorDefaultVariable);
        GCMApplication gcma = PAGCMDeployment.loadApplicationDescriptor(descriptorPath, vc);
        gcma.startDeployment();
        GCM.getMigrationController(client).migrateGCMComponentTo(
                gcma.getVirtualNodes().values().iterator().next().getANode());
        Runner runner = (Runner) client.getFcInterface("Runner");
        Assert.assertTrue("Failed to invoke web services with primitive component", runner.execute()
                .getBooleanValue());
    }

    @Test
    public void testCXFWebServiceBindingsWithWSCallerError() throws Exception {
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        try {
            GCM.getBindingController(client).bindFc(
                    Client.SERVICES_NAME,
                    url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME +
                        "(WSCallerError)");
            fail();
        } catch (IllegalBindingException ibe) {
            ibe.printStackTrace();
        }
    }

    @Test
    public void testCXFWebServiceBindingsWithURLError() throws Exception {
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        try {
            GCM.getBindingController(client).bindFc(Client.SERVICES_NAME, "ErrorURL");
            fail();
        } catch (IllegalBindingException ibe) {
            ibe.printStackTrace();
        }
    }

    @Test
    public void testCXFWebServiceBindingsWithMethodError() throws Exception {
        ComponentType cType = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("Runner", Runner.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE),
                tf.createFcItfType(Client.SERVICEERROR_NAME, ServiceError.class.getName(),
                        TypeFactory.CLIENT, TypeFactory.MANDATORY, TypeFactory.SINGLE) });
        Component client = gf.newFcInstance(cType, new ControllerDescription("Client", Constants.PRIMITIVE),
                new ContentDescription(Client.class.getName()));
        GCM.getBindingController(client).bindFc(Client.SERVICEERROR_NAME,
                url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME);
        GCM.getGCMLifeCycleController(client).startFc();
        Runner runner = (Runner) client.getFcInterface("Runner");
        Assert.assertFalse("Successful access to a non existing method", runner.execute().getBooleanValue());
    }
}
