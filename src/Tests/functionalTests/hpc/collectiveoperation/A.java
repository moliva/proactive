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
package functionalTests.hpc.collectiveoperation;

import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.api.PASPMD;
import org.objectweb.proactive.extra.hpc.spmd.CollectiveOperation;


public class A {
    private CollectiveOperation cop;
    private double max, min, sum;
    private double[] maxArray, minArray, sumArray;

    public A() {
    }

    public void init() {
        this.cop = new CollectiveOperation(PAGroup.getGroup(PASPMD.getSPMDGroup()));
    }

    public void max() {
        double myValue = PASPMD.getMyRank() + 1;
        this.max = cop.max(myValue);
    }

    public void min() {
        double myValue = PASPMD.getMyRank() + 1;
        this.min = cop.min(myValue);
    }

    public void sum() {
        double myValue = PASPMD.getMyRank() + 1;
        this.sum = cop.sum(myValue);
    }

    public void maxArray() {
        double[] myArray = new double[] { PASPMD.getMyRank(), 1.0 / (PASPMD.getMyRank() + 0.1),
                PASPMD.getMyRank() * 10.0 };
        cop.max(myArray);
        this.maxArray = myArray;
    }

    public void minArray() {
        double[] myArray = new double[] { PASPMD.getMyRank(), 1.0 / (PASPMD.getMyRank() + 0.1),
                PASPMD.getMyRank() * 10.0 };
        cop.min(myArray);
        this.minArray = myArray;
    }

    public void sumArray() {
        double[] myArray = new double[] { PASPMD.getMyRank(), 1.0 / (PASPMD.getMyRank() + 0.1),
                PASPMD.getMyRank() * 10.0 };
        cop.sum(myArray);
        this.sumArray = myArray;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getSum() {
        return sum;
    }

    public double[] getMaxArray() {
        return maxArray;
    }

    public double[] getMinArray() {
        return minArray;
    }

    public double[] getSumArray() {
        return sumArray;
    }
}
