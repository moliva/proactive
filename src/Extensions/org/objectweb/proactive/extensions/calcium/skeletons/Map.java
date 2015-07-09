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

import java.io.Serializable;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extensions.calcium.muscle.Conquer;
import org.objectweb.proactive.extensions.calcium.muscle.Divide;
import org.objectweb.proactive.extensions.calcium.muscle.Execute;


/**
 * Map represents data parallelism SIMD (Single Instruction Multiple Data).
 *
 * A parameter is {@link Divide}d into several parameters, and the same
 * nested {@link Skeleton} is executed on each parameter.
 * (As oposed to {@link Fork} where each parameter is executed on a different nested {@link Skeleton}).
 *
 *  @author The ProActive Team
 */
@PublicAPI
public class Map<P extends java.io.Serializable, R extends java.io.Serializable> implements Skeleton<P, R> {
    Divide<P, ?> div;
    Skeleton<?, ?> child;
    Conquer<?, R> conq;

    /**
     * The main constructor.
     *
     * @param div The custom {@link Divide} behavior.
     * @param nested The nested {@link Skeleton} to execute on each class.
     * @param conq The custom {@link Conquer} behavior.
     */
    public <X extends Serializable, Y extends Serializable> Map(Divide<P, X> div, Skeleton<X, Y> nested,
            Conquer<Y, R> conq) {
        this.div = div;
        this.child = nested;
        this.conq = conq;
    }

    /**
     * Like the main constructor, but accepts an {@link Execute} object instead of a {@link Seq}.
     *
     * @param div The custom {@link Divide} behavior.
     * @param muscle The muscle function that will be nested in a {@link Seq} {@link Skeleton}.
     * @param conq The custom {@link Conquer} behavior.
     */
    public <X extends Serializable, Y extends Serializable> Map(Divide<P, X> div, Execute<X, Y> muscle,
            Conquer<Y, R> conq) {
        this.div = div;
        this.child = new Seq<X, Y>(muscle);
        this.conq = conq;
    }

    /**
     * @see Skeleton#accept(SkeletonVisitor)
     */
    public void accept(SkeletonVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Map";
    }
}
