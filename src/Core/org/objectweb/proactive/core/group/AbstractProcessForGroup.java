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
package org.objectweb.proactive.core.group;

import java.util.Vector;

import org.objectweb.proactive.core.body.future.FutureProxy;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.Proxy;
import org.objectweb.proactive.core.mop.StubObject;


public abstract class AbstractProcessForGroup implements Runnable {

    protected static Proxy findLastProxy(Object obj) {
        if (!MOP.isReifiedObject(obj)) {
            return null;
        }
        Proxy proxy = ((StubObject) obj).getProxy();
        while (proxy instanceof FutureProxy) {
            if (MOP.isReifiedObject(((FutureProxy) proxy).getResult())) {
                return AbstractProcessForGroup.findLastProxy(((FutureProxy) proxy).getResult());
            } else {
                return proxy;
            }
        }
        return proxy;
    }

    protected Vector<?> memberList;
    protected ProxyForGroup proxyGroup;

    protected int groupIndex; // may change if dispatch is dynamic
    protected int resultIndex; // corresponds to index of results; does not change
    protected Vector<Object> memberListOfResultGroup = null;
    protected boolean dynamicallyDispatchable = false;

    public int getMemberListSize() {
        if (memberList != null) {
            return memberList.size();
        } else {
            return 0;
        }
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int index) {
        this.groupIndex = index;
    }

    public Vector<Object> getResultGroup() {
        return memberListOfResultGroup;
    }

    public boolean isDynamicallyDispatchable() {
        return dynamicallyDispatchable;
    }

    public void setDynamicallyDispatchable(boolean dynamicallyDispatchable) {
        this.dynamicallyDispatchable = dynamicallyDispatchable;
    }
}
