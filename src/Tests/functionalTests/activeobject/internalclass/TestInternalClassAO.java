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
package functionalTests.activeobject.internalclass;

import java.io.Serializable;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;

import functionalTests.FunctionalTest;


/*
 * See PROACTIVE-277
 * Notes:
 * 
 * Member or nested top-level classes must be public to be activated.
 * As local classes (i.e. member classes defined in a code block) cannot be public, they cannot be activated.
 * 
 */
public class TestInternalClassAO extends FunctionalTest {

    private int enclosingPrivate = 0;

    private static final int AWAITED_VALUE = 3;

    @Test
    public void test() throws NodeException, ActiveObjectCreationException {

        ////// new active //////
        boolean newActiveException = false;
        MemberClass ao = null;
        try {
            ao = PAActiveObject.newActive(MemberClass.class, new Object[] {});
        } catch (ActiveObjectCreationException e) {
            newActiveException = true;
        }
        // cannot create an active object from a member class
        Assert.assertTrue(newActiveException);

        // ok
        NestedTopLevelClass sao = PAActiveObject.newActive(NestedTopLevelClass.class, new Object[] {});

        /////// turn active ///////
        MemberClass ao2 = new MemberClass();
        // access to the enclosing instance 
        ao2.incrementEnclosingPrivateValue();
        ao2 = PAActiveObject.turnActive(ao2);
        // access to the enclosing instance through activated object
        ao2.incrementEnclosingPrivateValue();
        // access to the enclosing instance through activated object with an intermediate AO
        RemoteAgent ra = PAActiveObject.newActive(RemoteAgent.class, new Object[] {});
        ra.doCallOnMemberClassInstance(ao2);
        Assert.assertEquals(AWAITED_VALUE, this.enclosingPrivate);

        // ok
        NestedTopLevelClass sao2 = new NestedTopLevelClass();
        sao2 = (NestedTopLevelClass) PAActiveObject.turnActive(sao2);

    }

    // Requirement for non static member class : MUST BE SERIALIZABLE
    // Note that empty constructor is not mandatory
    public class MemberClass implements Serializable {
        public int incrementEnclosingPrivateValue() {
            return TestInternalClassAO.this.enclosingPrivate++;
        }
    }

    // No requirement on Nested Top Level class.
    public static class NestedTopLevelClass {
    }

}
