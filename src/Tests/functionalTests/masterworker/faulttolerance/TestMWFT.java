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
package functionalTests.masterworker.faulttolerance;

import static junit.framework.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extensions.masterworker.ProActiveMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.Master;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import functionalTests.FunctionalTest;
import functionalTests.masterworker.A;


/**
 * Test load balancing
 */
public class TestMWFT extends FunctionalTest {
    private URL descriptor = TestMWFT.class.getResource("MasterWorkerFT.xml");
    private URL descriptor2 = TestMWFT.class.getResource("MasterWorkerFT2.xml");
    private Master<A, Integer> master;
    private List<A> tasks;
    private GCMApplication pad;
    private GCMApplication pad2;
    private GCMVirtualNode vn1;
    private GCMVirtualNode vn2;
    public static final int NB_TASKS = 5;

    @org.junit.Test
    public void action() throws Exception {
        master.solve(tasks);
        System.out.println("Waiting for one result");
        List<Integer> ids = master.waitKResults(1);
        System.out.println("Killing all active objects in VN2");
        pad2.kill();
        System.out.println("Waiting for the remaining results");
        List<Integer> ids2 = master.waitAllResults();
        ids.addAll(ids2);
        assertTrue("Only one worker left", master.workerpoolSize() == 1);

        Iterator<Integer> it = ids.iterator();
        int last = it.next();
        while (it.hasNext()) {
            int next = it.next();
            assertTrue("Results recieved in submission order", last < next);
            last = next;
        }
    }

    @Before
    public void initTest() throws Exception {
        tasks = new ArrayList<A>();
        for (int i = 0; i < NB_TASKS; i++) {
            A t = new A(i, (NB_TASKS - i) * 2000, false);
            tasks.add(t);
        }

        this.pad = PAGCMDeployment.loadApplicationDescriptor(descriptor, (VariableContractImpl) super
                .getVariableContract().clone());
        this.pad.startDeployment();
        this.vn1 = this.pad.getVirtualNode("VN1");
        this.vn1.waitReady();
        System.out.println("VN1 is ready");
        this.pad2 = PAGCMDeployment.loadApplicationDescriptor(descriptor2, (VariableContractImpl) super
                .getVariableContract().clone());
        this.pad2.startDeployment();
        this.vn2 = this.pad2.getVirtualNode("VN2");
        this.vn2.waitReady();
        System.out.println("VN2 is ready");

        master = new ProActiveMaster<A, Integer>();
        master.addResources(vn1.getCurrentNodes());
        master.addResources(vn2.getCurrentNodes());
        master.setResultReceptionOrder(Master.SUBMISSION_ORDER);
        master.setInitialTaskFlooding(1);
        master.setPingPeriod(500);
    }

    @After
    public void endTest() throws Exception {
        master.terminate(false);
        pad.kill();
    }
}
