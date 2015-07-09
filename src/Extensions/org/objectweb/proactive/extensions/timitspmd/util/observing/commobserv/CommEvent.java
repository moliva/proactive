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
package org.objectweb.proactive.extensions.timitspmd.util.observing.commobserv;

import org.objectweb.proactive.extensions.timitspmd.util.observing.Event;
import org.objectweb.proactive.extensions.timitspmd.util.observing.EventObserver;


/**
 * A communication event represented by the triplet (observer, destination rank,
 * value).<br>
 * Must be interpreted like the
 * <li>observer</li>
 * registers the communicated
 * <li>value</li>
 * to the
 * <li>destination rank</li>.
 *
 * @see org.objectweb.proactive.benchmarks.timit.util.observing.Event
 * @author The ProActive Team
 */
public class CommEvent extends Event {

    /** The destination rank */
    private int destRank;

    /**
     * Creates an instance of CommEvent.
     *
     * @param observer
     *            The observer that registers this event
     * @param destRank
     *            The destination rank
     * @param value
     *            The communicated value
     */
    public CommEvent(EventObserver observer, int destRank, double value) {
        super(observer, value);
        this.destRank = destRank;
    }

    /**
     * Returns the destination rank.
     *
     * @return The destination rank
     */
    public int getDestRank() {
        return this.destRank;
    }

    /**
     * Sets the destination rank.
     *
     * @param destRank
     *            The destination rank
     */
    public void setDestRank(int destRank) {
        this.destRank = destRank;
    }
}
