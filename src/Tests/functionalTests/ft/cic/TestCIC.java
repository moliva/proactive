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
package functionalTests.ft.cic;

import static junit.framework.Assert.assertTrue;

import org.junit.Before;

import functionalTests.TestDisabler;
import functionalTests.ft.AbstractFTTezt;


/**
 * AO fails during the computation, and is restarted.
 * Communications between passive object, non-ft active object and ft active object.
 */
public class TestCIC extends AbstractFTTezt {

    public TestCIC() {
        super(TestCIC.class.getResource("/functionalTests/ft/cic/testFT_CIC.xml"), 4, 1);
    }

    @Before
    public void before() {
        TestDisabler.waitingFeatureFix();
    }

    @org.junit.Test
    public void action() throws Exception {

        this.startFTServer("cic");
        int res = this.deployAndStartAgents();
        this.stopFTServer();
        assertTrue(res == AbstractFTTezt.AWAITED_RESULT);
    }
}
