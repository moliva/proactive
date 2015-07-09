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
package functionalTests.ft;

import java.io.Serializable;


/**
 * @author The ProActive Team
 */
public class Agent implements Serializable {

    /**
     *
     */
    private Agent neighbour;
    private int counter;
    private int iter;
    private Collector launcher;

    public Agent() {
    }

    public void initCounter(int value) {
        this.counter = value;
        this.iter = 0;
    }

    public void setNeighbour(Agent n) {
        this.neighbour = n;
    }

    public void setLauncher(Collector l) {
        this.launcher = l;
    }

    public ReInt doStuff(ReInt param) {
        this.counter += param.getValue();
        return new ReInt(this.counter);
    }

    public ReInt getCounter() {
        return new ReInt(this.counter);
    }

    public void startComputation(int max) {
        iter++;
        ReInt a = this.neighbour.doStuff(new ReInt(this.counter));
        ReInt b = this.neighbour.doStuff(new ReInt(this.counter));
        ReInt c = this.neighbour.doStuff(new ReInt(this.counter));
        ReInt d = this.neighbour.doStuff(new ReInt(this.counter));
        this.counter += a.getValue();
        this.counter += b.getValue();
        this.counter += c.getValue();
        this.counter += d.getValue();

        if (iter < max) {
            neighbour.startComputation(max);
        } else {
            this.launcher.finished(this.counter);
        }
    }
}
