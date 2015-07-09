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
package functionalTests.activeobject.future;

import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.ProActiveTimeoutException;

import functionalTests.FunctionalTest;


public class TestPAFuture extends FunctionalTest {

    @Test
    public void isAwaitedNoFuture() {
        Object o = new Object();
        boolean resp = PAFuture.isAwaited(o);
        Assert.assertFalse("O is not a future, should not been awaited", resp);
    }

    @Test(timeout = 500)
    public void waitForNoFuture() {
        Object o = new Object();
        PAFuture.waitFor(o);
    }

    @Test(timeout = 500)
    public void waitForWithTimeoutNoFuture() throws ProActiveTimeoutException {
        Object o = new Object();
        PAFuture.waitFor(o, 1000);
    }

    @Test
    public void waitForAny() {

    }

    @Test
    public void waitForAnyNoFture() {
        Vector<Object> v = new Vector<Object>();
        v.add(new Object());
        v.add(new Object());

        int index;
        index = PAFuture.waitForAny(v);
        v.remove(index);
        index = PAFuture.waitForAny(v);
        v.remove(index);
        Assert.assertTrue(v.isEmpty());
    }

    @Test(timeout = 500)
    public void waitForAllNoFture() {
        Vector<Object> v = new Vector<Object>();
        v.add(new Object());
        v.add(new Object());

        PAFuture.waitForAll(v);
    }

    @Test(timeout = 500)
    public void waitForAllWithTimeoutNoFture() throws ProActiveTimeoutException {
        Vector<Object> v = new Vector<Object>();
        v.add(new Object());
        v.add(new Object());

        PAFuture.waitForAll(v, 1000);
    }
}
