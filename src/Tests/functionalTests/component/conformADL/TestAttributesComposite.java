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
package functionalTests.component.conformADL;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.proactive.core.component.adl.FactoryFactory;

import functionalTests.ComponentTest;
import functionalTests.component.conformADL.components.CAttributes;


public class TestAttributesComposite extends ComponentTest {
    protected Factory factory;

    @Before
    public void setUp() throws Exception {
        factory = FactoryFactory.getFactory();
    }

    // -----------------------------------------------------------------------------------
    // Full test
    // -----------------------------------------------------------------------------------
    @Test
    public void testCompositeWithAttributeController() throws Exception {

        // ----------------------------------------------------------        
        // Load the ADL definition
        // ----------------------------------------------------------        
        Component root = (Component) factory.newComponent(
                "functionalTests.component.conformADL.components.CAttributesComposite",
                new HashMap<Object, Object>());

        // ----------------------------------------------------------
        // Start the Root component
        // ----------------------------------------------------------
        GCM.getGCMLifeCycleController(root).startFc();

        // ----------------------------------------------------------
        // Call the attributes methods
        // ----------------------------------------------------------
        CAttributes ca = (CAttributes) GCM.getAttributeController(root);
        ca.setX1(true);
        assertEquals(true, ca.getX1());
        ca.setX2((byte) 1);
        assertEquals((byte) 1, ca.getX2());
        ca.setX3((char) 1);
        assertEquals((char) 1, ca.getX3());
        ca.setX4((short) 1);
        assertEquals((short) 1, ca.getX4());
        ca.setX5(1);
        assertEquals(1, ca.getX5());
        ca.setX6(1);
        assertEquals((long) 1, ca.getX6());
        ca.setX7(1);
        assertEquals(1, ca.getX7(), 0);
        ca.setX8(1);
        assertEquals(1, ca.getX8(), 0);
        ca.setX9("1");
        assertEquals("1", ca.getX9());
    }

    // -----------------------------------------------------------------------------------
    // Test composite with content do not extends AttributeController
    // -----------------------------------------------------------------------------------
    @Test(expected = InstantiationException.class)
    @Ignore
    public void testCompositeWithContentError() throws Exception {
        // ----------------------------------------------------------        
        // Load the ADL definition
        // ----------------------------------------------------------        
        factory.newComponent("functionalTests.component.conformADL.components.CAttributesCompositeError",
                new HashMap<Object, Object>());

    }
}
