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

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.examples.nbody.common.Cube;
import org.objectweb.proactive.examples.nbody.common.Deployer;
import org.objectweb.proactive.examples.nbody.common.Displayer;
import org.objectweb.proactive.examples.nbody.common.Planet;


/**
 * <P>
 * Starts the simulation running the groupcom example. Domains are synchronized by a Maestro,
 * but communicate with Group communication, which is an improvement compared to several
 * sequential communications.
 * </P>
 *
 * @author The ProActive Team
 * @version 1.0,  2005/04
 * @since   ProActive 2.2
 */
public class Start {
    protected static final Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    public static void main(String[] args) {
        org.objectweb.proactive.examples.nbody.common.Start.main(args);
    }

    /**
     * Called by common.Start if this version is selected.
     */
    public static void main(int totalNbBodies, int maxIter, Displayer displayer, Deployer deployer) {
        logger.info("RUNNING groupcom VERSION");

        Node[] nodes = deployer.getWorkerNodes();

        Cube universe = new Cube(-100, -100, -100, 200, 200, 200);
        Object[][] constructorParams = new Object[totalNbBodies][3];
        for (int i = 0; i < totalNbBodies; i++) {
            constructorParams[i][0] = new Integer(i);
            // coordinates between -100,-100 and 100,100
            constructorParams[i][1] = new Planet(universe);
            constructorParams[i][2] = deployer;
        }
        Domain domainGroup = null;
        try {
            // Create all the Domains as part of a Group
            domainGroup = (Domain) PAGroup.newGroup(Domain.class.getName(), constructorParams, nodes);
        } catch (ClassNotReifiableException e) {
            deployer.abortOnError(e);
        } catch (ClassNotFoundException e) {
            deployer.abortOnError(e);
        } catch (ActiveObjectCreationException e) {
            deployer.abortOnError(e);
        } catch (NodeException e) {
            deployer.abortOnError(e);
        }

        // Add the reference on the Domain to the deployer
        deployer.addAoReference(domainGroup);

        logger.info("[NBODY] " + totalNbBodies + " Domains are deployed");

        Maestro maestro = null;
        try {
            // Supervizes the synchronizations
            maestro = PAActiveObject.newActive(Maestro.class, new Object[] { domainGroup,
                    new Integer(maxIter), deployer }, nodes[nodes.length - 1]);
        } catch (ActiveObjectCreationException e) {
            deployer.abortOnError(e);
        } catch (NodeException e) {
            deployer.abortOnError(e);
        }

        // Add the reference on the Maestro to the deployer
        deployer.addAoReference(maestro);

        // init workers
        domainGroup.init(domainGroup, displayer, maestro);

        // launch computation
        domainGroup.sendValueToNeighbours();
    }
}
