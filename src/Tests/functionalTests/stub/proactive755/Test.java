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
package functionalTests.stub.proactive755;

import org.objectweb.proactive.api.PAActiveObject;

import functionalTests.FunctionalTest;


/**
 * Regression test for PROACTIVE-755
 * 
 *  invalid return type for generated stub when the 
 *  return type in an class is a subclass of 
 *  the one defined in the interface implemented by the class
 *  
 *  Interface A
 *     A getA()
 *
 * class AImpl impl A
   AIMpl getA()
 * 
 * the generated stub for AA returns A -- while AImpl should be expected
 *  
 */
public class Test extends FunctionalTest {

    @org.junit.Test
    public void proactive755() throws Exception {

        A aa = PAActiveObject.newActive(AImpl.class, new Object[] {});

        AImpl a = (AImpl) aa.getA();

    }

}
