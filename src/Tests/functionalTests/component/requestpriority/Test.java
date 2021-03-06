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
package functionalTests.component.requestpriority;

import static org.junit.Assert.assertEquals;

import org.etsi.uri.gcm.api.control.PriorityController;
import org.etsi.uri.gcm.api.control.PriorityController.RequestPriority;
import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;

import functionalTests.ComponentTest;


/**
 * Test the priority controller.
 *
 * @author The ProActive Team
 */
public class Test extends ComponentTest {
    private static String P1_NAME = "Primitive_One";
    private Component p1 = null;
    private FItf functionnal_Itf = null;
    private NF1Itf nonFunctionnal1_Itf = null;
    private NF2Itf nonFunctionnal2_Itf = null;
    private NF3Itf nonFunctionnal3_Itf = null;

    /**
     *
     */
    @Before
    public void createComponent() throws Exception {
        Component boot = Utils.getBootstrapComponent();
        GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);
        GenericFactory cf = GCM.getGenericFactory(boot);

        ControllerDescription myController = new ControllerDescription(P1_NAME, Constants.PRIMITIVE,
            "/functionalTests/component/requestpriority/my-component-config.xml", false);
        ComponentType pc_type = type_factory.createFcType(new InterfaceType[] { type_factory.createFcItfType(
                FItf.ITF_NAME, FItf.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                TypeFactory.SINGLE), });

        p1 = newFcInstance(cf, pc_type, myController);

        assertEquals(GCM.getNameController(p1).getFcName(), P1_NAME);

        // start component
        GCM.getGCMLifeCycleController(p1).startFc();

        // get interfaces
        functionnal_Itf = ((FItf) p1.getFcInterface(FItf.ITF_NAME));
        nonFunctionnal1_Itf = ((NF1Itf) p1.getFcInterface(NF1Itf.CONTROLLER_NAME));
        nonFunctionnal2_Itf = ((NF2Itf) p1.getFcInterface(NF2Itf.CONTROLLER_NAME));
        nonFunctionnal3_Itf = ((NF3Itf) p1.getFcInterface(NF3Itf.CONTROLLER_NAME));

        // set priorities functional 
        PriorityController pc = GCM.getPriorityController(p1);
        assertEquals(RequestPriority.NF1, pc.getGCMPriority(Constants.PRIORITY_CONTROLLER, "setPriority",
                null));
        assertEquals(RequestPriority.NF2, pc.getGCMPriority(Constants.PRIORITY_CONTROLLER, "setPriorityNF2",
                null));
        assertEquals(RequestPriority.NF3, pc.getGCMPriority(Constants.PRIORITY_CONTROLLER, "setPriorityNF3",
                null));
        assertEquals(RequestPriority.NF1, pc.getGCMPriority(Constants.PRIORITY_CONTROLLER, "getPriority",
                null));

        assertEquals(RequestPriority.F, pc.getGCMPriority(FItf.ITF_NAME, "addCall", null));
        assertEquals(RequestPriority.F, pc.getGCMPriority(FItf.ITF_NAME, "getCallOrder", null));
        assertEquals(RequestPriority.F, pc.getGCMPriority(FItf.ITF_NAME, "longFunctionalCall", null));
        assertEquals(RequestPriority.F, pc.getGCMPriority(FItf.ITF_NAME, "functionalCall", null));

        assertEquals(RequestPriority.NF1, pc.getGCMPriority(NF1Itf.CONTROLLER_NAME, "NF1Call", null));
        assertEquals(RequestPriority.NF1, pc.getGCMPriority(NF2Itf.CONTROLLER_NAME, "NF2Call", null));
        assertEquals(RequestPriority.NF1, pc.getGCMPriority(NF3Itf.CONTROLLER_NAME, "NF3Call", null));

        pc.setGCMPriority(NF2Itf.CONTROLLER_NAME, "NF2Call", new Class<?>[] {}, RequestPriority.NF2);
        assertEquals(RequestPriority.NF2, pc.getGCMPriority(NF2Itf.CONTROLLER_NAME, "NF2Call", null));

        pc.setGCMPriority(NF3Itf.CONTROLLER_NAME, "NF3Call", new Class<?>[] {}, RequestPriority.NF3);
        assertEquals(RequestPriority.NF3, pc.getGCMPriority(NF3Itf.CONTROLLER_NAME, "NF3Call", null));
    }

    protected Component newFcInstance(GenericFactory gf, ComponentType type,
            ControllerDescription controllerDescription) throws Exception {
        return gf.newFcInstance(type, controllerDescription, new ContentDescription(
            PrioritizedComponent.class.getName(), new Object[] {}));
    }

    /**
     *
     */
    @org.junit.Test
    public void test_F_F_NF3() throws Exception {
        String expectedOrder = FItf.F_STR_CALL + NF3Itf.NF3_STR_CALL + FItf.F_STR_CALL;
        functionnal_Itf.longFunctionalCall();
        functionnal_Itf.functionalCall();
        Thread.sleep(500); //wait to be sure the first functionnalCall are processed
        nonFunctionnal3_Itf.NF3Call();
        assertEquals(expectedOrder, functionnal_Itf.getCallOrder());
    }

    /**
     *
     */
    @org.junit.Test
    public void test_stopped_NF1_F_NF3() throws Exception {
        String expectedOrder = NF1Itf.NF1_STR_CALL + NF3Itf.NF3_STR_CALL + FItf.F_STR_CALL;
        GCM.getGCMLifeCycleController(p1).stopFc();
        nonFunctionnal1_Itf.longNF1Call();
        Thread.sleep(500); //wait to be sure the first functionnalCall are processing, allowing us to enqueue request in the order we want

        functionnal_Itf.functionalCall();

        nonFunctionnal3_Itf.NF3Call();

        GCM.getGCMLifeCycleController(p1).startFc();
        assertEquals(expectedOrder, functionnal_Itf.getCallOrder());
    }

    /**
     *
     */
    @org.junit.Test
    public void test_F_F_NF1_NF2() throws Exception {
        String expectedOrder = FItf.F_STR_CALL + FItf.F_STR_CALL + NF1Itf.NF1_STR_CALL + NF2Itf.NF2_STR_CALL;
        functionnal_Itf.longFunctionalCall();
        Thread.sleep(500);
        functionnal_Itf.functionalCall();
        nonFunctionnal1_Itf.NF1Call();
        nonFunctionnal2_Itf.NF2Call();
        assertEquals(expectedOrder, functionnal_Itf.getCallOrder());
    }

    /**
     *
     */
    @org.junit.Test
    public void test_F_F_NF1_F_NF2() throws Exception {
        String expectedOrder = FItf.F_STR_CALL + FItf.F_STR_CALL + NF1Itf.NF1_STR_CALL + NF2Itf.NF2_STR_CALL +
            FItf.F_STR_CALL;
        functionnal_Itf.longFunctionalCall();
        Thread.sleep(500);
        functionnal_Itf.functionalCall();
        nonFunctionnal1_Itf.NF1Call();
        functionnal_Itf.functionalCall();
        nonFunctionnal2_Itf.NF2Call();
        assertEquals(expectedOrder, functionnal_Itf.getCallOrder());
    }

    /**
     *
     */
    @org.junit.Test
    public void test_F_F_NF1_F_NF2_NF3() throws Exception {
        String expectedOrder = FItf.F_STR_CALL + NF3Itf.NF3_STR_CALL + FItf.F_STR_CALL + NF1Itf.NF1_STR_CALL +
            NF2Itf.NF2_STR_CALL + FItf.F_STR_CALL;
        functionnal_Itf.longFunctionalCall();
        Thread.sleep(500);
        functionnal_Itf.functionalCall();
        nonFunctionnal1_Itf.NF1Call();
        functionnal_Itf.functionalCall();
        nonFunctionnal2_Itf.NF2Call();
        nonFunctionnal3_Itf.NF3Call();
        assertEquals(expectedOrder, functionnal_Itf.getCallOrder());
    }

    /**
     *
     */
    @org.junit.Test
    public void test_stopped_NF1_F_NF1_F_NF2_NF3() throws Exception {
        String expectedOrder = NF1Itf.NF1_STR_CALL + NF3Itf.NF3_STR_CALL + NF1Itf.NF1_STR_CALL +
            NF2Itf.NF2_STR_CALL + FItf.F_STR_CALL + FItf.F_STR_CALL;

        GCM.getGCMLifeCycleController(p1).stopFc();
        nonFunctionnal1_Itf.longNF1Call();
        Thread.sleep(500);
        functionnal_Itf.functionalCall();
        nonFunctionnal1_Itf.NF1Call();
        functionnal_Itf.functionalCall();
        nonFunctionnal2_Itf.NF2Call();
        nonFunctionnal3_Itf.NF3Call();

        GCM.getGCMLifeCycleController(p1).startFc();
        assertEquals(expectedOrder, functionnal_Itf.getCallOrder());
    }

    /**
     *
     */
    @org.junit.Test
    public void test_F_F_NF2_F_NF2_NF1() throws Exception {
        String expectedOrder = FItf.F_STR_CALL + NF2Itf.NF2_STR_CALL + NF2Itf.NF2_STR_CALL + FItf.F_STR_CALL +
            FItf.F_STR_CALL + NF1Itf.NF1_STR_CALL;
        functionnal_Itf.longFunctionalCall();
        Thread.sleep(500);
        functionnal_Itf.functionalCall();
        nonFunctionnal2_Itf.NF2Call();
        functionnal_Itf.functionalCall();
        nonFunctionnal2_Itf.NF2Call();
        nonFunctionnal1_Itf.NF1Call();
        assertEquals(expectedOrder, functionnal_Itf.getCallOrder());
    }

    /**
     *
     */
    @org.junit.Test
    public void test_stopped_NF1_F_NF2_F_NF2_NF1() throws Exception {
        String expectedOrder = NF1Itf.NF1_STR_CALL + NF2Itf.NF2_STR_CALL + NF2Itf.NF2_STR_CALL +
            NF1Itf.NF1_STR_CALL + FItf.F_STR_CALL + FItf.F_STR_CALL;
        nonFunctionnal1_Itf.longNF1Call();
        GCM.getGCMLifeCycleController(p1).stopFc();
        Thread.sleep(500);
        functionnal_Itf.functionalCall();
        nonFunctionnal2_Itf.NF2Call();
        functionnal_Itf.functionalCall();
        nonFunctionnal2_Itf.NF2Call();
        nonFunctionnal1_Itf.NF1Call();
        GCM.getGCMLifeCycleController(p1).startFc();
        assertEquals(expectedOrder, functionnal_Itf.getCallOrder());
    }
}
