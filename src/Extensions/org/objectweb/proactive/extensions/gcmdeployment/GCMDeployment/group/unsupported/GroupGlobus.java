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
package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group.unsupported;

import java.util.List;

import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilder;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group.AbstractGroup;


public class GroupGlobus extends AbstractGroup {
    private String count;
    private String queue;
    private String maxTime;
    private String stderr;
    private String stdout;
    private String stdin;
    private String directory;
    private String hostname;

    @Override
    public List<String> internalBuildCommands(CommandBuilder commandBuilder) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns the count.
     * @return String
     */
    public String getCount() {
        return count;
    }

    /**
     * Sets the count.
     * @param count The count to set
     */
    public void setCount(String count) {
        this.count = count;
    }

    /**
     * @return Returns the queue.
     */
    public String getQueue() {
        return queue;
    }

    /**
     * @param queue The queue to set.
     */
    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * @return Returns the stderr.
     */
    public String getStderr() {
        return stderr;
    }

    /**
     * @param stderr The stderr to set.
     */
    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    /**
     * @return Returns the stdout.
     */
    public String getStdout() {
        return stdout;
    }

    /**
     * @param stdout The stdout to set.
     */
    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public void setStdin(String stdin) {
        this.stdin = stdin;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
