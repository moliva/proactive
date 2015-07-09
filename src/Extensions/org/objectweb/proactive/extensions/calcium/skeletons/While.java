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
package org.objectweb.proactive.extensions.calcium.skeletons;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extensions.calcium.muscle.Condition;
import org.objectweb.proactive.extensions.calcium.muscle.Execute;


/**
 * The <code>While</code> {@link Skeleton} represents conditioned iteration.
 * The nested {@link Skeleton} will be executed while the {@link Condition} holds <code>true</code>.
 *
 * @author The ProActive Team
 *
 */
@PublicAPI
public class While<P extends java.io.Serializable> implements Skeleton<P, P> {
    Condition<P> cond;
    Skeleton<P, P> child;

    /**
     * The main constructor.
     *
     * @param cond The {@link Condition} that will be evaluated on each iteration.
     * @param nested The nested skeleton that will be invoked.
     */
    public While(Condition<P> cond, Skeleton<P, P> nested) {
        this.cond = cond;
        this.child = nested;
    }

    /**
     * Like the main constructor, but accepts {@link Execute} objects.
     *
     * The {@link Execute} object will be wrapped in a {@link Seq} {@link Skeleton}.
     *
     * @param cond The {@link Condition} that will be evaluated on each iteration.
     * @param muscle The {@link Execute} that will be invoked on each iteration.
     */
    public While(Condition<P> cond, Execute<P, P> muscle) {
        this.cond = cond;
        this.child = new Seq<P, P>(muscle);
    }

    /**
     * @see Skeleton#accept(SkeletonVisitor)
     */
    public void accept(SkeletonVisitor visitor) {
        visitor.visit(this);
    }
}
