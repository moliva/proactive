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
package org.objectweb.proactive.examples.nbody.barneshut;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.examples.nbody.common.Deployer;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


/**
 * Synchronization of the others Maestro
 */
@ActiveObject
public class BigMaestro implements Serializable {

    /** Counts the number of Maestro that have respond */
    private int nbFinished = 0;

    /** Number of iteration at a time */
    private int iter = 0;

    /** Number of iteration maximum */
    private int maxIter;

    /** List of all the Planets */
    private List<Planet> lPlanets;

    /** References on all the Active Maestro */
    private Maestro[] maestroArray;

    private Deployer deployer;

    /**
     * Required by ProActive
     */
    public BigMaestro() {
    }

    /**
     * Create a new master for the simulation, which pilots all the Maestro given in parameter.
     * @param maestroArray the group of Maestro which are to be controled by this BigMaestro.
     * @param max the total number of iterations that should be simulated
     * @param killsupport KillSupport
     */
    public BigMaestro(Maestro[] maestroArray, Integer max, Deployer deployer) {
        this.deployer = deployer;
        maxIter = max.intValue();
        this.maestroArray = maestroArray;
        // All the Maestro have a list of 8 Planets
        lPlanets = new ArrayList<Planet>(maestroArray.length * 8);
        for (int i = 0; i < maestroArray.length * 8; i++)
            lPlanets.add(null);
    }

    /**
     * Called by a Maestro when all of this Domain have finished computation.
     * This method counts the calls, and responds all Maestro for continuing if needed.
     * @param id the identification of the Maestro that have finished
     * @param lPla the list of Planets that contains the given Maestro
     */
    public void notifyFinished(int id, List<Planet> lPla) {
        nbFinished++; // one another have finished

        // update of the new planets's positions
        for (int i = 0; i < lPla.size(); i++)
            lPlanets.set(id * 8 + i, lPla.get(i));

        // next iteration
        if (nbFinished == maestroArray.length) {
            nbFinished = 0;
            iter++;
            if (iter == maxIter) {
                deployer.terminateAllAndShutdown(false);
                return;
            }

            // Restart all the Maestro
            for (int i = 0; i < maestroArray.length; i++)
                maestroArray[i].finished();
        }
    }
}
