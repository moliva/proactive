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
package functionalTests.component.conform;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.Utils;

import functionalTests.component.conform.components.C;
import functionalTests.component.conform.components.CAttributes;
import functionalTests.component.conform.components.I;
import functionalTests.component.conform.components.W;
import functionalTests.component.conform.components.X;
import functionalTests.component.conform.components.Y;
import functionalTests.component.conform.components.Z;


public class TestGenericFactory extends Conformtest {
    protected Component boot;
    protected GCMTypeFactory tf;
    protected GenericFactory gf;
    protected ComponentType t;
    protected ComponentType u;
    protected final static String AC = "attribute-controller/" + PKG + ".CAttributes/false,false,false";
    protected final static String sI = "server/" + PKG + ".I/false,false,false";
    protected final static String cI = "client/" + PKG + ".I/true,false,false";

    @Before
    public void setUp() throws Exception {
        boot = Utils.getBootstrapComponent();
        tf = GCM.getGCMTypeFactory(boot);
        gf = GCM.getGenericFactory(boot);
        t = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("server", I.class.getName(), false, false, false),
                tf.createFcItfType("client", I.class.getName(), true, false, false) });
        u = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType(Constants.ATTRIBUTE_CONTROLLER, CAttributes.class.getName(), false, false,
                        false), tf.createFcItfType("server", I.class.getName(), false, false, false),
                tf.createFcItfType("client", I.class.getName(), true, false, false) });
    }

    // -------------------------------------------------------------------------
    // Test direct component creation
    // -------------------------------------------------------------------------
    @Test
    public void testFPrimitive() throws Exception {
        Component c = gf.newFcInstance(t, flatPrimitive, C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, NC, MCC, GC, MC, sI,
                cI })));
    }

    @Test
    public void testFParametricPrimitive() throws Exception {
        Component c = gf.newFcInstance(u, flatParametricPrimitive, C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, AC, NC, MCC, GC, MC,
                sI, cI })));
    }

    @Test
    public void testPrimitive() throws Exception {
        Component c = gf.newFcInstance(t, "primitive", C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, SC, NC, MCC, GC, MC,
                MoC, PC, sI, cI })));
    }

    @Test
    public void testParametricPrimitive() throws Exception {
        Component c = gf.newFcInstance(u, parametricPrimitive, C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, MC, LC, MCC, GC, SC, NC,
                AC, sI, cI })));
    }

    @Test
    public void testComposite() throws Exception {
        Component c = gf.newFcInstance(t, "composite", null);
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, CC, LC, SC, NC, MCC, GC,
                MC, MoC, PC, sI, cI })));
    }

    @Test
    public void testParametricComposite() throws Exception {
        Component c = gf.newFcInstance(u, parametricComposite, C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, CC, LC, SC, AC, NC, MCC,
                GC, MC, sI, cI })));
    }

    // -------------------------------------------------------------------------
    // Test component creation via templates
    // -------------------------------------------------------------------------
    @Test
    @Ignore
    public void testFPrimitiveTemplate() throws Exception {
        Component c = gf.newFcInstance(t, flatPrimitiveTemplate, C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, NC, MCC, GC, MC, F, sI,
                cI })));
        c = GCM.getFactory(c).newFcInstance();
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, NC, MCC, GC, MC, sI,
                cI })));
    }

    @Test
    @Ignore
    public void testFParametricPrimitiveTemplate() throws Exception {
        Component c = gf.newFcInstance(u, flatParametricPrimitiveTemplate, C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, AC, NC, MCC, GC, MC, F,
                sI, cI })));
        c = GCM.getFactory(c).newFcInstance();
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, AC, NC, MCC, GC, MC,
                sI, cI })));
    }

    @Test
    @Ignore
    public void testPrimitiveTemplate() throws Exception {
        Component c = gf.newFcInstance(t, primitiveTemplate, C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, SC, NC, MCC, GC, MC, F,
                sI, cI })));
        c = GCM.getFactory(c).newFcInstance();
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, SC, NC, MCC, GC, MC,
                sI, cI })));
    }

    @Test
    @Ignore
    public void testParametricPrimitiveTemplate() throws Exception {
        Component c = gf.newFcInstance(u, parametricPrimitiveTemplate, C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, MC, MCC, GC, SC, NC, AC,
                F, sI, cI })));
        c = GCM.getFactory(c).newFcInstance();
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, MC, MCC, GC, SC, NC,
                AC, sI, cI })));
    }

    @Test
    @Ignore
    public void testCompositeTemplate() throws Exception {
        Component c = gf.newFcInstance(t, compositeTemplate, null);
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, CC, SC, NC, MCC, GC, MC,
                F, sI, cI })));
        c = GCM.getFactory(c).newFcInstance();
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, CC, SC, NC, MCC, GC,
                MC, sI, cI })));
    }

    @Test
    @Ignore
    public void testParametricCompositeTemplate() throws Exception {
        Component c = gf.newFcInstance(u, parametricCompositeTemplate, C.class.getName());

        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, CC, SC, AC, NC, MCC, GC,
                MC, F, sI, cI })));
        c = GCM.getFactory(c).newFcInstance();
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, CC, LC, SC, AC, NC, MCC,
                GC, MC, sI, cI })));
    }

    // -------------------------------------------------------------------------
    // Test component creation errors
    // -------------------------------------------------------------------------
    @Test
    public void testUnknownControllerDescriptor() throws Exception {
        try {
            // no such controller descriptor
            gf.newFcInstance(t, "unknownDescriptor", C.class.getName());
            fail();
        } catch (InstantiationException e) {
        }
    }

    @Test
    public void testBadControllerDescriptor1() throws Exception {
        try {
            // error in controller descriptor
            gf.newFcInstance(t, badPrimitive, C.class.getName());
            fail();
        } catch (InstantiationException e) {
        }
    }

    @Test
    public void testBadControllerDescriptor2() throws Exception {
        try {
            // error in controller descriptor
            gf.newFcInstance(u, badParametricPrimitive, C.class.getName());
            fail();
        } catch (InstantiationException e) {
        }
    }

    @Test
    public void testContentClassNotFound() throws Exception {
        try {
            // no such class
            gf.newFcInstance(t, "primitive", "UnknownClass");
            fail();
        } catch (InstantiationException e) {
        }
    }

    @Test
    public void testContentClassAbstract() throws Exception {
        try {
            // X is an abstract class
            gf.newFcInstance(t, "primitive", W.class.getName());
            fail();
        } catch (InstantiationException e) {
        }
    }

    @Test
    public void testContentClassNoDefaultConstructor() throws Exception {
        try {
            // X has no public constructor
            gf.newFcInstance(t, "primitive", X.class.getName());
            fail();
        } catch (InstantiationException e) {
        }
    }

    @Test(expected = InstantiationException.class)
    @Ignore
    public void testContentClassControlInterfaceMissing() throws Exception {
        // Y does not implement BindingController
        gf.newFcInstance(t, "primitive", Y.class.getName());
    }

    @Test(expected = InstantiationException.class)
    public void testContentClassInterfaceMissing() throws Exception {
        // Z does not implement I
        gf.newFcInstance(t, "primitive", Z.class.getName());
    }

    @Test
    public void testTemplateContentClassNotFound() throws Exception {
        try {
            // no such class
            gf.newFcInstance(t, primitiveTemplate, "UnknownClass");
            fail();
        } catch (InstantiationException e) {
        }
    }

    @Test
    public void testTemplateContentClassAbstract() throws Exception {
        try {
            // X is an abstract class
            gf.newFcInstance(t, primitiveTemplate, W.class.getName());
            fail();
        } catch (InstantiationException e) {
        }
    }

    @Test
    public void testTemplateContentClassNoDefaultConstructor() throws Exception {
        try {
            // X has no public constructor
            gf.newFcInstance(t, primitiveTemplate, X.class.getName());
            fail();
        } catch (InstantiationException e) {
        }
    }

    @Test
    @Ignore
    public void testTemplateContentClassControlInterfaceMissing() throws Exception {
        try {
            // Y does not implement BindingController
            gf.newFcInstance(t, primitiveTemplate, Y.class.getName());
            fail();
        } catch (InstantiationException e) {
        }
    }

    @Test
    @Ignore
    public void testTemplateContentClassInterfaceMissing() throws Exception {
        try {
            // Z does not implement I
            gf.newFcInstance(t, primitiveTemplate, Z.class.getName());
            fail();
        } catch (InstantiationException e) {
        }
    }
}
