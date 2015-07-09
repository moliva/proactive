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
package org.objectweb.proactive.examples.pi;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * This class represents the result of a computation(performed on an interval or).
 * @author The ProActive Team
 *
 */
public class Result implements Serializable {
    private BigDecimal bd;
    private long computedTime;

    /**
     * Empty constructor
     */
    public Result() {
    }

    /**
     * @param bd the initial BigDecimal value
     * @param computedTime time the computation took
     */
    public Result(BigDecimal bd, long computedTime) {
        this.bd = bd;
        this.computedTime = computedTime;
    }

    /**
     * @return the value of the result, as a BigDecimal
     */
    public BigDecimal getNumericalResult() {
        return bd;
    }

    /**
     * @param increment the big decimal to add
     */
    public void addNumericalResult(BigDecimal increment) {
        bd = bd.add(increment);
    }

    /**
     * @return Returns the computedTime.
     */
    public long getComputationTime() {
        return computedTime;
    }

    /**
     * @param computedTime The computedTime to add.
     */
    public void addComputationTime(long computedTime) {
        this.computedTime += computedTime;
    }

    @Override
    public String toString() {
        return ((bd != null) ? (bd.toString() + "") : null);
    }
}
