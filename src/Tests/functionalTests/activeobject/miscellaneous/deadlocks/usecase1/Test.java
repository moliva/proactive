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
package functionalTests.activeobject.miscellaneous.deadlocks.usecase1;

import org.objectweb.proactive.api.PAActiveObject;

import functionalTests.FunctionalTest;


/**
 * 
 * Tests that the request queue is not locked while serving a method through a serveAll-like call.
 * See JIRA: PROACTIVE-
 *
 */
public class Test extends FunctionalTest {

    @org.junit.Test
    public void action() throws Exception {
        AODeadlock2 ao2 = PAActiveObject.newActive(AODeadlock2.class, new Object[0]);
        AODeadlock1 ao1 = PAActiveObject.newActive(AODeadlock1.class, new Object[] { ao2 });
        ao2.setAODeadlock1(ao1);

        int iw = ao1.foo();
        System.out.println(iw);
    }
}
