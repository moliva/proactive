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
package org.objectweb.proactive.examples.nbody.groupcom;

import java.io.Serializable;

import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.examples.nbody.common.Deployer;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


/**
 * Synchronization of the computation of the Domains
 */
@ActiveObject
public class Maestro implements Serializable {
    private Domain domainGroup;
    private int nbFinished = 0;
    private int iter = 0;
    private int maxIter;
    private int size;
    private Deployer deployer;

    /**
     * Required by ProActive Active Objects
     */
    public Maestro() {
    }

    /**
     * Create a new master for the simulation, which pilots all the domains given in parameter.
     * @param domainG the group of Domains which are to be controled by this Maestro.
     * @param max the total number of iterations that should be simulated
     */
    public Maestro(Domain domainG, Integer max, Deployer deployer) {

        maxIter = max.intValue();
        domainGroup = domainG;
        size = PAGroup.getGroup(domainGroup).size();
    }

    /**
     * Called by a Domain when computation is finished.
     * This method counts the answers, and restarts all Domains when all have finished.
     */
    public void notifyFinished() {
        nbFinished++;
        if (nbFinished == size) {
            iter++;
            if (iter == maxIter) {
                deployer.terminateAllAndShutdown(false);
                return;
            }
            nbFinished = 0;
            domainGroup.sendValueToNeighbours();
        }
    }
}
