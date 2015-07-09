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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.objectweb.proactive.annotation.PublicAPI;


/**
 * Annotation used for specifying the dispatch mode for all parameters of all
 * methods of the annotated interface.
 * <br>
 * Example:
 * <br>
 * All parameters of the declared method are dispatched using the ONE_TO_ONE mode:
 * <pre>
 * &#064;ClassDispatchMetadata(mode = &#064;ParamDispatchMetadata(mode = ParamDispatchMode.ONE_TO_ONE))
 * public interface MyItf {
 *    void compute(List&lt;String&gt; args, String other);
 *    List&lt;String&gt; computeSync(List&lt;String&gt; args, String other);
 *    ...
 * }
 * </pre>
 *
 * @see org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMode
 * @see org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMetadata
 *
 * @author The ProActive Team
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@PublicAPI
public @interface ClassDispatchMetadata {
    /**
     * Specifies the dispatch mode
     * @return the dispatch mode of all the parameters
     */
    ParamDispatchMetadata mode();
}
