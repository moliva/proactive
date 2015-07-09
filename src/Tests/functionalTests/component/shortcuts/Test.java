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
package functionalTests.component.shortcuts;

import org.etsi.uri.gcm.util.GCM;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;

import functionalTests.ComponentTest;
import functionalTests.component.I1;
import functionalTests.component.Message;
import functionalTests.component.PrimitiveComponentA;
import functionalTests.component.PrimitiveComponentB;
import functionalTests.component.Setup;


/**
 * @author The ProActive Team
 */
public class Test extends ComponentTest {

    /**
     *
     */
    private static final int NB_WRAPPERS = 5;
    private Message result1;
    private Message result2;
    private Message result3;
    private Message result4;
    private final String expectedResult = "foo" + PrimitiveComponentA.MESSAGE + PrimitiveComponentB.MESSAGE +
        PrimitiveComponentA.MESSAGE;
    private Component systemWithWrappingWithShortcuts;
    private Component systemWithWrappingWithoutShortcuts;
    private Component systemWithoutWrapping;

    public Test() {
        super("Shortcut communications through composite components",
                "Shortcut communications through composite components");
    }

    @org.junit.Test
    public void action() throws Exception {
        initializeComponentSystems();

        //System.out.println("testing unwrapped system");
        GCM.getGCMLifeCycleController(systemWithoutWrapping).stopFc();
        GCM.getGCMLifeCycleController(systemWithoutWrapping).startFc();
        result1 = ((I1) systemWithoutWrapping.getFcInterface("i1")).processInputMessage(new Message("foo"));
        // waiting for the future is only for having an ordered logging output
        PAFuture.waitFor(result1);
        Thread.sleep(2000);

        //System.out.println("testing wrapped system without shortcuts");
        GCM.getGCMLifeCycleController(systemWithWrappingWithoutShortcuts).stopFc();
        GCM.getGCMLifeCycleController(systemWithWrappingWithoutShortcuts).startFc();

        result2 = ((I1) systemWithWrappingWithoutShortcuts.getFcInterface("i1"))
                .processInputMessage(new Message("foo"));
        PAFuture.waitFor(result2);
        Thread.sleep(2000);

        //System.out.println("testing wrapped system with shortcuts -- fist invocation");
        // first call, which performs tensioning
        result3 = ((I1) systemWithWrappingWithShortcuts.getFcInterface("i1"))
                .processInputMessage(new Message("foo"));
        PAFuture.waitFor(result3);
        Thread.sleep(2000);

        GCM.getGCMLifeCycleController(systemWithWrappingWithShortcuts).stopFc();
        GCM.getGCMLifeCycleController(systemWithWrappingWithShortcuts).startFc();

        // second call, which goes directly through the shortcut
        //System.out.println("testing wrapped system with shortcuts -- second invocation");
        result4 = ((I1) systemWithWrappingWithShortcuts.getFcInterface("i1"))
                .processInputMessage(new Message("foo"));
        PAFuture.waitFor(result4);
        Thread.sleep(2000);

        // TODO manage shortcuts with reconfigurations
        // reset while shortcut exists
        //        resetComponentSystem();
        //        initializeComponentSystems();
        //      first call, which performs tensioning
        //        result5 = ((I1) systemWithWrappingWithShortcuts.getFcInterface("i1")).processInputMessage(new Message("foo"));
        // a shortcut is now realized. Compare with previous result
        //        result6 = ((I1) systemWithWrappingWithShortcuts.getFcInterface("i1")).processInputMessage(new Message("foo"));
        Assert.assertEquals(expectedResult, (PAFuture.getFutureValue(result4)).getMessage());
        Assert.assertEquals(expectedResult, (PAFuture.getFutureValue(result3)).getMessage());
        Assert.assertEquals(expectedResult, (PAFuture.getFutureValue(result2)).getMessage());
        Assert.assertEquals(expectedResult, (PAFuture.getFutureValue(result1)).getMessage());
    }

    private void initializeComponentSystems() throws Exception {
        // system without wrapped components
        Component unwrappedA = Setup.createPrimitiveA();
        Component unwrappedB = Setup.createPrimitiveB1();
        GCM.getBindingController(unwrappedA).bindFc("i2", unwrappedB.getFcInterface("i2"));
        GCM.getGCMLifeCycleController(unwrappedA).startFc();
        GCM.getGCMLifeCycleController(unwrappedB).startFc();
        systemWithoutWrapping = unwrappedA;

        // system with wrapping but without shortcuts
        Component wrappedAWithoutShortcuts = Setup.createPrimitiveA();
        for (int i = 0; i < NB_WRAPPERS; i++) {
            wrappedAWithoutShortcuts = wrapWithCompositeOfTypeA(NB_WRAPPERS - i, wrappedAWithoutShortcuts);
        }

        Component wrappedBWithoutShortcuts = Setup.createPrimitiveB1();
        for (int i = 0; i < NB_WRAPPERS; i++) {
            wrappedBWithoutShortcuts = wrapWithCompositeOfTypeB(NB_WRAPPERS - i, wrappedBWithoutShortcuts);
        }

        GCM.getBindingController(wrappedAWithoutShortcuts).bindFc("i2",
                wrappedBWithoutShortcuts.getFcInterface("i2"));
        GCM.getGCMLifeCycleController(wrappedAWithoutShortcuts).startFc();
        GCM.getGCMLifeCycleController(wrappedBWithoutShortcuts).startFc();

        systemWithWrappingWithoutShortcuts = wrappedAWithoutShortcuts;

        // system with wrapping and with shortcuts
        Component wrappedAWithShortcuts = Setup.createPrimitiveA();
        for (int i = 0; i < NB_WRAPPERS; i++) {
            wrappedAWithShortcuts = wrapWithSynchronousCompositeOfTypeA(NB_WRAPPERS - i,
                    wrappedAWithShortcuts);
        }

        Component wrappedBWithShortcuts = Setup.createPrimitiveB1();
        for (int i = 0; i < NB_WRAPPERS; i++) {
            wrappedBWithShortcuts = wrapWithSynchronousCompositeOfTypeB(NB_WRAPPERS - i,
                    wrappedBWithShortcuts);
        }

        GCM.getBindingController(wrappedAWithShortcuts).bindFc("i2",
                wrappedBWithShortcuts.getFcInterface("i2"));
        GCM.getGCMLifeCycleController(wrappedAWithShortcuts).startFc();
        GCM.getGCMLifeCycleController(wrappedBWithShortcuts).startFc();

        systemWithWrappingWithShortcuts = wrappedAWithShortcuts;
    }

    private void resetComponentSystem() {
        // TODO change the inner wrapped components and check the shortcut is aware of the reconfiguration
    }

    private Component wrapWithSynchronousCompositeOfTypeB(int index, Component wrappee) throws Exception {
        Component wrapper = Setup.createSynchronousCompositeOfTypeB("sync_composite_b" + index);
        GCM.getContentController(wrapper).addFcSubComponent(wrappee);
        GCM.getBindingController(wrapper).bindFc("i2", wrappee.getFcInterface("i2"));
        return wrapper;
    }

    private Component wrapWithCompositeOfTypeB(int index, Component wrappee) throws Exception {
        Component wrapper = Setup.createCompositeOfTypeB("composite_b" + index);
        GCM.getContentController(wrapper).addFcSubComponent(wrappee);
        GCM.getBindingController(wrapper).bindFc("i2", wrappee.getFcInterface("i2"));
        return wrapper;
    }

    private Component wrapWithSynchronousCompositeOfTypeA(int index, Component wrappee) throws Exception {
        Component wrapper = Setup.createSynchronousCompositeOfTypeA("sync_composite_a" + index);
        GCM.getContentController(wrapper).addFcSubComponent(wrappee);
        GCM.getBindingController(wrapper).bindFc("i1", wrappee.getFcInterface("i1"));
        GCM.getBindingController(wrappee).bindFc("i2", wrapper.getFcInterface("i2"));
        return wrapper;
    }

    private Component wrapWithCompositeOfTypeA(int index, Component wrappee) throws Exception {
        Component wrapper = Setup.createCompositeOfTypeA("composite_a" + index);
        GCM.getContentController(wrapper).addFcSubComponent(wrappee);
        GCM.getBindingController(wrapper).bindFc("i1", wrappee.getFcInterface("i1"));
        GCM.getBindingController(wrappee).bindFc("i2", wrapper.getFcInterface("i2"));
        return wrapper;
    }

    @Before
    public void initTest() throws Exception {
        CentralPAPropertyRepository.PA_COMPONENT_USE_SHORTCUTS.setValue(true);
    }

    @After
    public void endTest() throws Exception {
        CentralPAPropertyRepository.PA_COMPONENT_USE_SHORTCUTS.setValue(false);
    }
}
