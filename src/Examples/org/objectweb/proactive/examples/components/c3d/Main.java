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
package org.objectweb.proactive.examples.components.c3d;

import org.objectweb.proactive.core.component.adl.Launcher;


/**
 * This example is a C3D Component version.
 */
public class Main {
    private static final String DEFAULT_ADL = Main.class.getPackage().getName() + ".adl.userAndComposite";

    public static void main(final String[] args) throws Exception {
        if ((args.length != 2) && (args.length != 1)) {
            System.out.println("Parameters : descriptor_file [fractal_ADL_file] " +
                "\n        The first file describes your deployment of computing nodes." +
                "\n                You may want to try ../../descriptors/components/C3D_all.xml" +
                "\n        The second file describes your components layout. " +
                "\n                Default is " + DEFAULT_ADL);

            return;
        }

        String adl;
        String descriptor;

        if (args.length == 1) {
            adl = DEFAULT_ADL;
            descriptor = args[0];
        } else {
            descriptor = args[0];
            adl = args[1];
        }

        Launcher.main(new String[] { "-fractal", adl, "m", descriptor });
    }
}
