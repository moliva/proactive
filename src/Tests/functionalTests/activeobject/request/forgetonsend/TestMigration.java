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
package functionalTests.activeobject.request.forgetonsend;

import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;

import functionalTests.GCMFunctionalTest;


/**
 * When migrating an Active Object, the migration should wait until the SendingQueue is empty to
 * avoid multiple and un-useful serializations. 
 */

public class TestMigration extends GCMFunctionalTest {

    private C c1, c2, c3, c4;

    public TestMigration() throws ProActiveException {
        super(4, 1);
        super.startDeployment();
    }

    @Test
    public void migration() throws Exception {

        Node node1 = super.getANode();
        Node node2 = super.getANode();
        Node node3 = super.getANode();
        Node node4 = super.getANode();

        c1 = PAActiveObject.newActive(C.class, new Object[] { "C1" }, node1);
        c2 = PAActiveObject.newActive(C.class, new Object[] { "C2" }, node2);
        c3 = PAActiveObject.newActive(C.class, new Object[] { "C3" }, node3);
        c4 = PAActiveObject.newActive(C.class, new Object[] { "C4" }, node4);

        int r1 = c1.getRuntimeHashCode();
        int r2 = c2.getRuntimeHashCode();
        int r3 = c3.getRuntimeHashCode();
        int r4 = c4.getRuntimeHashCode();

        c1.sendTwoFos(c2);
        c1.moveTo(node3);

        Thread.sleep(10000);

        // Check Migration
        assertTrue(c2.getFooASerializer() == r1); // fooA should be sent from node1
        assertTrue(c2.getFooBSerializer() == r1); // fooB should be sent from node1 too
        assertTrue(c2.getServices().equals("ab")); // Check FIFO
    }
}