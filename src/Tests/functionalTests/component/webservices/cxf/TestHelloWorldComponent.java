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
package functionalTests.component.webservices.cxf;

import static org.junit.Assert.assertTrue;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.extensions.webservices.AbstractWebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.WebServicesFactory;
import org.objectweb.proactive.extensions.webservices.client.AbstractClientFactory;
import org.objectweb.proactive.extensions.webservices.client.Client;
import org.objectweb.proactive.extensions.webservices.client.ClientFactory;
import org.objectweb.proactive.extensions.webservices.component.controller.AbstractPAWebServicesControllerImpl;
import org.objectweb.proactive.extensions.webservices.component.controller.PAWebServicesController;
import org.objectweb.proactive.extensions.webservices.exceptions.UnknownFrameWorkException;

import functionalTests.FunctionalTest;
import functionalTests.component.webservices.common.GoodByeWorldItf;
import functionalTests.component.webservices.common.HelloWorldComponent;
import functionalTests.component.webservices.common.HelloWorldItf;


public class TestHelloWorldComponent extends FunctionalTest {

    private String url;
    private Component comp;
    private WebServices ws;
    private PAWebServicesController wsc;

    @org.junit.Before
    public void deployHelloWorldComponent() {

        try {
            url = AbstractWebServicesFactory.getLocalUrl();

            Component boot = Utils.getBootstrapComponent();

            GCMTypeFactory tf = GCM.getGCMTypeFactory(boot);
            GenericFactory cf = GCM.getGenericFactory(boot);

            ComponentType typeComp = tf.createFcType(new InterfaceType[] {
                    tf.createFcItfType("hello-world", HelloWorldItf.class.getName(), false, false, false),
                    tf
                            .createFcItfType("good-bye-world", GoodByeWorldItf.class.getName(), false, false,
                                    false) });

            String controllersConfigFileLocation = AbstractPAWebServicesControllerImpl.getControllerFileUrl(
                    "cxf").getPath();
            ControllerDescription cd = new ControllerDescription("composite", Constants.PRIMITIVE,
                controllersConfigFileLocation);
            comp = cf.newFcInstance(typeComp, cd, new ContentDescription(HelloWorldComponent.class.getName(),
                null));

            GCM.getGCMLifeCycleController(comp).startFc();

            // Deploying the service in the Active Object way
            WebServicesFactory wsf = AbstractWebServicesFactory.getWebServicesFactory("cxf");
            ws = wsf.getWebServices(url);
            ws.exposeComponentAsWebService(comp, "server", new String[] { "hello-world" });
            ws.exposeComponentAsWebService(comp, "server2");

            // Deploying the service using the web service controller
            wsc = org.objectweb.proactive.extensions.webservices.component.Utils
                    .getPAWebServicesController(comp);
            wsc.initServlet();
            wsc.setUrl(url);
            wsc.exposeComponentAsWebService("server3", new String[] { "hello-world" });
            wsc.exposeComponentAsWebService("server4");

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @org.junit.Test
    public void testHelloWorldComponent() {

        ClientFactory cf = null;
        try {
            cf = AbstractClientFactory.getClientFactory("cxf");
        } catch (UnknownFrameWorkException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            assertTrue(false);
        }

        try {

            Client client = cf.getClient(url, "server_hello-world", HelloWorldItf.class);

            client.oneWayCall("putHelloWorld", null);

            boolean containsHello = (Boolean) client.call("contains", new Object[] { "Hello world!" },
                    boolean.class)[0];

            assertTrue(containsHello);

            client.oneWayCall("putTextToSay", new Object[] { "Good bye world!" });

            String text = (String) client.call("sayText", null, String.class)[0];
            assertTrue(text.equals("Hello world!"));

            text = (String) client.call("sayText", null, String.class)[0];
            assertTrue(text.equals("Good bye world!"));

            text = (String) client.call("sayText", null, String.class)[0];
            assertTrue(text.equals("The list is empty"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {

            Client client = cf.getClient(url, "server2_hello-world", HelloWorldItf.class);

            client.oneWayCall("putTextToSay", new Object[] { "Hi ProActive Team!" });

            String text = (String) client.call("sayText", null, String.class)[0];
            assertTrue(text.equals("Hi ProActive Team!"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {
            Client client = cf.getClient(url, "server2_good-bye-world", GoodByeWorldItf.class);

            client.oneWayCall("putGoodByeWorld", null);

            String text = (String) client.call("sayText", null, String.class)[0];
            assertTrue(text.equals("Good bye world!"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @org.junit.Test
    public void testHelloWorldComponent2() {

        ClientFactory cf = null;
        try {
            cf = AbstractClientFactory.getClientFactory("cxf");
        } catch (UnknownFrameWorkException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            assertTrue(false);
        }

        try {

            Client client = cf.getClient(url, "server3_hello-world", HelloWorldItf.class);

            client.oneWayCall("putHelloWorld", null);

            boolean containsHello = (Boolean) client.call("contains", new Object[] { "Hello world!" },
                    boolean.class)[0];

            assertTrue(containsHello);

            client.oneWayCall("putTextToSay", new Object[] { "Good bye world!" });

            String text = (String) client.call("sayText", null, String.class)[0];
            assertTrue(text.equals("Hello world!"));

            text = (String) client.call("sayText", null, String.class)[0];
            assertTrue(text.equals("Good bye world!"));

            text = (String) client.call("sayText", null, String.class)[0];
            assertTrue(text.equals("The list is empty"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {

            Client client = cf.getClient(url, "server4_hello-world", HelloWorldItf.class);

            client.oneWayCall("putTextToSay", new Object[] { "Hi ProActive Team!" });

            String text = (String) client.call("sayText", null, String.class)[0];
            assertTrue(text.equals("Hi ProActive Team!"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {
            Client client = cf.getClient(url, "server4_good-bye-world", GoodByeWorldItf.class);

            client.oneWayCall("putGoodByeWorld", null);

            String text = (String) client.call("sayText", null, String.class)[0];
            assertTrue(text.equals("Good bye world!"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @org.junit.After
    public void undeployHelloWorldComponent() {
        try {
            ws.unExposeComponentAsWebService("server", new String[] { "hello-world" });
            ws.unExposeComponentAsWebService(this.comp, "server2");
            wsc.unExposeComponentAsWebService("server3", new String[] { "hello-world" });
            wsc.unExposeComponentAsWebService("server4");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
