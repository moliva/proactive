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
//@snippet-start calcium_primes_2
package org.objectweb.proactive.extensions.calcium.examples.findprimes;

import java.io.Serializable;


class Interval implements Serializable {
    public int min;
    public int max;
    public int solvableSize;

    /**
     * Creates a new interval to search for primes.
     * @param min  Beginning of interval
     * @param max  End of interval
     * @param solvableSize Acceptable size of search interval
     */
    public Interval(int min, int max, int solvableSize) {
        this.min = min;
        this.max = max;
        this.solvableSize = solvableSize;
    }

    // @snippet-break calcium_primes_2

    @Override
    public String toString() {
        return "Params: " + min + "<?<" + max;
    }

    public int[] foo() {

        return new int[] { 1, 2 };
    }
    // @snippet-resume calcium_primes_2
}
//@snippet-end calcium_primes_2