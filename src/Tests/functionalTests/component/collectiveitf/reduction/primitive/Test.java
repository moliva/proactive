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
package functionalTests.component.collectiveitf.reduction.primitive;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.component.exceptions.ReductionException;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;

import functionalTests.ComponentTest;


public class Test extends ComponentTest {
    public static final String MESSAGE = "-Main-";
    public static final int NB_CONNECTED_ITFS = 2;

    public Test() {
        super("Multicast reduction for primitive components", "Multicast reduction for primitive components");
    }

    /*
     * @see testsuite.test.FunctionalTest#action()
     */
    @org.junit.Test
    public void action() throws Exception {
        try {
            // test selection of unique value
            List<IntWrapper> l = new ArrayList<IntWrapper>();
            l.add(new IntWrapper(12));
            Object result = null;
            try {
                result = org.objectweb.proactive.core.component.type.annotations.multicast.ReduceMode.SELECT_UNIQUE_VALUE
                        .reduce(l);
            } catch (ReductionException e) {
                e.printStackTrace();
            }
            Assert.assertEquals(new IntWrapper(12), result);

            // test case: simple invocation on component with unicast - annotated interface
            Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
            Map<String, Object> context = new HashMap<String, Object>();
            Component root = (Component) f.newComponent(
                    "functionalTests.component.collectiveitf.reduction.primitive.adl.Testcase", context);
            GCM.getGCMLifeCycleController(root).startFc();
            boolean result2 = ((RunnerItf) root.getFcInterface("runTestItf")).runTest();
            Assert.assertTrue(result2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
