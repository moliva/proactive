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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.objectweb.proactive.core.util.ProActiveInet;


/**
 * This class handles a partial computation of PI. It is also used for workers in the component version of the application.
 *
 * @author The ProActive Team
 *
 */
public class PiComputer implements PiComp {
    private static final int ROUND_MODE = BigDecimal.ROUND_HALF_EVEN;
    private BigDecimal ZERO = new BigDecimal("0");
    private BigDecimal ONE = new BigDecimal("1");
    private BigDecimal OPPOSITE_ONE = new BigDecimal("-1");
    private BigDecimal TWO = new BigDecimal("2");
    private BigDecimal OPPOSITE_TWO = new BigDecimal("-2");
    private BigDecimal FOUR = new BigDecimal("4");
    private BigDecimal FIVE = new BigDecimal("5");
    private BigDecimal SIX = new BigDecimal("6");
    private BigDecimal EIGHT = new BigDecimal("8");
    private BigDecimal OPPOSITE_EIGHT = new BigDecimal("-8");
    private BigInteger SIXTEEN = new BigInteger("16");

    /**
     * Empty constructor
     */
    public PiComputer() {
    }

    /**
     * Constructor with scale parameter
     * @param scaleObject The scale parameter for the setScale method
     */
    public PiComputer(Integer scaleObject) {
        System.out.println("created PiComputer on host " +
            ProActiveInet.getInstance().getInetAddress().getHostName());
        setScale(scaleObject);
    }

    public void setScale(Integer scale) {
        ZERO = ZERO.setScale(scale);
        ONE = ONE.setScale(scale);
        OPPOSITE_ONE = OPPOSITE_ONE.setScale(scale);
        TWO = TWO.setScale(scale);
        OPPOSITE_TWO = OPPOSITE_TWO.setScale(scale);
        FOUR = FOUR.setScale(scale);
        FIVE = FIVE.setScale(scale);
        SIX = SIX.setScale(scale);
        EIGHT = EIGHT.setScale(scale);
        OPPOSITE_EIGHT = OPPOSITE_EIGHT.setScale(scale);
    }

    public Result compute(Interval interval) {
        System.out.println("Starting computation for interval [" + interval.getBeginning() + " , " +
            interval.getEnd() + "] on host : " + ProActiveInet.getInstance().getInetAddress().getHostName());
        long startChrono = System.currentTimeMillis();

        BigDecimal bd = ZERO;

        // BBP formula for the given interval
        for (int k = interval.getBeginning().intValue(); k <= interval.getEnd().intValue(); k++) {
            bd = bd.add(f(k));
        }

        return new Result(bd, System.currentTimeMillis() - startChrono);
    }

    private BigDecimal f(int k) {
        BigDecimal K = new BigDecimal(k);
        BigDecimal EIGHT_K = EIGHT.multiply(K);
        BigDecimal FIRST = ONE.divide(new BigDecimal(SIXTEEN.pow(k)), ROUND_MODE);
        BigDecimal SECOND = FOUR.divide(EIGHT_K.add(ONE), ROUND_MODE);
        BigDecimal THIRD = OPPOSITE_TWO.divide(EIGHT_K.add(FOUR), ROUND_MODE);
        BigDecimal FOURTH = OPPOSITE_ONE.divide(EIGHT_K.add(FIVE), ROUND_MODE);
        BigDecimal FIFTH = OPPOSITE_ONE.divide(EIGHT_K.add(SIX), ROUND_MODE);

        return FIRST.multiply(SECOND.add(THIRD.add(FOURTH.add(FIFTH))));
    }
}
