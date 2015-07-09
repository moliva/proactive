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
package org.objectweb.proactive.examples.robustarith;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;


/**
 * @author The ProActive Team
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Sum implements Serializable {
    private SubSum[] sums;

    public Sum() {
    }

    public Ratio eval(Formula formula, int begin, int end) throws OverflowException {
        Ratio[] ratios = new Ratio[sums.length];
        int d = (end - begin) / ratios.length;

        for (int i = 0; i < (ratios.length - 1); i++) {
            ratios[i] = sums[i].eval(formula, begin, (begin + d) - 1);
            begin += d;
        }

        ratios[ratios.length - 1] = sums[ratios.length - 1].eval(formula, begin, end);

        Ratio r = new Ratio(BigInteger.ZERO, BigInteger.ONE);
        for (int i = 0; i < ratios.length; i++) {
            r.add(ratios[i]);
        }

        return r;
    }

    public Sum(List<Node> nodes) throws ActiveObjectCreationException, NodeException {
        sums = new SubSum[nodes.size()];
        int i = 0;
        for (Node node : nodes) {
            sums[i] = (SubSum) PAActiveObject.newActive(SubSum.class.getName(),
                    new Object[] { "SubSum" + i }, node);
            ++i;
        }
    }
}
