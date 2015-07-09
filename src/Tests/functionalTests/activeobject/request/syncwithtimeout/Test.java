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
package functionalTests.activeobject.request.syncwithtimeout;

import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.ProActiveTimeoutException;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;

import functionalTests.FunctionalTest;


/**
 * Test blocking request, and calling void, int returned type and object
 * returned type method
 */

public class Test extends FunctionalTest {
    SynchrounousMethodCallWithTimeout activeA;
    boolean exceptionCaught = false;

    @Before
    public void action() throws Exception {
        try {
            CentralPAPropertyRepository.PA_FUTURE_SYNCHREQUEST_TIMEOUT.setValue(2);
            activeA = PAActiveObject.newActive(SynchrounousMethodCallWithTimeout.class, new Object[0]);

            System.out
                    .println("calling a synchronous method that waits for 10 sec while timeout is set to 2 sec");
            activeA.sleepFor10Sec();
        } catch (ProActiveTimeoutException e) {
            System.out.println("got the exception " + e.getMessage() + ", this is good");
            exceptionCaught = true;
            return;
        }
        System.out.println("missed the exception, this is *no* good");
    }

    @org.junit.Test
    public void postConditions() {
        assertTrue(exceptionCaught);
    }
}
