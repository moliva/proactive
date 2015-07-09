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
package org.objectweb.proactive.examples.masterworker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.commons.cli.HelpFormatter;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extensions.masterworker.ProActiveMaster;
import org.objectweb.proactive.extensions.masterworker.TaskException;
import org.objectweb.proactive.extensions.masterworker.tasks.NativeTask;


/**
 * This simple test class is an example on how to launch Native commands using the Master/Worker API
 * The program launches the command "hostname" on a set of remote machines and display the results.
 * @author The ProActive Team
 *
 */
public class NativeExample extends AbstractExample {
    static ProActiveMaster<SimpleNativeTask, ArrayList<String>> master;

    /**
     * @param args
     * @throws ProActiveException 
     * @throws LoginException 
     */
    public static void main(String[] args) throws Exception {
        //   Getting command line parameters and creating the master (see AbstractExample)
        init(args);

        if (master_vn_name == null) {
            master = new ProActiveMaster<SimpleNativeTask, ArrayList<String>>();
        } else {
            master = new ProActiveMaster<SimpleNativeTask, ArrayList<String>>(descriptor_url, master_vn_name);
        }

        registerShutdownHook(new Runnable() {
            public void run() {
                master.terminate(true);
            }
        });

        // Adding ressources
        if (schedulerURL != null) {
            master.addResources(schedulerURL, login, password, classpath);
        } else if (vn_name == null) {
            master.addResources(descriptor_url);
        } else {
            master.addResources(descriptor_url, vn_name);
        }

        // Creating the tasks to be solved
        List<SimpleNativeTask> tasks = new ArrayList<SimpleNativeTask>();
        for (int i = 0; i < 20; i++) {
            tasks.add(new SimpleNativeTask("hostname"));
        }

        // Submitting the tasks
        master.solve(tasks);
        Collection<ArrayList<String>> results = null;

        // Collecting the results
        try {
            results = master.waitAllResults();
        } catch (TaskException e) {
            // We catch user exceptions
            e.printStackTrace();
        }
        for (ArrayList<String> result : results) {
            for (String line : result) {
                System.out.println(line);
            }
        }

        PALifeCycle.exitSuccess();
    }

    /**
     * A task executing a native command
     * @author The ProActive Team
     *
     */
    public static class SimpleNativeTask extends NativeTask {

        /**
         *
         */
        public SimpleNativeTask(String command) {
            super(command);
        }
    }

    protected static void init(String[] args) throws Exception {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("NativeExample", command_options);
        AbstractExample.init(args);
    }
}
