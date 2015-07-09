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
package org.objectweb.proactive.extensions.timitspmd.util;

/**
 * This class is a reimplementation of HierarchicalTimer which perform many
 * tests on timers counters start/stop.<br>
 * It could be very useful for debug purpose.<br>
 * This class is used when using 'activateDebug()' method
 *
 * @author The ProActive Team
 */
public class SecuredHierarchicalTimer extends HierarchicalTimer {

    /**
     *
     */
    private int headCounterID = -1;

    private void checkRange(int n) {
        if ((n < 0) || (n >= this.getNbCounter())) {
            throw new IllegalArgumentException("Incorrect counter id. Maybe it has be set by a wrong "
                + "Timed instance. If it is a stop, maybe you never start" + "this counter.");
        }
    }

    private boolean internalIsStarted(int n) {
        return (this.parent[0] == n) || (this.parent[1] == n) || (this.parent[2] == n);
    }

    private boolean isJustStarted(int n) {
        return (this.level >= 0) && (this.parent[this.level] == n);
    }

    private String getInfos(int n, int lev) {
        return "'" + this.getCounterName(n) + "' [id=" + n + ", level=" + lev + "]";
    }

    /**
     * Reset only one counter
     *
     * @param n
     *            the counter id
     */
    @Override
    public void resetCounter(int n) {
        this.checkRange(n);
        super.resetCounter(n);
    }

    /**
     * Starts a counter.
     *
     * @param n
     *            The integer that identify the timer to stop.
     */
    @Override
    public void start(int n) {
        this.checkRange(n);
        if (this.internalIsStarted(n)) {
            throw new IllegalStateException("TimerCounter " + this.getInfos(n, this.level + 1) +
                " already started !");
        }
        if (this.headCounterID == -1) {
            this.headCounterID = n;
        }
        if (((this.level + 1) == 0) && (n != this.headCounterID)) {
            throw new IllegalStateException("Bad counters placement: not hierarchical. TimerCounter " +
                this.getInfos(this.headCounterID, 0) + " is already the " + "counter's head and " +
                this.getInfos(n, 0) + " can't be set " + "as head also. Try to make a global TOTAL counter " +
                "which include the others.");
        }
        super.start(n);
    }

    /**
     * Stops the adequate counter.
     *
     * @param n
     *            The integer that idetifies the timer to stop.
     */
    @Override
    public void stop(int n) {
        this.checkRange(n);
        if (!this.isJustStarted(n)) {
            if (!this.internalIsStarted(n)) {
                throw new IllegalStateException("TimerCounter " + this.getInfos(n, this.level) +
                    " must be started before being stopped !");
            } else {
                throw new IllegalStateException("TimerCounter " + this.getInfos(n, this.level) +
                    " stop misplaced. The last started counter is " +
                    this.getCounterName(this.parent[this.level]) + " and you should " +
                    "stop this one. Maybe your start/stop are imbricated.");
            }
        } else {
            super.stop(n);
            this.parent[this.level + 1] = -1;
        }
    }

    /**
     * Returns the value
     */
    @Override
    public int readTimer(int i, int j, int k) {
        this.checkRange(i);
        this.checkRange(j);
        this.checkRange(k);
        return super.readTimer(i, j, k);
    }
}
