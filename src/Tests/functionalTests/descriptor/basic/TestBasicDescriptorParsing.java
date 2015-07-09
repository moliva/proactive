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
package functionalTests.descriptor.basic;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationParserImpl;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentParserImpl;
import org.xml.sax.SAXException;


public class TestBasicDescriptorParsing {

    //    @Test
    public void oldDeploymentDescriptorParse() throws Exception {
        String descriptorLocation = getClass().getResource("javaproperty_ERROR.xml").getPath();

        Object proActiveDescriptor = PADeployment.getProactiveDescriptor("file:" + descriptorLocation);

    }

    @Test
    public void deploymentDescriptorParse() throws Exception {
        URL descriptorLocation = getClass().getResource("wrong_namespace.xml");

        boolean gotException = false;

        try {
            GCMDeploymentParserImpl parser = new GCMDeploymentParserImpl(descriptorLocation, null);
        } catch (SAXException e) {
            gotException = e.getException().getMessage().contains("old format");
        }

        Assert.assertTrue(gotException);

    }

    //    @Test
    public void applicationDescriptorParse() throws Exception {

        URL descriptorLocation = getClass().getResource("application_ProActive_MS_basic.xml");

        System.out.println("parsing " + descriptorLocation);
        GCMApplicationParserImpl parser = new GCMApplicationParserImpl(descriptorLocation, null);

        parser.getCommandBuilder();
        parser.getVirtualNodes();
        parser.getNodeProviders();

    }

}
