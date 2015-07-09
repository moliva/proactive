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
 * Annotation for specifying the dispatch strategy of a given parameter. You
 * could use it directly in the method's parameter declaration or inside
 * {@link MethodDispatchMetadata} and {@link ClassDispatchMetadata} annotations.
 * <br>
 * Examples:
 * <br>
 * Dispatch the <code>args</code> parameter using the ROUND_ROBIN mode:
 * <pre>
 * void compute(&#064;ParamDispatchMetadata(mode = ParamDispatchMode.ROUND_ROBIN) List<String> args, String other);
 *</pre>
 *<br>
 * Dispatch all the parameter in the class' declared methods with the ONE_TO_ONE mode:
 * <pre>
 * &#064;ClassDispatchMetadata(mode = &#064;ParamDispatchMetadata(mode = ParamDispatchMode.ONE_TO_ONE))
 * public interface SlaveMulticast {
 *     ...
 * }
 *</pre>
 *
 * @see org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMode
 *
 * @author The ProActive Team
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@PublicAPI
public @interface ParamDispatchMetadata {
    /**
     * Selection of dispatch strategy from the {@link ParamDispatchMode} enumeration.
     * @return selected dispatch strategy
     */
    ParamDispatchMode mode();

    /**
     * Used for specifying a custom dispatch strategy. Custom dispatch strategies are defined in classes that
     * implement the {@link ParamDispatch} interface.
     * <br>
     * For a custom dispatch strategy to be specified, the ParamDispatchMode.CUSTOM value must be selected for
     * the {@link ParamDispatchMetadata#mode()} method.
     * @return a class defining the dispatch strategy
     */
    Class<?> customMode() default ParamDispatchMode.class;
}
