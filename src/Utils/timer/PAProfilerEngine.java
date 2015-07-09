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
package timer;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * The Engine used for profiling code
 * It creates profilers on request and keep them on a list
 * It also registers itself to run when the JVM shutdowns and dump the results of the profilers
 */
public class PAProfilerEngine implements Runnable {
    ArrayList<Timer> profilerList = new ArrayList<Timer>();
    private static PAProfilerEngine engine;

    static {
        engine = new PAProfilerEngine();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(engine));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a profiler of default type
     * @return an AverageTimeProfiler
     */
    public static Timer createTimer() {
        Timer tmp = new AverageMicroTimer();
        registerTimer(tmp);
        return tmp;
    }

    /**
     * Add profilers to be managed by this profiler engine
     * @param papr
     */
    public static void registerTimer(Timer papr) {
        synchronized (engine.profilerList) {
            engine.profilerList.add(papr);
        }
    }

    /**
     * Remove a profiler from this engine
     * It's dump() method will thus never be called
     * @param papr
     */
    public static boolean removeTimer(Timer papr) {
        synchronized (engine.profilerList) {
            return engine.profilerList.remove(papr);
        }
    }

    private PAProfilerEngine() {
    }

    /**
     * This method starts when a shutdown of the VM is initiated
     */
    public void run() {
        dump();
    }

    /**
     * Call dump on all profilers registered in this engine
     */
    public void dump() {
        Iterator<Timer> it = profilerList.iterator();
        while (it.hasNext()) {
            it.next().dump();
        }
    }

    public static void main(String[] args) {
        System.out.println("Creating a profiler and registering it");
        PAProfilerEngine.createTimer();
        System.out.println("Creating an AverageTimeProfiler and registering it");
        Timer avg = new AverageMicroTimer("Test ");
        PAProfilerEngine.registerTimer(avg);

        for (int i = 0; i < 10; i++) {
            avg.start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            avg.stop();
        }
        System.out.println("Now dying");
    }
}
