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

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extensions.calcium.muscle.Muscle;


/**
 * This class provides statistics on the computation of a task.
 *
 * The statistics presented are the aggregation of all sub-tasks spawned from this tasks.
 * That is to say, for example, that the computation time represents the time computed by
 * all subtasks and this task.
 *
 * @author The ProActive Team
 */
@PublicAPI
public interface Stats extends Serializable {

    /**
     * @return Returns the time elapsed since the creation of the task
     * until this task is finished.
     */
    public long getWallClockTime();

    /**
     * The computation time will ideally correspond to the CPU Time or,
     * if the JVM does not support it, the wallclock time.
     * In comparison with the processing time, this time does not
     * include the network and other overheads.
     *
     * Note that if the measured code executed a fork, the child's cpu time
     * will not be reflected.
     *
     * @return Returns the computation time spent by this task.
     */
    public long getComputationTime();

    /**
     * The processing time represents the time this task was asigned to some
     * resource for computation. This time includes the time the node took to
     * travel through the network.
     * @return Returns the time spent by this node in processing state.
     */
    public long getProcessingTime();

    /**
     * The waiting time represent the time this task spent waiting for other
     * related nodes to finish. In particular for sub-nodes spawned from this one.
     * @return Returns the time this node spent in waiting state.
     */
    public long getWaitingTime();

    /**
     * The ready time represents the time this task was ready for execution waiting
     * for an available resource.
     * @return Returns the time spent by this node in ready state.
     */
    public long getReadyTime();

    /**
     * The results time represents the time since the task is considered finished,
     * and the time the client actually asks (and gets) the result.
     * @return Returns the time spent by this tasks in results state.
     */
    public long getResultsTime();

    /**
     * The tree span is the average number of branches of internal tree nodes.
     * It is calculated as: (#Nodes - 1)/(# Inner Nodes).
     *
     * @return Returns the average number of branches of this tree.
     */
    public float getTreeSpan();

    /**
     * The average depth of the tree, calculated as logB(#Nodes -1),
     * where the base B is the average tree span.
     * @return The average depth of the tree.
     */
    public float getTreeDepth();

    /**
     * @return Returns the number of nodes in the tree.
     */
    public int getTreeSize();

    /**
     * @param muscle The muscle of interest
     * @return The exercise of the specified muscle, or null if no statistics are available for this muscle.
     */
    public Exercise getExcercise(Muscle muscle);

    /**
     * @return The theoretical optimistic maximum number of available resources during the execution.
     * of this task. This doesn't mean that the task was actually executed on as many resources.
     */
    public int getMaxAvailableResources();
}
