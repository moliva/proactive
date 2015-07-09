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
package org.objectweb.proactive.examples.pi;

import java.util.List;

import org.objectweb.proactive.core.component.type.annotations.multicast.ClassDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMode;


/**
 * This interface represents the client multicast interface of the master component in the component version of the application.
 * The dispatch mode is one to one. That means that the parameters of the methods are scattered. If a parameter is a list, each item of the list sent to one component the interface is bound to.
 * It also means that the number of elements in the list and the number of components the interface is bound to have to be the same.
 * @author The ProActive Team
 *
 */
@ClassDispatchMetadata(mode = @ParamDispatchMetadata(mode = ParamDispatchMode.ONE_TO_ONE))
public interface PiCompMultiCast {

    /**
     * Initiates the computation of pi on all the workers bound to the client multicast interface
     * @param msg the list of intervals that have to be distributed to the workers
     * @return The list of partial results of pi computation that have to be gathered into the final pi value
     */
    public List<Result> compute(List<Interval> msg);

    /**
     * Sets scale for several pi computers
     * @param scale The scale to set
     */
    public void setScale(List<Integer> scale);
}
