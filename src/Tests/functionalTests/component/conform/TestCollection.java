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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Utils;

import functionalTests.component.conform.components.C;
import functionalTests.component.conform.components.I;


public class TestCollection extends Conformtest {
    protected Component boot;
    protected GCMTypeFactory tf;
    protected GenericFactory gf;
    protected ComponentType t;
    protected final static String serverI = "server/" + PKG + ".I/false,false,false";
    protected final static String servers0I = "servers0/" + PKG + ".I/false,false,true";
    protected final static String servers1I = "servers1/" + PKG + ".I/false,false,true";
    protected final static String servers2I = "servers2/" + PKG + ".I/true,false,true";
    protected final static String servers3I = "servers3/" + PKG + ".I/true,false,true";
    protected final static String clientI = "client/" + PKG + ".I/true,true,false";
    protected final static String clients0I = "clients0/" + PKG + ".I/true,true,true";
    protected final static String clients1I = "clients1/" + PKG + ".I/true,true,true";
    protected final static String clients2I = "clients2/" + PKG + ".I/false,true,true";
    protected final static String clients3I = "clients3/" + PKG + ".I/false,true,true";

    // -------------------------------------------------------------------------
    // Constructor ans setup
    // -------------------------------------------------------------------------
    @Before
    public void setUp() throws Exception {
        boot = Utils.getBootstrapComponent();
        tf = GCM.getGCMTypeFactory(boot);
        gf = GCM.getGenericFactory(boot);
        t = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("server", I.class.getName(), false, false, false),
                tf.createFcItfType("servers", I.class.getName(), false, false, true),
                tf.createFcItfType("client", I.class.getName(), true, true, false),
                tf.createFcItfType("clients", I.class.getName(), true, true, true) });
    }

    // -------------------------------------------------------------------------
    // Test component instantiation
    // -------------------------------------------------------------------------
    @Test
    public void testPrimitiveWithCollection() throws Exception {
        Component c = gf.newFcInstance(t, "primitive", C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, SC, NC, MCC, GC, MC,
                MoC, PC, serverI, clientI })));
        //       new Object[] { COMP, BC, LC, SC, NC, serverI, clientI })));
    }

    @Test
    public void testCompositeWithCollection() throws Exception {
        Component c = gf.newFcInstance(t, "composite", null);
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, CC, LC, SC, NC, MCC, GC,
                MC, MoC, PC, serverI, clientI })));
    }

    @Test
    @Ignore
    // missing factory/org.objectweb.proactive.core.component.Fractive/false,false,false,
    public void testPrimitiveTemplateWithCollection() throws Exception {
        Component c = gf.newFcInstance(t, primitiveTemplate, C.class.getName());
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, F, SC, NC, MC, MCC, GC,
                serverI, clientI })));
        c = GCM.getFactory(c).newFcInstance();
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, SC, NC, MC, MCC, GC,
                serverI, clientI })));
    }

    @Test
    @Ignore
    public void testCompositeTemplateWithCollection() throws Exception {
        Component c = gf.newFcInstance(t, compositeTemplate, "composite");
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, CC, F, SC, NC, serverI,
                clientI })));
        c = GCM.getFactory(c).newFcInstance();
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, CC, LC, SC, NC, serverI,
                clientI })));
    }

    // -------------------------------------------------------------------------
    // Test lazy interface creation through getFc(Internal)Interface
    // -------------------------------------------------------------------------
    @Test
    public void testPrimitiveGetFcInterface() throws Exception {
        Component c = gf.newFcInstance(t, "primitive", C.class.getName());
        GCM.getGCMLifeCycleController(c).startFc();
        Interface i;
        i = (Interface) c.getFcInterface("servers0");
        assertEquals("Bad interface", servers0I, getItf(i, false));
        checkInterface((I) i);
        i = (Interface) c.getFcInterface("servers1");
        assertEquals("Bad interface", servers1I, getItf(i, false));
        checkInterface((I) i);
        i = (Interface) c.getFcInterface("clients0");
        assertEquals("Bad interface", clients0I, getItf(i, false));
        i = (Interface) c.getFcInterface("clients1");
        assertEquals("Bad interface", clients1I, getItf(i, false));
    }

    @Test
    @Ignore
    public void testCompositeGetFcInterface() throws Exception {
        Component c = gf.newFcInstance(t, "composite", null);
        Interface i;
        i = (Interface) c.getFcInterface("servers0");
        assertEquals("Bad interface", servers0I, getItf(i, false));
        i = (Interface) c.getFcInterface("servers1");
        assertEquals("Bad interface", servers1I, getItf(i, false));
        i = (Interface) c.getFcInterface("clients0");
        assertEquals("Bad interface", clients0I, getItf(i, false));
        i = (Interface) c.getFcInterface("clients1");
        assertEquals("Bad interface", clients1I, getItf(i, false));

        ContentController cc = GCM.getContentController(c);
        i = (Interface) cc.getFcInternalInterface("servers2");
        // ((InterfaceType) i.getFcItfType()).isFcClientItf()) should return false since it's an internal client, therefore a server!
        // TODO check this behavior with Julia or AOKell

        assertEquals("Bad interface", servers2I, getItf(i, false)); // second parameter should be true
        i = (Interface) cc.getFcInternalInterface("servers3");
        assertEquals("Bad interface", servers3I, getItf(i, false));
        i = (Interface) cc.getFcInternalInterface("clients2");
        assertEquals("Bad interface", clients2I, getItf(i, false));
        i = (Interface) cc.getFcInternalInterface("clients3");
        assertEquals("Bad interface", clients3I, getItf(i, false));
    }

    @Test
    @Ignore
    public void testPrimitiveTemplateGetFcInterface() throws Exception {
        Component c = gf.newFcInstance(t, primitiveTemplate, C.class.getName());
        Interface i;
        i = (Interface) c.getFcInterface("servers0");
        assertEquals("Bad interface", servers0I, getItf(i, false));
        i = (Interface) c.getFcInterface("servers1");
        assertEquals("Bad interface", servers1I, getItf(i, false));
        i = (Interface) c.getFcInterface("clients0");
        assertEquals("Bad interface", clients0I, getItf(i, false));
        i = (Interface) c.getFcInterface("clients1");
        assertEquals("Bad interface", clients1I, getItf(i, false));

        c = GCM.getFactory(c).newFcInstance();
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, LC, SC, NC, MC, MCC, GC,
                serverI, clientI })));
    }

    @Test
    @Ignore
    public void testCompositeTemplateGetFcInterface() throws Exception {
        Component c = gf.newFcInstance(t, compositeTemplate, null);
        Interface i;
        i = (Interface) c.getFcInterface("servers0");
        assertEquals("Bad interface", servers0I, getItf(i, false));
        i = (Interface) c.getFcInterface("servers1");
        assertEquals("Bad interface", servers1I, getItf(i, false));
        i = (Interface) c.getFcInterface("clients0");
        assertEquals("Bad interface", clients0I, getItf(i, false));
        i = (Interface) c.getFcInterface("clients1");
        assertEquals("Bad interface", clients1I, getItf(i, false));

        ContentController cc = GCM.getContentController(c);
        i = (Interface) cc.getFcInternalInterface("servers2");
        assertEquals("Bad interface", servers2I, getItf(i, false));
        i = (Interface) cc.getFcInternalInterface("servers3");
        assertEquals("Bad interface", servers3I, getItf(i, false));
        i = (Interface) cc.getFcInternalInterface("clients2");
        assertEquals("Bad interface", clients2I, getItf(i, false));
        i = (Interface) cc.getFcInternalInterface("clients3");
        assertEquals("Bad interface", clients3I, getItf(i, false));

        c = GCM.getFactory(c).newFcInstance();
        checkComponent(c, new HashSet<Object>(Arrays.asList(new Object[] { COMP, BC, CC, LC, SC, NC, serverI,
                clientI })));
    }

    // -------------------------------------------------------------------------
    // Test errors of lazy interface creation through getFc(Internal)Interface
    // -------------------------------------------------------------------------
    @Test
    public void testPrimitiveNoSuchCollectionItf() throws Exception {
        Component c = gf.newFcInstance(t, "primitive", C.class.getName());
        try {
            c.getFcInterface("server0");
            fail();
        } catch (NoSuchInterfaceException e) {
        }
        try {
            c.getFcInterface("client0");
            fail();
        } catch (NoSuchInterfaceException e) {
        }
    }

    @Test
    public void testCompositeNoSuchCollectionItf() throws Exception {
        Component c = gf.newFcInstance(t, "composite", null);
        try {
            c.getFcInterface("server0");
            fail();
        } catch (NoSuchInterfaceException e) {
        }
        try {
            c.getFcInterface("client0");
            fail();
        } catch (NoSuchInterfaceException e) {
        }
    }

    @Test
    public void testPrimitiveTemplateNoSuchCollectionItf() throws Exception {
        Component c = gf.newFcInstance(t, primitiveTemplate, C.class.getName());
        try {
            c.getFcInterface("server0");
            fail();
        } catch (NoSuchInterfaceException e) {
        }
        try {
            c.getFcInterface("client0");
            fail();
        } catch (NoSuchInterfaceException e) {
        }
    }

    @Test
    public void testCompositeTemplateNoSuchCollectionItf() throws Exception {
        Component c = gf.newFcInstance(t, compositeTemplate, null);
        try {
            c.getFcInterface("server0");
            fail();
        } catch (NoSuchInterfaceException e) {
        }
        try {
            c.getFcInterface("client0");
            fail();
        } catch (NoSuchInterfaceException e) {
        }
    }
}
