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
package org.objectweb.proactive.extra.montecarlo.basic;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extra.montecarlo.SimulationSet;

import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.randvar.NormalGen;
import umontreal.iro.lecuyer.rng.RandomStream;


/**
 * BrownianBridge
 * 
 * @author The ProActive Team
 *
 */
@PublicAPI
public class BrownianBridge implements SimulationSet<double[]> {

    private double w0, wT, t, T;

    /**
     * @param w0 Known fixed at time 0 value of the Brownian motion
     * @param wT Know fixed at time T value of the Brownian motion
     * @param t  A given time
     * @param T Maturity date
     */
    public BrownianBridge(double w0, double wT, double t, double T) {
        super();
        this.w0 = w0;
        this.wT = wT;
        this.t = t;
        this.T = T;
    }

    /**
     * The Brownian Bridge is used to generate new samples between two known samples of a Brownian motion path.
     * i.e generate sample at time t using sample at time 0 and at T, with 0<t<T
     */
    public double[] simulate(RandomStream rng) {
        final double[] answer = new double[1];
        NormalGen ngen = new NormalGen(rng, new NormalDist());
        answer[0] = w0 + (t / T) * (wT - w0) + Math.sqrt(t * (T - t) / T) * ngen.nextDouble();
        return answer;
    }

}
