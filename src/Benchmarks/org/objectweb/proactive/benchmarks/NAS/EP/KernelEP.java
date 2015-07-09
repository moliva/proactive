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
package org.objectweb.proactive.benchmarks.NAS.EP;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PASPMD;
import org.objectweb.proactive.benchmarks.NAS.Kernel;
import org.objectweb.proactive.benchmarks.NAS.NASProblemClass;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.timitspmd.util.BenchmarkStatistics;
import org.objectweb.proactive.extensions.timitspmd.util.EventStatistics;
import org.objectweb.proactive.extensions.timitspmd.util.HierarchicalTimerStatistics;
import org.objectweb.proactive.extensions.timitspmd.util.TimItManager;
import org.objectweb.proactive.gcmdeployment.GCMApplication;


/**
 * Kernel EP
 *
 * An "Embarrassingly Parallel" kernel. It provides an estimate of the
 * upper achievable limits for floating point performance, i.e., the
 * performance without significant interprocessor communication.
 */
public class KernelEP extends Kernel {

    private EPProblemClass problemClass;
    private Node[] nodes;

    /* The reference for the typed group of Workers */
    private WorkerEP workers;

    public KernelEP() {
    }

    public KernelEP(NASProblemClass pclass, GCMApplication gcma) {
        this.problemClass = (EPProblemClass) pclass;
        this.gcma = gcma;
    }

    public void runKernel() throws ProActiveException {
        Object[] param = new Object[] { problemClass };

        Object[][] params = new Object[problemClass.NUM_PROCS][];
        for (int i = 0; i < problemClass.NUM_PROCS; i++) {
            params[i] = param;
        }

        try {
            nodes = super.getNodes(this.problemClass.NUM_PROCS).toArray(new Node[] {});

            if (nodes.length < this.problemClass.NUM_PROCS) {
                System.err.println("Not enough nodes: get " + nodes.length + ", need " +
                    this.problemClass.NUM_PROCS);
            }

            workers = (WorkerEP) PASPMD.newSPMDGroup(WorkerEP.class.getName(), params, nodes);

            TimItManager tManager = TimItManager.getInstance();
            tManager.setTimedObjects(workers);

            workers.start();

            BenchmarkStatistics bstats = tManager.getBenchmarkStatistics();
            HierarchicalTimerStatistics tstats = bstats.getTimerStatistics();
            EventStatistics estats = bstats.getEventsStatistics();

            KernelEP.printEnd(this.problemClass, tstats.getMax(0, 0, 0), Double.valueOf(estats.getEventValue(
                    "mflops").toString()), (bstats.getInformation().indexOf("UNSUCCESSFUL") == -1));

        } catch (ClassNotReifiableException e) {
            e.printStackTrace();
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will be called by the kill method of the Benchmark class
     * to terminate all workers.
     */
    public void killKernel() {
        workers.terminate();
    }

    public static void printStarted(String kernel, char className, long[] size, int nbIteration, int nbProcess) {
        System.out.print("\n\n NAS Parallel Benchmarks ProActive -- " + kernel + " Benchmark\n\n");
        System.out.println(" Class: " + className);
        System.out.println(" Number of random numbers generated: " + Math.pow(2., (size[0] + 1)));
        System.out.println(" Number of active processes: " + nbProcess);
    }
}
