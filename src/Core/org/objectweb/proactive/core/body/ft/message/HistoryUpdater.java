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
package org.objectweb.proactive.core.body.ft.message;

import java.io.Serializable;
import java.util.List;

import org.objectweb.proactive.core.UniqueID;


/**
 * This structure defines a part of a reception history. It is used to
 * update a ReceptionHistory object.
 * @author The ProActive Team
 * @since 3.0
 */
public class HistoryUpdater implements Serializable {

    /**
     *
     */

    /** Reception events, i.e. id of the senders */
    public List<UniqueID> elements;

    /** Reception index of the first element */
    public long base;

    /** Reception index of the last element */
    public long last;

    /** ID of the owner */
    public UniqueID owner;

    /** Index of the associated checkpoint */
    public int checkpointIndex;

    /** Incarnation number of the sender */
    public int incarnation;

    /**
     * Create an history updater.
     * @param elements Reception events, i.e. id of the senders
     * @param base Reception index of the first element
     * @param last Reception index of the last element
     * @param owner ID of the owner
     * @param checkpointIndex Index of the associated checkpoint
     * @param incarnation Incarnation number of the sender
     */
    public HistoryUpdater(List<UniqueID> elements, long base, long last, UniqueID owner, int checkpointIndex,
            int incarnation) {
        this.elements = elements;
        this.base = base;
        this.last = last;
        this.owner = owner;
        this.checkpointIndex = checkpointIndex;
        this.incarnation = incarnation;
    }
}
