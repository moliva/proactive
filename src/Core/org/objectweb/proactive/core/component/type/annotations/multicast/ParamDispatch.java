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
package org.objectweb.proactive.core.component.type.annotations.multicast;

import java.lang.reflect.Type;
import java.util.List;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;


/**
 * This interface declares methods for defining the partitioning of parameters in multicast interfaces.
 *
 * @author The ProActive Team
 *
 */
@PublicAPI
public interface ParamDispatch {

    /**
     * Transforms an input parameter passed to a multicast interface into input parameters for connected server interfaces
     * @param inputParameter input parameter as given to a multicast interface
     * @param nbOutputReceivers number of server interfaces connected to the multicast interface
     * @return a map of parameters to be distributed
     * @throws ParameterDispatchException if parameter dispatch fails
     */
    public List<Object> partition(Object inputParameter, int nbOutputReceivers)
            throws ParameterDispatchException;

    /**
     * Computes the number of method invocations that will be generated with the selection distribution algorithm.
     * @param inputParameter input parameter as given to a multicast interface
     * @param nbOutputReceivers number of server interfaces connected to the multicast interface
     * @return the number of method invocations to expect from the specified distribution
     * @throws ParameterDispatchException if parameter dispatch computation fails
     */
    public int expectedDispatchSize(Object inputParameter, int nbOutputReceivers)
            throws ParameterDispatchException;

    /**
     * Verifies that, for the specified distribution mode, the types of parameters are compatible between client side and server side.
     * @param clientSideInputParameter type of parameter from the client side
     * @param serverSideInputParameter type of parameter from the server side
     * @return true if the types are compatible, false otherwise
     * @throws ParameterDispatchException if verification fails
     */
    public boolean match(Type clientSideInputParameter, Type serverSideInputParameter)
            throws ParameterDispatchException;
}
