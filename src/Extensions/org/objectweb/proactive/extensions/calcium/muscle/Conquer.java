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
package org.objectweb.proactive.extensions.calcium.muscle;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extensions.calcium.system.SkeletonSystem;


/**
 * This interface is used to reduce a list of parameters
 * into a single one. It is useful for {@link org.objectweb.proactive.extensions.calcium.skeletons.Skeleton}s
 * such as: {@link org.objectweb.proactive.extensions.calcium.skeletons.DaC}), {@link org.objectweb.proactive.extensions.calcium.skeletons.Map}, and {@link org.objectweb.proactive.extensions.calcium.skeletons.Fork}.
 *
 * @author The ProActive Team
 *
 */
@PublicAPI
public interface Conquer<Y, R> extends Muscle<Y[], R> {
    public R conquer(Y[] param, SkeletonSystem system) throws Exception;
}
