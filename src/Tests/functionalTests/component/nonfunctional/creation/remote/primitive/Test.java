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
package functionalTests.component.nonfunctional.creation.remote.primitive;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.After;
import org.junit.Assert;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.representative.PANFComponentRepresentative;

import functionalTests.ComponentTestDefaultNodes;
import functionalTests.component.nonfunctional.creation.DummyControllerComponentImpl;
import functionalTests.component.nonfunctional.creation.DummyControllerItf;


/**
 * @author The ProActive Team
 *
 * creates a new non-functional component, marked as non-functional
 */
public class Test extends ComponentTestDefaultNodes {
    Component dummyNFComponent;
    String name;
    String nodeUrl;

    public Test() throws ProActiveException {
        super(1, 1);
    }

    @org.junit.Test
    public void action() throws Exception {
        Thread.sleep(2000);
        /*
         * Getting the Fractal-Proactive bootstrap component
         */
        Component boot = Utils.getBootstrapComponent();

        /*
         * Getting the GCM-ProActive type factory
         */
        GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);

        /*
         * Getting the
         * GCM-ProActive generic
         * factory
         */
        PAGenericFactory cf = Utils.getPAGenericFactory(boot);

        ComponentType fcType = type_factory.createFcType(new InterfaceType[] { type_factory.createFcItfType(
                "fitness-controller-membrane", DummyControllerItf.class.getName(), TypeFactory.SERVER,
                TypeFactory.MANDATORY, TypeFactory.SINGLE), });

        ControllerDescription controllerDescription = new ControllerDescription("fitnessController",
            Constants.PRIMITIVE);

        ContentDescription contentDescription = new ContentDescription(DummyControllerComponentImpl.class
                .getName());

        dummyNFComponent = cf.newNfFcInstance(fcType, controllerDescription, contentDescription, super
                .getANode());
        GCM.getGCMLifeCycleController(dummyNFComponent).startFc();
        DummyControllerItf ref = (DummyControllerItf) dummyNFComponent
                .getFcInterface("fitness-controller-membrane");
        name = ref.dummyMethodWithResult();
        System.out.println("Received result is : " + name);
        ref.dummyVoidMethod("Message");
    }

    @After
    public void postConditions() throws Exception {
        Assert.assertTrue(dummyNFComponent instanceof PANFComponentRepresentative);
    }

}
