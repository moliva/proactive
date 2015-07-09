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
package functionalTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extensions.dataspaces.core.DataSpacesURI;
import org.objectweb.proactive.extensions.dataspaces.core.InputOutputSpaceConfiguration;
import org.objectweb.proactive.extensions.dataspaces.core.ScratchSpaceConfiguration;
import org.objectweb.proactive.extensions.dataspaces.core.SpaceInstanceInfo;
import org.objectweb.proactive.extensions.dataspaces.core.naming.NamingService;
import org.objectweb.proactive.extensions.dataspaces.core.naming.NamingServiceDeployer;
import org.objectweb.proactive.extensions.dataspaces.exceptions.ApplicationAlreadyRegisteredException;
import org.objectweb.proactive.extensions.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extensions.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extensions.dataspaces.exceptions.WrongApplicationIdException;

import functionalTests.FunctionalTest;


/**
 * Test NamingService on the local default node.
 */
public class TestRemoteNamingService extends FunctionalTest {
    // extends GCMFunctionalTestDefaultNodes {

    private static final String NAME = "DSnamingservice";

    private NamingService stub;

    protected static final long MAIN_APPID = 1;

    protected static final long ANOTHER_APPID1 = 0;

    protected static final long ANOTHER_APPID2 = 2;

    protected SpaceInstanceInfo spaceInstanceInput1;

    protected SpaceInstanceInfo spaceInstanceInput1b;

    protected SpaceInstanceInfo spaceInstanceInput1c;

    protected SpaceInstanceInfo spaceInstanceInput2;

    protected SpaceInstanceInfo spaceInstanceOutput1;

    protected SpaceInstanceInfo spaceInstanceOutput1b;

    protected SpaceInstanceInfo spaceInstanceOutput2;

    protected SpaceInstanceInfo spaceInstanceScratch;

    private NamingServiceDeployer remoteObjectDeployer;

    public TestRemoteNamingService() throws ConfigurationException {
        // super(1, 1);

        InputOutputSpaceConfiguration configInput1 = InputOutputSpaceConfiguration
                .createInputSpaceConfiguration("http://hostA", "/tmp", "h1", "input1");
        InputOutputSpaceConfiguration configInput2 = InputOutputSpaceConfiguration
                .createInputSpaceConfiguration("http://hostB", "/tmp", "h1", "input2");
        InputOutputSpaceConfiguration configOutput1 = InputOutputSpaceConfiguration
                .createOutputSpaceConfiguration("http://hostC", "/tmp", "h1", "output1");
        ScratchSpaceConfiguration configScratch = new ScratchSpaceConfiguration("http://hostD", "/tmp", "h1");
        InputOutputSpaceConfiguration configOutput2 = InputOutputSpaceConfiguration
                .createOutputSpaceConfiguration("http://hostA", "/tmp", "h1", "output2");

        spaceInstanceInput1 = new SpaceInstanceInfo(MAIN_APPID, configInput1);
        spaceInstanceInput1b = new SpaceInstanceInfo(ANOTHER_APPID1, configInput1);
        spaceInstanceInput1c = new SpaceInstanceInfo(ANOTHER_APPID2, configInput1);

        spaceInstanceInput2 = new SpaceInstanceInfo(MAIN_APPID, configInput2);

        spaceInstanceOutput1 = new SpaceInstanceInfo(MAIN_APPID, configOutput1);
        spaceInstanceOutput1b = new SpaceInstanceInfo(ANOTHER_APPID1, configOutput1);
        spaceInstanceOutput2 = new SpaceInstanceInfo(MAIN_APPID, configOutput2);

        spaceInstanceScratch = new SpaceInstanceInfo(MAIN_APPID, "node1", "rt1", configScratch);
    }

    @Before
    public void before() throws ProActiveException, URISyntaxException {
        remoteObjectDeployer = new NamingServiceDeployer();

        final String url = remoteObjectDeployer.getNamingServiceURL();
        stub = NamingService.createNamingServiceStub(url);
    }

    @Test
    public void test() throws ApplicationAlreadyRegisteredException, WrongApplicationIdException,
            SpaceAlreadyRegisteredException, IllegalArgumentException {

        Set<SpaceInstanceInfo> spaces = new HashSet<SpaceInstanceInfo>();
        Set<Long> appsRegistered;

        spaces.add(spaceInstanceInput1);
        spaces.add(spaceInstanceInput2);
        spaces.add(spaceInstanceOutput1);
        spaces.add(spaceInstanceOutput2);

        assertFalse(stub.isApplicationIdRegistered(MAIN_APPID));
        // TEST REGISTER APP
        stub.registerApplication(MAIN_APPID, spaces);

        // check if everything has been registered
        assertTrue(stub.isApplicationIdRegistered(MAIN_APPID));
        appsRegistered = stub.getRegisteredApplications();
        appsRegistered.contains(MAIN_APPID);

        // TEST LOOKUP FIRST
        assertIsSpaceRegistered(spaceInstanceInput1);
        assertIsSpaceRegistered(spaceInstanceInput2);
        assertIsSpaceRegistered(spaceInstanceOutput1);
        assertIsSpaceRegistered(spaceInstanceOutput2);

        // TEST LOOKUP ALL
        final DataSpacesURI query = DataSpacesURI.createURI(MAIN_APPID);
        final Set<SpaceInstanceInfo> actual = stub.lookupMany(query);
        assertEquals(spaces, actual);

        // TEST UNREGISTER
        assertTrue(stub.unregister(spaceInstanceInput1.getMountingPoint()));
        assertTrue(stub.unregister(spaceInstanceOutput1.getMountingPoint()));

        // TEST LOOKUP FIRST WITH NULL ANSWER
        assertIsSpaceUnregistered(spaceInstanceInput1);
        assertIsSpaceUnregistered(spaceInstanceOutput1);

        // TEST REGISTER
        stub.register(spaceInstanceInput1);
        stub.register(spaceInstanceOutput1);

        // TEST EXCEPTION WHEN SPACE ALREADY REGISTERED
        try {
            stub.register(spaceInstanceInput1);
            fail("Exception expected");
        } catch (SpaceAlreadyRegisteredException e) {
        } catch (Exception e) {
            fail("Expected exception of different type");
        }

        // TEST EXCEPTION WHEN APP NOT REGISTERED
        try {
            stub.register(spaceInstanceInput1b);
            fail("Exception expected");
        } catch (WrongApplicationIdException e) {
        } catch (Exception e) {
            fail("Expected exception of different type");
        }

        // TEST EXCEPTION WHEN APP ALREADY REGISTERED
        try {
            stub.registerApplication(MAIN_APPID, null);
            fail("Exception expected");
        } catch (ApplicationAlreadyRegisteredException e) {
        } catch (Exception e) {
            fail("Expected exception of different type");
        }

        // TEST UNREGISTER APP
        stub.unregisterApplication(MAIN_APPID);
    }

    @After
    public void after() throws ProActiveException {
        if (remoteObjectDeployer != null) {
            remoteObjectDeployer.terminate();
            remoteObjectDeployer = null;
            stub = null;
        }
    }

    private void assertIsSpaceRegistered(SpaceInstanceInfo expected) {
        SpaceInstanceInfo actual = stub.lookupOne(expected.getMountingPoint());
        assertEquals(actual.getMountingPoint(), expected.getMountingPoint());
    }

    private void assertIsSpaceUnregistered(SpaceInstanceInfo expected) {
        assertNull(stub.lookupOne(expected.getMountingPoint()));
    }
}
