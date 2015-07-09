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
package org.objectweb.proactive.extensions.calcium.statistics;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;


public class Timer implements Serializable {
    long t;
    long accumulated;
    int numberActivatedTimes;
    boolean cpuTime;

    public Timer(boolean useCPUTime) {
        cpuTime = useCPUTime;
        reset();
    }

    public Timer() {
        this(false);
    }

    /**
     * Starts the current timer. This method
     * resets the timer state.
     */
    public void start() {
        reset();
    }

    /**
     * Stops the current timer.
     * @return Accumulated elapsed time.
     */
    public long stop() {
        if (t > 0) {
            accumulated += (getCurrentTime() - t);
        }
        t = -1;
        return accumulated;
    }

    /**
     * After the timer has been stoped, this method can
     * resume the counter. The new time will be agregated to
     * the previous time.
     */
    public void resume() {
        t = getCurrentTime();
        numberActivatedTimes++;
    }

    /**
     * Resets the timer state.
     */
    private void reset() {
        t = getCurrentTime();
        accumulated = 0;
        numberActivatedTimes = 1;
    }

    /**
     * @return The currently accumulated time of this timer.
     */
    public long getTime() {
        if (t > 0) {
            return (accumulated + getCurrentTime()) - t;
        }

        return accumulated;
    }

    /**
     * @return Number of times this timer wast activated: the number
     * of times resume was called plus 1 (the inital start).
     */
    public int getNumberOfActivatedTimes() {
        return numberActivatedTimes;
    }

    private long getCurrentTime() {
        if (!cpuTime) {
            return System.currentTimeMillis();
        }

        ThreadMXBean tmb = ManagementFactory.getThreadMXBean();
        if (tmb.isThreadCpuTimeSupported()) {
            if (!tmb.isThreadCpuTimeEnabled()) {
                tmb.setThreadCpuTimeEnabled(true);
            }
            return tmb.getCurrentThreadCpuTime() / 1000000;
        }

        return System.currentTimeMillis();
    }
}
