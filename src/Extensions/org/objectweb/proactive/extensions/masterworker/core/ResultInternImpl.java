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

import org.objectweb.proactive.extensions.masterworker.interfaces.internal.Identifiable;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.ResultIntern;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * A result of a task, contains the result itself, or an exception if one has been thrown
 * @author The ProActive Team
 *
 */
public class ResultInternImpl implements ResultIntern<Serializable> {

    /**
     *
     */

    /**
     * The id of the task
     */
    private long id = -1;

    /**
     * the result
     */
    private Serializable result = null;

    /**
     * when this task has thrown an exception
     */
    private boolean isException = false;

    /**
     *  the exception thrown
     */
    private Throwable exception = null;

    /**
     * Creates an empty result object for the given task
     * @param taskId id of the task associated with the result
     */
    public ResultInternImpl(long taskId) {
        this.id = taskId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Identifiable) && (id == ((Identifiable) obj).getId());

    }

    /**
     * {@inheritDoc}
     */
    public Throwable getException() {
        return exception;
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
    public Serializable getResult() {
        return result;
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
    public void setException(final Throwable e) {
        if (e == null) {
            throw new IllegalArgumentException("Exception can't be null");
        }

        this.exception = e;
        this.isException = true;
    }

    /**
     * {@inheritDoc}
     */
    public void setResult(final Serializable res) {
        this.result = res;
    }

    /**
     * {@inheritDoc}
     */
    public boolean threwException() {
        return isException;
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

    @Override
    public String toString() {
        return "ID: " + id + " Result: " + result + " Exception: " + exception;
    }
}
