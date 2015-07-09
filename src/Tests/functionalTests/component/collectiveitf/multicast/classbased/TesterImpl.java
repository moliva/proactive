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
package functionalTests.component.collectiveitf.multicast.classbased;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.ProActiveRuntimeException;

import functionalTests.component.collectiveitf.multicast.MulticastTestItf;
import functionalTests.component.collectiveitf.multicast.Test;
import functionalTests.component.collectiveitf.multicast.Tester;
import functionalTests.component.collectiveitf.multicast.WrappedInteger;


public class TesterImpl implements Tester, BindingController {
    MulticastTestItf clientItf;
    OneToOneMulticast oneToOneMulticastClientItf = null;
    BroadcastMulticast broadcastMulticastClientItf = null;

    public void testConnectedServerMulticastItf() throws Exception {
    }

    public void testOwnClientMulticastItf() throws Exception {
        List<WrappedInteger> listParameter = new ArrayList<WrappedInteger>();
        for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
            listParameter.add(i, new WrappedInteger(i));
        }
        List<WrappedInteger> result;

        result = broadcastMulticastClientItf.dispatch(listParameter);
        Assert.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
        for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
            Assert.assertTrue(result.contains(new WrappedInteger(i)));
        }

        result = oneToOneMulticastClientItf.dispatch(listParameter);
        Assert.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
        for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
            Assert.assertTrue(result.get(i).equals(new WrappedInteger(i)));
        }
    }

    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if ("oneToOneMulticastClientItf".equals(clientItfName) && (serverItf instanceof OneToOneMulticast)) {
            oneToOneMulticastClientItf = (OneToOneMulticast) serverItf;
        } else if ("broadcastMulticastClientItf".equals(clientItfName) &&
            (serverItf instanceof BroadcastMulticast)) {
            broadcastMulticastClientItf = (BroadcastMulticast) serverItf;
        } else {
            throw new ProActiveRuntimeException("cannot find multicast interface " + clientItfName);
        }
    }

    public String[] listFc() {
        return new String[] { "oneToOneMulticastClientItf", "broadcastMulticastClientItf" };
    }

    public Object lookupFc(String clientItfName) throws NoSuchInterfaceException {
        if ("oneToOneMulticastClientItf".equals(clientItfName)) {
            return oneToOneMulticastClientItf;
        }
        if ("broadcastMulticastClientItf".equals(clientItfName)) {
            return broadcastMulticastClientItf;
        }
        throw new NoSuchInterfaceException(clientItfName);
    }

    public void unbindFc(String clientItfName) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if ("oneToOneMulticastClientItf".equals(clientItfName)) {
            oneToOneMulticastClientItf = null;
        } else if ("broadcastMulticastClientItf".equals(clientItfName)) {
            broadcastMulticastClientItf = null;
        } else {
            throw new ProActiveRuntimeException("cannot find multicast interface " + clientItfName);
        }
    }
}
