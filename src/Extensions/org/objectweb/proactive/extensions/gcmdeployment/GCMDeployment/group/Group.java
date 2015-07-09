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
package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilder;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfo;


public interface Group extends Serializable {
    public String getId();

    /**
     * Set environment variables for this cluster
     * @param env environment variables
     */
    public void setEnvironment(Map<String, String> env);

    /**
     * Set the command path to override the default one
     * @param commandPath path to the command
     */
    public void setCommandPath(String commandPath);

    /**
     * Set the HostInfo
     *
     * @param hostInfo
     */
    public void setHostInfo(HostInfo hostInfo);

    /**
     * Get the HostInfo
     *
     * @return if set the HostInfo is returned. null is returned otherwise
     */
    public HostInfo getHostInfo();

    /**
     * Check that this bridge is in a consistent state and is ready to be
     * used.
     *
     * @throws IllegalStateException thrown if anything is wrong
     */
    public void check() throws IllegalStateException;

    /**
     * Build the command to start the group
     *
     * @param commandBuilder The final command builder
     * @return The command to be used to start this group
     */
    public List<String> buildCommands(CommandBuilder commandBuilder, GCMApplicationInternal gcma);
}
