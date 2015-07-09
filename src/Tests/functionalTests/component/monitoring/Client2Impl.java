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
package functionalTests.component.monitoring;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.wrapper.IntMutableWrapper;


public class Client2Impl implements Runner, BindingController {
    private static final long SLEEP_TIME = 20;
    private static final String[] ITF_NAMES_FOR_EACH_METHOD = { "service2", "service2", "service2" }; // , "service3", "service3" };
    private static final String[] METHOD_NAMES = { "doAnotherThing", "getDouble", "getBoolean" }; //, "foo", "executeAlone" };
    private static final int NB_ITERATIONS = 100;
    private Service2 service2;
    private Service3 service3;
    private int[] nbCallsPerMethod = new int[METHOD_NAMES.length];

    private void sleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        for (int i = 0; i < METHOD_NAMES.length; i++) {
            nbCallsPerMethod[i] = 0;
        }
        for (int i = 0; i < NB_ITERATIONS; i++) {
            sleep();
            int indexMethod = i % METHOD_NAMES.length;
            nbCallsPerMethod[indexMethod]++;
            switch (indexMethod) {
                case 0:
                    service2.doAnotherThing();
                    break;
                case 1:
                    service2.getDouble();
                    break;
                case 2:
                    service2.getBoolean();
                    break;
                case 3:
                    service3.foo(new IntMutableWrapper(2));
                    break;
                case 4:
                    service3.executeAlone();
                    break;
                default:
                    break;
            }
        }
    }

    public int getTotalNbMethodCalls() {
        return NB_ITERATIONS;
    }

    public long getSleepTime() {
        return SLEEP_TIME;
    }

    public String[] getItfNamesForEachMethod() {
        return ITF_NAMES_FOR_EACH_METHOD;
    }

    public String[] getMethodNames() {
        return METHOD_NAMES;
    }

    public int[] getNbCallsPerMethod() {
        return nbCallsPerMethod;
    }

    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if ("service2".equals(clientItfName)) {
            service2 = (Service2) serverItf;
        } else if ("service3".equals(clientItfName)) {
            service3 = (Service3) serverItf;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public String[] listFc() {
        return new String[] { "service2", "service3" };
    }

    public Object lookupFc(String clientItfName) throws NoSuchInterfaceException {
        if ("service2".equals(clientItfName)) {
            return service2;
        } else if ("service3".equals(clientItfName)) {
            return service3;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public void unbindFc(String arg0) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        throw new ProActiveRuntimeException("not implemented!");
    }
}
