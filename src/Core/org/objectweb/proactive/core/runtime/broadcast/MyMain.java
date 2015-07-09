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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.runtime.broadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.objectweb.proactive.api.PARuntime;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;


public class MyMain {
    private static RTBroadcaster rtBrodcaster;

    public static void main(String[] args) throws IOException, BroadcastDisabledException {
        System.out.println("runtime : " + ProActiveRuntimeImpl.getProActiveRuntime().getURL());

        //--Running the listening thread
        Thread t = new Thread(rtBrodcaster = RTBroadcaster.getInstance());

        //-- CLI
        String line = "";
        while (!line.equals("0")) {
            System.out.println("\n===============================\n");
            System.out.println("0. Exit");
            System.out.println("1. Broadcast our location ");
            System.out.println("2. Discover other runtimes");
            System.out.println("3. apas");
            System.out.println("===============================");

            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            line = br.readLine();

            if (line.equals("1")) {
                rtBrodcaster.sendCreation();
            } else if (line.equals("2")) {
                rtBrodcaster.sendDiscover();
            } else if (line.equals("3")) {
                System.out.println(Arrays.toString(PARuntime.findRuntimes().toArray()));
            }

            else if (line.equals("0")) {
                RTBroadcaster.getInstance().kill();
                System.exit(0);
            }
            //            try {
            //                Thread.sleep(1000);
            //            } catch (InterruptedException e) {
            //                // TODO Auto-generated catch block
            //                e.printStackTrace();
            //            }
        }
        System.out.println("Bye");
    }
}
