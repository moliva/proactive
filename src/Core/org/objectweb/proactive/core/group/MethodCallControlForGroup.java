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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.MethodCallExecutionFailedException;
import org.objectweb.proactive.core.mop.MethodCallInfo;


/**
 * @author The ProActive Team
 */
public abstract class MethodCallControlForGroup extends MethodCall {

    transient Method reifiedMethod = null;

    public MethodCallControlForGroup() {
    }

    public void mockReifiedMethod() {
    }

    @Override
    public Method getReifiedMethod() {
        if (reifiedMethod == null) {
            // create a mock reified method
            try {
                reifiedMethod = getClass().getMethod("mockReifiedMethod", new Class[] {});
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {

            }
        }
        return reifiedMethod;
    }

    /**
     * Returns the number of parmeters (0 for most of method call for group)
     * @return 0
     * @see org.objectweb.proactive.core.mop.MethodCall#getNumberOfParameter()
     */
    @Override
    public int getNumberOfParameter() {
        return 0;
    }

    //
    // --- PRIVATE METHODS FOR SERIALIZATION --------------------------------------------------------------
    //
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        this.writeTheObject(out);
    }

    @Override
    protected void writeTheObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.readTheObject(in);
    }

    @Override
    protected void readTheObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    // Overloaded to avoid this MethodCallControlForGroup object
    // go inside the recycling pool of MethodCall.
    @Override
    protected void finalize() {
    }

    // return null
    @Override
    public Object execute(Object targetObject) throws InvocationTargetException,
            MethodCallExecutionFailedException {
        return null;
    }

    @Override
    public MethodCallInfo getMethodCallInfo() {
        return new MethodCallInfo(MethodCallInfo.CallType.OneWay,
            MethodCallInfo.SynchronousReason.NotApplicable, null);
    }

    /**
     * ControlCall for group never are asynchronous
     * @return <code>false</code>
     * @see org.objectweb.proactive.core.mop.MethodCall#isAsynchronousWayCall()
     */
    @Override
    public boolean isAsynchronousWayCall() {
        return false;
    }

    /**
     * ControlCall for group always are oneway
     * @return <code>true</code>
     * @see org.objectweb.proactive.core.mop.MethodCall#isOneWayCall()
     */
    @Override
    public boolean isOneWayCall() {
        return true;
    }

    /**
     * This method does nothing, because control messages are not subject to be bloqued by barriers
     * @param barrierTags unsed parameter
     */
    @Override
    public void setBarrierTags(LinkedList<String> barrierTags) {
    }

    /**
     * Control messages are never tagged
     * @return null
     */
    @Override
    public LinkedList<String> getBarrierTags() {
        return null;
    }
}
