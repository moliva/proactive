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
package org.objectweb.proactive.extra.montecarlo;

import java.io.Serializable;

import org.objectweb.proactive.annotation.PublicAPI;

import umontreal.iro.lecuyer.rng.RandomStream;


/**
 * ExperienceSet
 *
 * This interface defines a Monte-Carlo set of successive experiences, using the provided random generator
 *
 * @author The ProActive Team
 */
@PublicAPI
//@snippet-start montecarlo_simulationset
public interface SimulationSet<T extends Serializable> extends Serializable {

    /**
     * Defines a Monte-Carlo set of successive experiences, a Random generator is given as a parameter
     *
     * A list of double values is expected as output, result of the successive experiences.
     * These experiences can be independant or correlated, this choice is left to the user inside the implementation of this method.
     *
     * @param rng random number generator
     * @return a list of double values
     */
    T simulate(final RandomStream rng);
}
//@snippet-end montecarlo_simulationset
