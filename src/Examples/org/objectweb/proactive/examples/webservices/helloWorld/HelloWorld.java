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
package org.objectweb.proactive.examples.webservices.helloWorld;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extensions.webservices.AbstractWebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.WebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WebServicesInitActiveFactory;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


/**
 * A simple example to expose an active object as a web service.
 *
 * @author The ProActive Team
 */
@ActiveObject
public class HelloWorld implements Serializable { //, InitActive {

    private final static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    private LinkedList<String> textsToSay = new LinkedList<String>();

    public HelloWorld() {
    }

    public String helloWorld() {
        return "Hello world !";
    }

    // This method is used to check
    // that it is not inserted in the wsdl
    // and not callable.
    public String toString() {
        return "HelloWorld";
    }

    public void putTextToSay(String textToSay) {
        this.textsToSay.add(textToSay);
    }

    public void putHelloWorld() {
        this.textsToSay.add("Hello World!");
    }

    public String sayText() {
        String str;
        if (this.textsToSay.isEmpty()) {
            str = "The list is empty";
        } else {
            str = this.textsToSay.poll();
        }
        return str;
    }

    public String putTextToSayAndConfirm(String textToSay) {
        this.textsToSay.add(textToSay);
        return "The text \"" + textToSay + "\" has been inserted into the list";
    }

    public static void main(String[] args) {
        try {
            File applicationDescriptor = null;
            String url = "";
            String wsFrameWork = "";
            if (args.length == 0) {
                url = AbstractWebServicesFactory.getLocalUrl();
                wsFrameWork = CentralPAPropertyRepository.PA_WEBSERVICES_FRAMEWORK.getValue();
            } else if (args.length == 1) {
                if (args[0].endsWith(".xml")) {
                    applicationDescriptor = new File(args[0]);
                    url = AbstractWebServicesFactory.getLocalUrl();
                    wsFrameWork = CentralPAPropertyRepository.PA_WEBSERVICES_FRAMEWORK.getValue();
                } else {
                    try {
                        new URL(args[0]);
                        url = args[0];
                        wsFrameWork = CentralPAPropertyRepository.PA_WEBSERVICES_FRAMEWORK.getValue();
                    } catch (MalformedURLException me) {
                        // Given argument is not the URL to use to expose the web service, it should be the web service framework to use
                        url = AbstractWebServicesFactory.getLocalUrl();
                        wsFrameWork = args[0];
                    }
                }
            } else if (args.length == 2) {
                if (args[0].endsWith(".xml")) {
                    applicationDescriptor = new File(args[0]);
                    try {
                        new URL(args[1]);
                        url = args[1];
                        wsFrameWork = CentralPAPropertyRepository.PA_WEBSERVICES_FRAMEWORK.getValue();
                    } catch (MalformedURLException me) {
                        // Given argument is not the URL to use to expose the web service, it should be the web service framework to use
                        url = AbstractWebServicesFactory.getLocalUrl();
                        wsFrameWork = args[1];
                    }
                } else {
                    url = args[0];
                    wsFrameWork = args[1];
                }
            } else if (args.length == 3) {
                applicationDescriptor = new File(args[0]);
                url = args[1];
                wsFrameWork = args[2];
            } else {
                logger.info("Wrong number of arguments:");
                logger.info("Usage: java HelloWorld [GCMA] [url] [wsFrameWork]");
                System.out.println("with wsFrameWork should be 'cxf'");
                return;
            }
            HelloWorld hw;

            if (applicationDescriptor != null) {
                logger.info("Using a GCM Deployment");

                GCMApplication gcmad = PAGCMDeployment.loadApplicationDescriptor(applicationDescriptor);

                gcmad.startDeployment();

                GCMVirtualNode hello = gcmad.getVirtualNode("Hello");

                if (hello == null) {
                    throw new ProActiveException("Hello virtual node is not defined");
                }

                Node node1 = hello.getANode();

                hw = (HelloWorld) PAActiveObject
                        .newActive("org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld",
                                null, new Object[] {}, node1, WebServicesInitActiveFactory
                                        .getInitActive(wsFrameWork), null);

                //                    hw = (HelloWorld) PAActiveObject.newActive(
                //                            "org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld",new Object[] {});
            } else {
                logger.info("Not using a GCM Deployment");

                hw = (HelloWorld) PAActiveObject
                        .newActive("org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld",
                                new Object[] {});
            }

            WebServicesFactory wsf;
            wsf = AbstractWebServicesFactory.getWebServicesFactory(wsFrameWork);
            WebServices ws = wsf.getWebServices(url);

            ws.exposeAsWebService(hw, "HelloWorld", new String[] { "putTextToSay", "sayText",
                    "putHelloWorld", "putTextToSayAndConfirm" });

        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
