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
package org.objectweb.proactive.examples.timitspmd.example2;

import java.util.Random;

import org.objectweb.proactive.extensions.timitspmd.util.TimItStore;
import org.objectweb.proactive.extensions.timitspmd.util.Timed;
import org.objectweb.proactive.extensions.timitspmd.util.TimerCounter;


/**
 * A simple distributed application that use TimIt.<br>
 * The application have three classes : Launcher, Worker and Root<br>
 * Launcher will deploy some Workers to do a job. Theses Workers will use a Root
 * instance to do it.
 *
 * See the source code of these classes to know how use TimIt
 *
 * @author The ProActive Team
 *
 */

// A timed class MUST extends Timed
public class Root extends Timed {

    /**
     *
     */
    public static final String TNAME_FOO = "foo";
    public static final String TNAME_BAR = "bar";
    private TimerCounter T_FOO;
    private TimerCounter T_BAR;
    private TimItStore ts;
    private Random rand;

    // Constructor
    public Root() {
        // First you have to get an instance of TimerStore for this
        // active object
        this.ts = TimItStore.getInstance(this);

        // Then you must create all counters with this TimItStore instance
        // By the way, theses counters will be available from every class in
        // this active object
        this.T_FOO = this.ts.addTimerCounter(new TimerCounter(Root.TNAME_FOO));
        this.T_BAR = this.ts.addTimerCounter(new TimerCounter(Root.TNAME_BAR));

        this.rand = new Random();
    }

    // A foo method that compute many random numbers thanks
    // to a gaussian scheme (slower than standard scheme)
    public void foo() {
        this.T_FOO.start();
        //        for (int i = 0; i < 98765; i++) {
        //            this.rand.nextGaussian(); // timed
        //        }
        sleep(45);
        this.T_FOO.stop();

        this.rand.nextGaussian(); // not timed
    }

    // A bar method that compute only one random number
    public void bar() {
        this.T_BAR.start();
        //        this.rand.nextGaussian();
        sleep(67);
        this.T_BAR.stop();
    }

    private void sleep(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
