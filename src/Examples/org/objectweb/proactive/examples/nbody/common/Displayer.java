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

import java.io.Serializable;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


/** This very limited class is needed as an Active Object class, containing the GUI.
 * The swing GUI is attached as a field of the Active Object, so it can be recreated
 * from scratch. */
@ActiveObject
public class Displayer implements Serializable {
    private transient NBodyFrame nbf;
    private boolean displayft;
    private int nbBodies;
    private boolean ddd = false;

    public Displayer() {
    }

    public Displayer(Integer nbBodies, Boolean displayft, Deployer deployer, BooleanWrapper enable3D) {
        this.nbBodies = nbBodies.intValue();
        this.displayft = displayft.booleanValue();
        if (!enable3D.getBooleanValue()) {
            nbf = new NBody2DFrame("ProActive N-Body", this.nbBodies, this.displayft, deployer);
        } else {
            ddd = true;
            // For compiling without Java 3D installed
            try {
                nbf = (NBodyFrame) Class
                        .forName("org.objectweb.proactive.examples.nbody.common.NBody3DFrame")
                        .getConstructor(
                                new Class[] { String.class, Integer.class, Boolean.class, Start.class })
                        .newInstance(
                                new Object[] { "ProActive N-Body", Integer.valueOf(this.nbBodies),
                                        Boolean.valueOf(this.displayft), deployer });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void drawBody(double x, double y, double z, double vx, double vy, double vz, int weight, int d,
            int id, String name) {
        if (!ddd) {
            nbf.drawBody(x, y, z, vx, vy, vz, weight, d, id, name);
        } else {
            // Doesn't work wothout / 1000, Igor (or Alex I don't know) doesn't know why ;)
            nbf.drawBody(x / 1000, y / 1000, z / 1000, vx, vy, vz, weight, d, id, name);
        }
    }
}
