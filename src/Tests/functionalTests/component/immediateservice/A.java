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
package functionalTests.component.immediateservice;

import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.component.body.ComponentInitActive;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;


public class A implements Itf, ComponentInitActive {
    private volatile boolean condition = true;

    /**
     * Initialize the immediate service method in the initComponentActivity
     */
    public void initComponentActivity(Body body) {
        PAActiveObject.setImmediateService("immediateMethod", new Class[] { String.class });
        //        ProActive.setImmediateService("startFc",
        //                new Class[] { });
        PAActiveObject.setImmediateService("immediateStopLoopMethod");
        //ProActive.setImmediateService("startFc");
    }

    public StringWrapper immediateMethod(String arg) {
        System.err.println("COMPONENT: immediateMethod: " + arg);
        StringWrapper res = new StringWrapper(arg + "success");
        condition = false;
        return res;
    }

    /**
     * This method never terminate, unless that the immediateStopLoopMethod (an immediate service) is called.
     */
    public void loopQueueMethod() {
        System.err.println("COMPONENT: loopQueueMethod: BEGINNING");
        while (condition) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.err.println("COMPONENT: loopQueueMethod: END");
    }

    public void immediateStopLoopMethod() {
        System.err.println("COMPONENT: immediateStopLoopMethod");
        condition = false;
    }

    public void startFc() throws IllegalLifeCycleException {
        System.err.println("MY_startFc");
    }
}
