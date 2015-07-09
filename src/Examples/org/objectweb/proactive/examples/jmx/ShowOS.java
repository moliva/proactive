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
package org.objectweb.proactive.examples.jmx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.jmx.client.ClientConnector;
import org.objectweb.proactive.core.util.URIBuilder;


/**
 * This example connects remotely a MBean Server and shows informations
 * about the operating system (such as the OS name, the OS version, ...)
 * @author The ProActive Team
 */
public class ShowOS {
    private ClientConnector cc;
    private ProActiveConnection connection;
    private String url;
    private static final String default_url = "//localhost/serverName";

    public ShowOS() throws Exception {
        System.out.println("Enter the url of the JMX MBean Server : [default is '" + default_url + "']");
        this.url = read();
        try {
            connect();
            infos();
        } catch (IOException e) {
            System.out.println("Cannot contact the connector, did you start one ? (see connector.[sh|bat])");
        }
        System.out.println("Good Bye!");
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        new ShowOS();
    }

    private String read() {
        String what = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            what = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return what;
    }

    private void connect() throws IOException {
        if ("".equals(url.trim())) {
            url = default_url;
        }

        String serverName = URIBuilder.getNameFromURI(url);

        System.out.println("Connecting to [" + serverName + "]: " + this.url);

        this.cc = new ClientConnector(this.url, serverName);
        this.cc.connect();
        this.connection = cc.getConnection();

    }

    private void infos() throws Exception {
        ObjectName name = new ObjectName("java.lang:type=OperatingSystem");
        String attribute = "";
        System.out.println("Attributes :");
        help(name);
        while (true) {
            System.out.println("Enter the name of the attribute :");
            attribute = read();
            if (attribute.equals("help")) {
                help(name);
            } else if (attribute.equals("quit")) {
                return;
            } else {
                try {
                    Object att = this.connection.getAttribute(name, attribute);
                    System.out.println("==> " + att);
                } catch (Exception e) {
                    if (e.getCause().getClass().equals(AttributeNotFoundException.class)) {
                        System.out.println("Unknown attribute name: '" + attribute + "' is not a valid name");
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    private void help(ObjectName name) throws Exception {
        System.out.println("List of attributes :");
        MBeanAttributeInfo[] atts = this.connection.getMBeanInfo(name).getAttributes();
        for (int i = 0, size = atts.length; i < size; i++)
            System.out.println("> " + atts[i].getName());
        System.out.println("> help");
        System.out.println("> quit");
    }
}
