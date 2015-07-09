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
package functionalTests.component.collectiveitf.reduction.primitive;

import java.util.List;

import org.objectweb.proactive.core.component.exceptions.ReductionException;
import org.objectweb.proactive.core.component.type.annotations.multicast.ReduceBehavior;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;


public class SumReduction implements ReduceBehavior {
    public Object reduce(List<?> values) throws ReductionException {
        int sum = 0;
        if (values.isEmpty()) {
            throw new ReductionException("no values to perform reduction on");
        }
        System.out.println("--------------");
        System.out.println("Addition of " + values.size() + " elements");

        for (Object value : values) {
            if (!(value instanceof IntWrapper)) {
                throw new ReductionException("wrong type: expected " + IntWrapper.class.getName() +
                    " but received " + value.getClass().getName());
            }
            IntWrapper intWrapperValue = (IntWrapper) value;
            sum += intWrapperValue.getIntValue();
        }
        System.out.println("--------------");
        return new IntWrapper(sum);
    }
}
