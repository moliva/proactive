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
package functionalTests.component;

import java.util.HashMap;
import java.util.Map;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.type.Composite;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.node.Node;


/**
 * This class provides primitives for the creation of systems of components
 * @author The ProActive Team
 */
public class Setup {
    private static ComponentType d_type = null;
    private static ComponentType A_TYPE = null;
    private static ComponentType B_TYPE = null;
    private static PAGenericFactory gf = null;
    private static GCMTypeFactory tf = null;

    private static void createTypes() throws Exception {
        createTypeD();
        createTypeA();
        createTypeB();
    }

    private static void init() throws InstantiationException, NoSuchInterfaceException {
        if ((tf == null) || (gf == null)) {
            CentralPAPropertyRepository.GCM_PROVIDER
                    .setValue("org.objectweb.proactive.core.component.Fractive");
            Component boot = Utils.getBootstrapComponent();
            if (tf == null) {
                tf = GCM.getGCMTypeFactory(boot);
            }

            if (gf == null) {
                gf = Utils.getPAGenericFactory(boot);
            }
        }
    }

    private static void createTypeB() throws Exception {
        init();
        if (B_TYPE == null) {
            B_TYPE = tf.createFcType(new InterfaceType[] { tf.createFcItfType("i2", I2.class.getName(),
                    TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE) });
        }
    }

    private static void createTypeD() throws Exception {
        init();
        if (d_type == null) {
            d_type = tf.createFcType(new InterfaceType[] {
                    tf.createFcItfType("i1", I1Multicast.class.getName(), TypeFactory.SERVER,
                            TypeFactory.MANDATORY, TypeFactory.SINGLE),
                    tf.createGCMItfType("i2", I2Multicast.class.getName(), TypeFactory.CLIENT,
                            TypeFactory.MANDATORY, GCMTypeFactory.MULTICAST_CARDINALITY) });
        }
    }

    public static void createTypeA() throws Exception {
        init();
        if (A_TYPE == null) {
            A_TYPE = tf.createFcType(new InterfaceType[] {
                    tf.createFcItfType("i1", I1.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                            TypeFactory.SINGLE),
                    tf.createFcItfType("i2", I2.class.getName(), TypeFactory.CLIENT, TypeFactory.MANDATORY,
                            TypeFactory.SINGLE) });
        }
    }

    public static Map<String, Component> createPrimitiveComponents() throws Exception {
        createTypes();
        Map<String, Component> map = new HashMap<String, Component>();
        map.put("primitiveA", createPrimitiveA());
        map.put("primitiveB", createPrimitiveB1());
        map.put("primitiveB2", createPrimitiveB2());
        map.put("primitiveD", createPrimitiveD());
        map.put("primitiveDbis", createPrimitiveDbis());

        return map;
    }

    public static Component createPrimitiveDbis() throws Exception {
        createTypeD();
        return gf.newFcInstance(d_type, new ControllerDescription("primitiveDbis", Constants.PRIMITIVE),
                new ContentDescription(PrimitiveComponentDbis.class.getName(), new Object[] {}));
    }

    public static Component createPrimitiveD() throws Exception {
        createTypeD();
        return gf.newFcInstance(d_type, new ControllerDescription("primitiveD", Constants.PRIMITIVE),
                new ContentDescription(PrimitiveComponentD.class.getName(), new Object[] {}));
    }

    public static Component createPrimitiveB2() throws Exception {
        createTypeB();
        return gf.newFcInstance(B_TYPE, new ControllerDescription("primitiveB2", Constants.PRIMITIVE),
                new ContentDescription(PrimitiveComponentB.class.getName(), new Object[] {}));
    }

    public static Component createPrimitiveB1() throws Exception {
        createTypeB();
        return gf.newFcInstance(B_TYPE, new ControllerDescription("primitiveB1", Constants.PRIMITIVE),
                new ContentDescription(PrimitiveComponentB.class.getName(), new Object[] {}));
    }

    public static Component createRemotePrimitiveB1(Node node) throws Exception {
        createTypeB();
        return gf.newFcInstance(B_TYPE, new ControllerDescription("primitiveB1", Constants.PRIMITIVE),
                new ContentDescription(PrimitiveComponentB.class.getName(), new Object[] {}), node);
    }

    public static Component createRemoteSlowPrimitiveB(Node node) throws Exception {
        createTypeB();
        return gf.newFcInstance(B_TYPE, new ControllerDescription("slowPrimitiveB1", Constants.PRIMITIVE),
                new ContentDescription(SlowPrimitiveComponentB.class.getName(), new Object[] {}), node);
    }

    public static Component createPrimitiveA() throws Exception {
        createTypeA();
        return gf.newFcInstance(A_TYPE, new ControllerDescription("primitiveA", Constants.PRIMITIVE),
                new ContentDescription(PrimitiveComponentA.class.getName(), new Object[] {}));
    }

    public static Component createSlowPrimitiveA() throws Exception {
        createTypeA();
        return gf.newFcInstance(A_TYPE, new ControllerDescription("slowPrimitiveA", Constants.PRIMITIVE),
                new ContentDescription(SlowPrimitiveComponentA.class.getName(), new Object[] {}));
    }

    public static Map<String, Component> createCompositeComponents() throws Exception {
        Map<String, Component> map = Setup.createPrimitiveComponents();
        Component compositeA = createCompositeA();
        Component compositeB1 = createCompositeB1();
        map.put("compositeA", compositeA);
        map.put("compositeB1", compositeB1);

        return map;
    }

    public static Component createCompositeB1() throws Exception {
        return createCompositeOfTypeB("compositeB1");
    }

    public static Component createCompositeOfTypeA(String name) throws Exception {
        createTypeA();
        Component composite = gf.newFcInstance(A_TYPE, new ControllerDescription(name, Constants.COMPOSITE),
                new ContentDescription(Composite.class.getName(), new Object[] {}));
        return composite;
    }

    public static Component createCompositeOfTypeB(String name) throws Exception {
        createTypeB();
        Component composite = gf.newFcInstance(B_TYPE, new ControllerDescription(name, Constants.COMPOSITE),
                new ContentDescription(Composite.class.getName(), new Object[] {}));
        return composite;
    }

    public static Component createSynchronousCompositeOfTypeB(String name) throws Exception {
        createTypeB();
        Component composite = gf.newFcInstance(B_TYPE, new ControllerDescription(name, Constants.COMPOSITE,
            Constants.SYNCHRONOUS), new ContentDescription(Composite.class.getName(), new Object[] {}));
        return composite;
    }

    public static Component createSynchronousCompositeOfTypeA(String name) throws Exception {
        createTypeA();
        Component composite = gf.newFcInstance(A_TYPE, new ControllerDescription(name, Constants.COMPOSITE,
            Constants.SYNCHRONOUS), new ContentDescription(Composite.class.getName(), new Object[] {}));
        return composite;
    }

    public static Component createRemoteCompositeB1(Node node) throws Exception {
        createTypeB();
        Component compositeB1 = gf.newFcInstance(B_TYPE, new ControllerDescription("compositeB1",
            Constants.COMPOSITE), new ContentDescription(Composite.class.getName(), new Object[] {}), node);
        return compositeB1;
    }

    public static Component createCompositeA() throws Exception {
        createTypeA();
        Component compositeA = gf.newFcInstance(A_TYPE, new ControllerDescription("compositeA",
            Constants.COMPOSITE), new ContentDescription(Composite.class.getName(), new Object[] {}));
        return compositeA;
    }
}
