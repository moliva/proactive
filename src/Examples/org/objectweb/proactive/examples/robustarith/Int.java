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
package org.objectweb.proactive.examples.robustarith;

import java.math.BigInteger;


/**
 * @author The ProActive Team
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Int {
    public static final BigInteger MINUS_ONE = BigInteger.ONE.negate();
    private static final int MAGNITUDE = 6553600;
    private static final BigInteger MAX = BigInteger.ONE.shiftLeft(MAGNITUDE);
    private static final BigInteger MIN = MAX.negate();

    public static BigInteger pow2(int e) throws OverflowException {
        BigInteger val = BigInteger.ONE.shiftLeft(e);
        if (val.compareTo(MAX) > 0) {
            throw new OverflowException("pow", new BigInteger("2"), new BigInteger("" + e));
        }

        return val;
    }

    public static BigInteger add(BigInteger a, BigInteger b) throws OverflowException {
        if ((a.signum() > 0) && (b.signum() > 0)) {
            if (MAX.subtract(a).compareTo(b) < 0) {
                throw new OverflowException("add", a, b);
            }
        } else if ((a.signum() < 0) && (b.signum() < 0)) {
            if (a.subtract(MIN).compareTo(b.negate()) < 0) {
                throw new OverflowException("add", a, b);
            }
        }

        return a.add(b);
    }

    public static BigInteger sub(BigInteger a, BigInteger b) throws OverflowException {
        try {
            return add(a, b.negate());
        } catch (OverflowException oe) {
            throw new OverflowException("sub", a, b);
        }
    }

    public static BigInteger mul(BigInteger a, BigInteger b) throws OverflowException {
        BigInteger m = a.multiply(b);
        if (m.compareTo(MAX) > 0) {
            throw new OverflowException("mul", a, b);
        }

        return m;
    }

    public static BigInteger div(BigInteger a, BigInteger b) throws OverflowException {
        if (b.signum() == 0) {
            throw new OverflowException("div", a, b);
        }

        return a.divide(b);
    }
}
