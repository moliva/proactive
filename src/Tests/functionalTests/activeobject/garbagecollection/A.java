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
package functionalTests.activeobject.garbagecollection;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;


public class A implements InitActive {
    private Collection<A> references;
    public static final Collection<WeakReference<A>> weak = new LinkedList<WeakReference<A>>();

    public A() {
    }

    public void initActivity(Body body) {
        // this is not a stub
        A.weak.add(new WeakReference<A>(this));
        references = new Vector<A>();
    }

    public void addRef(A ref) {
        this.references.add(ref);
    }

    public static int countCollected() {
        int count = 0;
        for (WeakReference<A> wr : A.weak) {
            if (wr.get() == null) {
                count++;
            }
        }
        return count;
    }
}
