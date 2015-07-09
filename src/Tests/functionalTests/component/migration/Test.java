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
package functionalTests.component.migration;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.objectweb.proactive.api.PAActiveObject;

import functionalTests.ComponentTest;


/**
 * This test deploys a distributed component system and makes sure migration is effective by
 * invoking methods on migrated components (through singleton, collection, gathercast and multicast interfaces)
 *
 * @author The ProActive Team
 */
public class Test extends ComponentTest {

    /**
     *
     */
    public static String MESSAGE = "-->m";

    //ComponentsCache componentsCache;
    public Test() {
        super("migration of components", "migration of components");
    }

    /*
     * (non-Javadoc)
     * 
     * @see testsuite.test.FunctionalTest#action()
     */
    @org.junit.Test
    public void GCMDeployment() throws Exception {
        DummyAO testAO = PAActiveObject
                .newActive(DummyAO.class, new Object[] { super.getVariableContract() });
        assertEquals(true, testAO.goGCMDeployment());
    }

    @Ignore
    // Fails on debian
    @org.junit.Test
    public void OldDeployment() throws Exception {
        DummyAO testAO = PAActiveObject
                .newActive(DummyAO.class, new Object[] { super.getVariableContract() });
        assertEquals(true, testAO.goOldDeployment());
    }
}
