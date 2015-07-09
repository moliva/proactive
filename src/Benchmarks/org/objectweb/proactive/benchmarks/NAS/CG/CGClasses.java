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
package org.objectweb.proactive.benchmarks.NAS.CG;

/**
 * NAS PARALLEL BENCHMARKS
 * 
 * Kernel CG
 * 
 * A conjugate gradient method is used to compute an approximation
 * to the smallest eigenvalue of a large, sparse, symmetric positive
 * definite matrix. This kernel is typical of unstructured grid
 * computations in that it tests irregular long distance communication,
 * employing unstructured matrix vector multiplication.
 */
public interface CGClasses {

    /**
     * Benchmark name
     */
    public static String KERNEL_NAME = "CG";
    public String OPERATION_TYPE = "floating point";

    /**
     * Common defs
     */
    public static final double RCOND = 0.1;

    /*************/

    /*  CLASS S  */

    /*************/
    public static final char S_CLASS_NAME = 'S';
    public static final int S_NA = 1400;
    public static final int S_NON_ZER = 7;
    public static final int S_SHIFT = 10;
    public static final int S_NITER = 15;
    public static final double S_zeta_verify_value = 8.5971775078648;

    /*************/

    /*  CLASS W  */

    /*************/
    public static final char W_CLASS_NAME = 'W';
    public static final int W_NA = 7000;
    public static final int W_NON_ZER = 8;
    public static final int W_SHIFT = 12;
    public static final int W_NITER = 15;
    public static final double W_zeta_verify_value = 10.362595087124;

    /*************/

    /*  CLASS A  */

    /*************/
    public static final char A_CLASS_NAME = 'A';
    public static final int A_NA = 14000;
    public static final int A_NON_ZER = 11;
    public static final int A_SHIFT = 20;
    public static final int A_NITER = 15;
    public static final double A_zeta_verify_value = 17.130235054029;

    /*************/

    /*  CLASS B  */

    /*************/
    public static final char B_CLASS_NAME = 'B';
    public static final int B_NA = 75000;
    public static final int B_NON_ZER = 13;
    public static final int B_SHIFT = 60;
    public static final int B_NITER = 75;
    public static final double B_zeta_verify_value = 22.712745482631;

    /*************/

    /*  CLASS C  */

    /*************/
    public static final char C_CLASS_NAME = 'C';
    public static final int C_NA = 150000;
    public static final int C_NON_ZER = 15;
    public static final int C_SHIFT = 110;
    public static final int C_NITER = 75;
    public static final double C_zeta_verify_value = 28.973605592845;

    /*************/

    /*  CLASS D  */

    /*************/
    public static final char D_CLASS_NAME = 'D';
    public static final int D_NA = 1500000;
    public static final int D_NON_ZER = 21;
    public static final int D_SHIFT = 500;
    public static final int D_NITER = 100;
    public static final double D_zeta_verify_value = 52.5145321058;
}
