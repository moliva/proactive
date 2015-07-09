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
package org.objectweb.proactive.examples.nbody.common;

public interface NBodyFrame {

    /**
     * size of the screen
     */
    public final static int SIZE = 500;

    /**
     * Maximum of ancient position of body (aka Ghost) saved.
     * Reduces this number to increase performance
     * Example of Complexities :
     * <li> Creation of java3d scene tree is at <B>O(MAX_HISTO_SIZE*nbBodies)</B><li>
     * <li> Drawing of trace is at <B>O(MAX_HISTO_SIZE*nbBodies)</B>
     */
    public final static int MAX_HISTO_SIZE = 100;

    /**
     * Method Invoked by remote bodies
     * @param x new x of the body
     * @param y new y of the body
     * @param z new z of the body
     * @param vx new vx of the body
     * @param vy new vy of the body
     * @param vz new vz of the body
     * @param mass mass of the body (INCOHERENT !)
     * @param diameter diameter of the body (DOUBLON D INFO, et INCOHERENT)
     * @param identification id of the body who call the method
     * @param hostName where the body is hosted
     */
    public abstract void drawBody(double x, double y, double z, double vx, double vy, double vz, int mass,
            int diameter, int identification, String hostName);
}
