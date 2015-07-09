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

import org.objectweb.proactive.annotation.PublicAPI;


/**
 * This method provides global statistics snapshot of the framework.
 *
 * @author The ProActive Team
 */
@PublicAPI
public interface StatsGlobal {

    /**
     * @return The current length of the ready queue.
     */
    public int getReadyQueueLength();

    /**
     * @return The current length of the processing queue.
     */
    public int getProccessingQueueLength();

    /**
     * @return The current length of the waiting queue.
     */
    public int getWaitingQueueLength();

    /**
     * @return The current length of the waiting queue.
     */
    public int getResultsQueueLength();

    /**
     * @return The current number of solved tasks since the boot of the framework.
     */
    public int getSolvedNumberOfTasks();

    /**
     * @return The current number of solved root-tasks since the boot of the framework.
     */
    public int getSolvedNumberOfRootTasks();

    /**
     * @return The average time spent by tasks on the processing state.
     */
    public long getAverageProcessingTime();

    /**
     * @return The average time spent by tasks on the waiting state.
     */
    public long getAverageWaitingTime();

    /**
     * @return The average time spent by tasks on the results state.
     */
    public long getAverageResultsTime();

    /**
     * @return The average time spent by tasks on the ready state.
     */
    public long getAverageReadyTime();

    /**
     * @return The average wallclock time of finished tasks
     */
    public long getAverageWallClockTime();

    /**
     * @return The average computation time of all tasks
     */
    public long getAverageComputationTime();
}
