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
package org.objectweb.proactive.examples.components;

import org.objectweb.proactive.core.component.adl.Launcher;


/** This is a wrapper to start component applications from their ADL description+deployment descr. */
public class StartFromADL {
    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.out
                    .println("Parameters : descriptor_file fractal_ADL_file "
                        + "\n        The first file describes your deployment of computing nodes."
                        + "\n                You may want to try ../../../descriptors/components/C3D_all.xml"
                        + "\n        The second file describes your components layout. "
                        + "\n                Try org.objectweb.proactive.examples.components.c3d.adl.userAndComposite");
        } else {
            String descriptor = args[0];
            String adl = args[1];
            Launcher.main(new String[] { "-fractal", adl, "m", descriptor });
        }
    }
}
