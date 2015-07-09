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
package org.objectweb.proactive.extensions.gcmdeployment;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class Helpers {

    /**
     * Checks that descriptor exist, is a file and is readable
     * 
     * @param descriptor
     *            The File to be checked
     * @throws IllegalArgumentException
     *             If the File is does not exist, is not a file or is not readable
     */
    public static URLConnection openConnectionTo(URL descriptor) throws IllegalArgumentException {
        URLConnection conn;
        try {
            conn = descriptor.openConnection();
        } catch (IOException e) {
            throw new IllegalArgumentException("Connection to " + descriptor.toExternalForm() +
                " could not be established.", e);
        }

        return conn;
    }

    public static URL fileToURL(File file) {
        if (file == null)
            return null;

        URL answer = null;
        try {
            answer = file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return answer;
    }

    static public String escapeCommand(String command) {
        // At each step, the command must be protected with " or ' and the command
        // passed as parameter must be escaped. This can be quite difficult since
        // Runtime.getRuntime().exec() only take an array of String as parameter...    	
        String res = command.replaceAll("'", "'\\\\''");
        return "'" + res + "'";
    }

    static public String escapeWindowsCommand(String command) {
        String res = command.replaceAll("\"", "\\\"");
        return res;
    }

}
