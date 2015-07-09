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
package org.objectweb.proactive.extensions.masterworker.core;

import java.io.Serializable;

import org.objectweb.proactive.extensions.masterworker.interfaces.DivisibleTask;
import org.objectweb.proactive.extensions.masterworker.interfaces.SubMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.Task;
import org.objectweb.proactive.extensions.masterworker.interfaces.WorkerMemory;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.Identifiable;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.TaskIntern;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * The internal version of a task, contains an internal ID and the task itself
 * @author The ProActive Team
 *
 */
public class TaskWrapperImpl implements TaskIntern<Serializable> {

    /**
     *
     */

    /**
     * The id of the task
     */
    private long id = NULL_TASK_ID;

    /**
     * The actual task object
     */
    private Task<Serializable> realTask = null;

    /**
     *
     */
    public TaskWrapperImpl() { // null task
    }

    /**
     * Creates a wrapper with the given task and id
     * @param id id of the task
     * @param realTask the user task
     */
    public TaskWrapperImpl(final long id, final Task<Serializable> realTask) {
        this.id = id;
        this.realTask = realTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Identifiable) {
            return id == ((Identifiable) obj).getId();
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public Task<Serializable> getTask() {
        return realTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (int) id;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNull() {
        return realTask == null;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable run(final WorkerMemory memory) throws Exception {
        return this.realTask.run(memory);
    }

    /**
    * {@inheritDoc}
    */
    public Serializable run(final WorkerMemory memory,
            final SubMaster<Task<Serializable>, Serializable> master) throws Exception {
        return ((DivisibleTask<Serializable>) this.realTask).run(memory, master);
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(final Identifiable o) {
        if (o == null) {
            throw new NullPointerException();
        }

        return (int) (id - (o).getId());
    }
}
