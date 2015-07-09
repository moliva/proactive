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
package org.objectweb.proactive.benchmarks.NAS.MG;

/**
 * Kernel MG
 *
 * A simplified multigrid kernel. It requires highly structured long
 * distance communication and tests both short and long distance data
 * communication.
 * It approximates a solution to the discrete Poisson problem.
 */
public interface MGClasses {

    /**
     * Benchmark name
     */
    public static final String KERNEL_NAME = "MG";
    public static final String OPERATION_TYPE = "floating point";
    public static final int MAXLEVEL = 11;

    /**
     * Common defs
     */
    /*************/

    /*  CLASS S  */

    /*************/
    public static final char S_CLASS_NAME = 'S';
    public static final int S_PROBLEM_SIZE = 32;
    public static final int S_NIT = 4;

    /*************/

    /*  CLASS W  */

    /*************/
    public static final char W_CLASS_NAME = 'W';
    public static final int W_PROBLEM_SIZE = 128;
    public static final int W_NIT = 4;

    /*************/

    /*  CLASS A  */

    /*************/
    public static final char A_CLASS_NAME = 'A';
    public static final int A_PROBLEM_SIZE = 256;
    public static final int A_NIT = 4;

    /*************/

    /*  CLASS B  */

    /*************/
    public static final char B_CLASS_NAME = 'B';
    public static final int B_PROBLEM_SIZE = 256;
    public static final int B_NIT = 20;

    /*************/

    /*  CLASS C  */

    /*************/
    public static final char C_CLASS_NAME = 'C';
    public static final int C_PROBLEM_SIZE = 512;
    public static final int C_NIT = 20;

    /*************/

    /*  CLASS D  */

    /*************/
    public static final char D_CLASS_NAME = 'D';
    public static final int D_PROBLEM_SIZE = 1024;
    public static final int D_NIT = 50;
}
