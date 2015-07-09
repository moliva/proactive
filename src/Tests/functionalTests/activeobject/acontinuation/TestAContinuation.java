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
package functionalTests.activeobject.acontinuation;

import static junit.framework.Assert.assertTrue;

import java.util.Vector;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import functionalTests.FunctionalTest;


/**
 * Test automatic continuations by results and parameters
 */

public class TestAContinuation extends FunctionalTest {
    AOAContinuation a;
    AOAContinuation b;
    AOAContinuation t1;
    AOAContinuation t2;
    AOAContinuation lastA;
    Id idPrincipal;
    Id idDeleguate;
    boolean futureByResult;

    @org.junit.Test
    public void action() throws Exception {
        boolean initial_ca_setting = CentralPAPropertyRepository.PA_FUTURE_AC.getValue();
        if (!CentralPAPropertyRepository.PA_FUTURE_AC.isTrue()) {
            CentralPAPropertyRepository.PA_FUTURE_AC.setValue(true);
        }
        ACThread acthread = new ACThread();
        acthread.start();
        acthread.join();
        CentralPAPropertyRepository.PA_FUTURE_AC.setValue(initial_ca_setting);

        assertTrue(futureByResult && a.isSuccessful());
        assertTrue(a.getFinalResult().equals("dummy"));
        assertTrue(lastA.getIdName().equals("e"));
        assertTrue(t1.getIdName().equals("d"));
        assertTrue(t2.getIdName().equals("d"));
    }

    private class ACThread extends Thread {
        @Override
        public void run() {
            try {
                a = PAActiveObject.newActive(AOAContinuation.class, new Object[] { "principal" });
                //test future by result
                a.initFirstDeleguate();
                idDeleguate = a.getId("deleguate2");
                idPrincipal = a.getId("principal");
                Vector<Id> v = new Vector<Id>(2);
                v.add(idDeleguate);
                v.add(idPrincipal);
                if (PAFuture.waitForAny(v) == 0) {
                    futureByResult = false;
                } else {
                    futureByResult = true;
                }

                //test future passed as parameter
                b = PAActiveObject.newActive(AOAContinuation.class, new Object[] { "dummy" });
                idPrincipal = b.getIdforFuture();
                a.forwardID(idPrincipal);
                //Test non-blocking when future passed as parameter
                AOAContinuation c = PAActiveObject.newActive(AOAContinuation.class, new Object[] { "c" });
                AOAContinuation d = PAActiveObject.newActive(AOAContinuation.class, new Object[] { "d" });
                AOAContinuation e = PAActiveObject.newActive(AOAContinuation.class, new Object[] { "e" });

                AOAContinuation de = d.getA(e);
                AOAContinuation cde = c.getA(de);
                lastA = e.getA(cde);

                //test multiple wrapped futures with multiples AC destinations
                AOAContinuation f = PAActiveObject.newActive(AOAContinuation.class, new Object[] { "f" });
                c.initSecondDeleguate();
                AOAContinuation t = c.delegatedGetA(d);
                t1 = e.getA(t);
                t2 = f.getA(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
