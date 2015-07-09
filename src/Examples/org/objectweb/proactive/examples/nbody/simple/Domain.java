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
package org.objectweb.proactive.examples.nbody.simple;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.examples.nbody.common.Displayer;
import org.objectweb.proactive.examples.nbody.common.Force;
import org.objectweb.proactive.examples.nbody.common.Planet;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


@ActiveObject
public class Domain implements Serializable {
    protected static final Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);
    private int identification; // a unique number to differentiate this Domain from the others
    private Domain[] neighbours; // the list of all the Domains
    private String hostName = "unknown"; // to display on which host we're running
    private Maestro maestro; // used for synchronization
    private Displayer display; // optional, to have a nice output 
    private Planet info; // the information of the body considered
    private Planet[] values; // list of all the bodies within all the other domains
    private int nbvalues; // have we received all values awaited ?
    private int nbReceived = 0; // have we received all values awaited ?

    /**
     * Empty constructor, required by ProActive
     */
    public Domain() {
    }

    /**
     * Creates a container for a Planet, within a region of space.
     * @param i The unique identifier of this Domain
     * @param planet The Planet controlled by this Domain
     */
    public Domain(Integer i, Planet planet) {
        identification = i.intValue();
        info = planet;
        hostName = ProActiveInet.getInstance().getInetAddress().getHostName();
    }

    /**
     * Sets some execution-time related variables.
     * @param domainArray all the other Domains.
     * @param dp The Displayer used to show on screen the movement of the objects.
     * @param master Maestro used to synchronize the computations.
     */
    public void init(Domain[] domainArray, Displayer dp, Maestro master) {
        display = dp; // even if Displayer is null
        maestro = master;
        neighbours = domainArray;
        values = new Planet[domainArray.length];
        values[identification] = null; // null will mean don't compute for this value
        nbvalues = domainArray.length - 1; // will never receive value from self!
        maestro.notifyFinished(); // say we're ready to start .
    }

    /**
     *         Reset all iteration related variables
     */
    public void clearValues() {
        nbReceived = 0;
    }

    /**
     * Work out the movement of the Planet,
     */
    public void moveBody() {
        Force force = new Force();
        for (int i = 0; i < values.length; i++) {
            force.add(info, values[i]); // adds the interaction of the distant body 
        }
        info.moveWithForce(force);
        clearValues();
    }

    /**
     * Called by a distant Domain, this method adds the inf contribution to the force applied on the local Planet
     * @param inf the distant Planet which adds its contribution.
     * @param id the identifier of this distant body.
     */
    public void setValue(Planet inf, int id) {
        values[id] = inf;
        nbReceived++;
        if (nbReceived > nbvalues) { // This is a bad sign!
            System.err.println("Domain " + identification + " received too many answers");
        }
        if (nbReceived == nbvalues) {
            maestro.notifyFinished();
            moveBody();
        }
    }

    /**
     * Triggers the emission of the local Planet to all the other Domains.
     */
    public void sendValueToNeighbours() {
        for (int i = 0; i < neighbours.length; i++)
            if (i != identification) { // don't notify self!
                neighbours[i].setValue(info, identification);
            }
        if (display == null) { // if no display, only the first Domain outputs message to say recompute is going on
            if (identification == 0) {
                logger.info("Compute movement.");
            }
        } else {
            display.drawBody(info.x, info.y, info.z, info.vx, info.vy, info.vz, (int) info.mass,
                    (int) info.diameter, identification, hostName);
        }
    }

    /**
     * Method called when the object is redeployed on a new Node (Fault recovery, or migration).
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        hostName = ProActiveInet.getInstance().getInetAddress().getHostName();
    }
}
