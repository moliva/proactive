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
package org.objectweb.proactive.extensions.masterworker.util;

/**
 * Object containing a task id and the worker name which has submitted the task
 * (null if it's the main client)
 *
 * @author The ProActive Team
 */
public class TaskID {
    private final String originator;
    private final Long id;
    private final boolean isDivisible;

    public TaskID(String originator, long id, boolean isDivisible) {
        this.originator = originator;
        this.id = id;
        this.isDivisible = isDivisible;
    }

    public String getOriginator() {
        return originator;
    }

    public Long getID() {
        return id;
    }

    public boolean isDivisible() {
        return isDivisible;
    }

    public boolean equals(Object obj) {

        if (obj instanceof TaskID) {
            return id.equals(((TaskID) obj).getID());
        } else if (obj instanceof Long) {
            return id.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return id.hashCode();
    }
}
