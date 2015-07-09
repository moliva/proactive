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
package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication;

import static org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers.GCMA_LOGGER;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilder;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentDescriptor;


public class NodeProvider {
    static Set<NodeProvider> nodeProviders = new HashSet<NodeProvider>();
    private String id;
    private Set<GCMDeploymentDescriptor> descriptors;
    private TechnicalServicesProperties technicalServicesProperties;

    static public Set<NodeProvider> getAllNodeProviders() {
        return nodeProviders;
    }

    public NodeProvider(String id) {
        this.id = id;
        this.descriptors = new HashSet<GCMDeploymentDescriptor>();
        this.technicalServicesProperties = new TechnicalServicesProperties();

        nodeProviders.add(this);
    }

    public void addGCMDeploymentDescriptor(GCMDeploymentDescriptor desc) {
        if (descriptors.contains(desc)) {
            GCMA_LOGGER.warn(desc.getDescriptorURL().toExternalForm() + " already the Node Provider named " +
                id);
        }
        descriptors.add(desc);
    }

    public void start(CommandBuilder commandBuilder, GCMApplicationInternal gcma) {
        for (GCMDeploymentDescriptor desc : descriptors) {
            desc.start(commandBuilder, gcma);
        }
    }

    public String getId() {
        return id;
    }

    protected Set<GCMDeploymentDescriptor> getDescriptors() {
        return descriptors;
    }

    public void setTechnicalServicesProperties(TechnicalServicesProperties providerTechnicalServices) {
        this.technicalServicesProperties = providerTechnicalServices;
    }

    public TechnicalServicesProperties getTechnicalServicesProperties() {
        return technicalServicesProperties;
    }

}
