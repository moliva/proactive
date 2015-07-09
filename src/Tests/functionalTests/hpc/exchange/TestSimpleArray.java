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
package functionalTests.hpc.exchange;

import static junit.framework.Assert.assertTrue;

import java.util.Arrays;

import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.api.PASPMD;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;

import functionalTests.GCMFunctionalTest;


public class TestSimpleArray extends GCMFunctionalTest {
    private A spmdgroup;

    public TestSimpleArray() throws ProActiveException {
        super(2, 2);
        super.startDeployment();
    }

    @org.junit.Test
    public void action() throws Exception {
        Object[][] params = { {}, {}, {}, {} };
        Node[] nodes = new Node[] { super.getANode(), super.getANode(), super.getANode(), super.getANode() };

        // Let's create a four nodes SPMD group
        spmdgroup = (A) PASPMD.newSPMDGroup(A.class.getName(), params, nodes);

        // Performing two kind of exchanges through a total exchange:
        //
        // +-----+ +-----+
        // | a00 | | a10 |
        // +-----+ +-----+
        // | a01 | | a11 |
        // +-----+ +-----+
        // JVM #0 . JVM#1
        //
        // (a00 <=> a01) and (a10 <=> a11) : Local exchanges
        // (a00 <=> a11) and (a01 <=> a10) : Distant exchanges
        // 
        // Procedure:
        // =========
        // Each AO has a quarter of a big array.
        //
        // Step 1 :
        // AOs on the same JVM are exchanging their quarter, then all have half of the global array
        //
        // Step 2 :
        // Each AO exchange its half with an AO in the other JVM, then all have the full array
        //
        for (int i = 0; i < 50; i++) {
            spmdgroup.doubleExchange();

            double[] a0 = ((A) PAGroup.get(spmdgroup, 0)).getArray();
            double[] a1 = ((A) PAGroup.get(spmdgroup, 1)).getArray();
            double[] a2 = ((A) PAGroup.get(spmdgroup, 2)).getArray();
            double[] a3 = ((A) PAGroup.get(spmdgroup, 3)).getArray();

            assertTrue(Arrays.equals(a0, a1));
            assertTrue(Arrays.equals(a1, a2));
            assertTrue(Arrays.equals(a2, a3));
        }
    }
}
