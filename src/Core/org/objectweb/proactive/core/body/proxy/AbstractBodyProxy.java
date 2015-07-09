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
package org.objectweb.proactive.core.body.proxy;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.exceptions.FutureCreationException;
import org.objectweb.proactive.core.body.exceptions.SendRequestCommunicationException;
import org.objectweb.proactive.core.body.future.Future;
import org.objectweb.proactive.core.body.future.FutureProxy;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.mop.*;
import org.objectweb.proactive.core.security.exceptions.CommunicationForbiddenException;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public abstract class AbstractBodyProxy extends AbstractProxy implements BodyProxy, java.io.Serializable {
    //
    // -- STATIC MEMBERS -----------------------------------------------
    //
    private static Logger syncCallLogger = ProActiveLogger.getLogger(Loggers.SYNC_CALL);

    //
    // -- PROTECTED MEMBERS -----------------------------------------------
    //
    protected Integer cachedHashCode = null;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public AbstractBodyProxy() {
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //

    //
    // -- implements Proxy -----------------------------------------------
    //

    /**
     * Performs operations on the Call object created by the stub, thus changing the semantics of
     * message-passing to asynchronous message-passing with future objects
     * 
     * 
     * The semantics of message-passing implemented by this proxy class may be definied as follows :
     * <UL>
     * <LI>Asynchronous message-passing
     * <LI>Creation of future objects where possible (which leads to wait-by-necessity).
     * <LI>Synchronous, blocking calls where futures are not available.
     * <LI>The Call <code>methodCall</code> is passed to the skeleton for execution.
     * </UL>
     */
    public Object reify(MethodCall methodCall) throws Throwable {
        Object cachedMethodResult = checkOptimizedMethod(methodCall);
        if (cachedMethodResult != null) {
            return cachedMethodResult;
        }

        return invokeOnBody(methodCall);
    }

    /*
     * HACK: toString() can be implicitly called by log4j, which may result in a deadlock if we call
     * log4j inside log4j, so for now, we disable the message for toString().
     */
    private static boolean isToString(MethodCall methodCall) {
        return (methodCall.getNumberOfParameter() == 0) && "toString".equals(methodCall.getName());
    }

    private static boolean isHashCode(MethodCall methodCall) {
        return (methodCall.getNumberOfParameter() == 0) && "hashCode".equals(methodCall.getName());
    }

    private static Set<String> loggedSyncCalls = Collections.synchronizedSet(new HashSet<String>());

    private Object invokeOnBody(MethodCall methodCall) throws Exception, RenegotiateSessionException,
            Throwable {
        // Now gives the MethodCall object to the body

        MethodCallInfo mci = methodCall.getMethodCallInfo();

        try {

            if (mci.getType() == MethodCallInfo.CallType.OneWay) {
                reifyAsOneWay(methodCall);
                return null;
            }

            if (mci.getType() == MethodCallInfo.CallType.Asynchronous) {
                return reifyAsAsynchronous(methodCall);
            }

            if (!isToString(methodCall) && !isHashCode(methodCall) &&
                syncCallLogger.isEnabledFor(Level.DEBUG)) {
                String msg = "[DEBUG: synchronous call] All calls to the method below are synchronous " +
                    "(not an error, but may lead to performance issues or deadlocks):" +
                    System.getProperty("line.separator") + methodCall.getReifiedMethod() +
                    System.getProperty("line.separator") + "They are synchronous for the following reason: " +
                    mci.getMessage();

                if (loggedSyncCalls.add(msg)) {
                    syncCallLogger.debug(msg);
                }
            }

            return reifyAsSynchronous(methodCall);
        } catch (MethodCallExecutionFailedException e) {
            throw new ProActiveRuntimeException(e.getMessage(), e.getTargetException());
        }
    }

    // optimization may be a local execution or a caching mechanism
    // returns null if not applicable
    private Object checkOptimizedMethod(MethodCall methodCall) throws Exception, RenegotiateSessionException,
            Throwable {
        if (methodCall.getName().equals("equals") && (methodCall.getNumberOfParameter() == 1)) {
            Object arg = methodCall.getParameter(0);
            if (MOP.isReifiedObject(arg)) {
                Proxy proxy = ((StubObject) arg).getProxy();
                if (proxy instanceof AbstractBodyProxy) {
                    return Boolean.valueOf(getBodyID().equals(((AbstractBodyProxy) proxy).getBodyID()));
                }
            }

            return Boolean.valueOf(false);
        }

        if (methodCall.getName().equals("hashCode") && (methodCall.getNumberOfParameter() == 0)) {
            if (cachedHashCode == null) {
                return cachedHashCode = (Integer) invokeOnBody(methodCall);
            } else {
                return cachedHashCode;
            }
        }

        return null;
    }

    /**
     * 
     */
    protected void reifyAsOneWay(MethodCall methodCall) throws Exception, RenegotiateSessionException {
        sendRequest(methodCall, null);
    }

    /*
     * Dummy Future used to reply to a one-way method call with exceptions Declared as public to
     * accomodate the MOP
     */
    public static class VoidFuture {
        public VoidFuture() {
        }
    }

    protected Object reifyAsAsynchronous(MethodCall methodCall) throws Exception, RenegotiateSessionException {
        StubObject futureobject = null;

        // Creates a stub + FutureProxy for representing the result
        String sourceBodyUrl = "[unknown]";
        try {
            sourceBodyUrl = LocalBodyStore.getInstance().getContext().getBody().getUrl();
        } catch (Exception e) {
        }
        try {

            Class<?> returnType = null;
            Type t = methodCall.getReifiedMethod().getGenericReturnType();
            if (t instanceof TypeVariable) {
                returnType = methodCall.getGenericTypesMapping().get(t);
            } else {
                returnType = methodCall.getReifiedMethod().getReturnType();
            }

            if (returnType.equals(java.lang.Void.TYPE)) {
                /* A future for a void call is used to put the potential exception inside */
                futureobject = (StubObject) MOP.newInstance(VoidFuture.class, null,
                        Constants.DEFAULT_FUTURE_PROXY_CLASS_NAME, null);
            } else {
                futureobject = (StubObject) MOP.newInstance(returnType, null,
                        Constants.DEFAULT_FUTURE_PROXY_CLASS_NAME, null);
            }
        } catch (MOPException e) {
            throw new FutureCreationException("Exception occured in reifyAsAsynchronous from " +
                sourceBodyUrl + " while creating future for methodcall = " + methodCall.getName(), e);
        } catch (ClassNotFoundException e) {
            throw new FutureCreationException("Exception occured in reifyAsAsynchronous from " +
                sourceBodyUrl + " while creating future for methodcall = " + methodCall.getName(), e);
        }

        // Set the id of the body creator in the created future
        FutureProxy fp = (FutureProxy) (futureobject.getProxy());
        fp.setCreatorID(this.getBodyID());
        fp.setUpdater(this.getBody());
        fp.setOriginatingProxy(this);

        try {
            sendRequest(methodCall, fp);
        } catch (java.io.IOException e) {
            throw new SendRequestCommunicationException(
                "Exception occured in reifyAsAsynchronous while sending request for methodcall = " +
                    methodCall.getName() + " from " + sourceBodyUrl + " to " + this.getBodyID(), e);
        }

        // And return the future object
        return futureobject;
    }

    protected Object reifyAsSynchronous(MethodCall methodCall) throws Throwable, Exception,
            RenegotiateSessionException {
        // Setting methodCall.res to null means that we do not use the future mechanism
        FutureProxy fp = FutureProxy.getFutureProxy();
        fp.setCreatorID(this.getBodyID());
        fp.setUpdater(this.getBody());

        String sourceBodyUrl = "[unknown]";
        try {
            sourceBodyUrl = LocalBodyStore.getInstance().getContext().getBody().getUrl();
        } catch (Exception e) {
        }

        try {
            sendRequest(methodCall, fp);
        } catch (java.io.IOException e) {
            throw new SendRequestCommunicationException(
                "Exception occured in reifyAsSynchronous while sending request for methodcall = " +
                    methodCall.getName() + " from " + sourceBodyUrl + " to " + this.getBodyID(), e);
        }

        // PROACTIVE_1180
        // explicit waitfor with PA_FUTURE_TIMEOUT to leave a chance
        // to have synchronous method calls
        // useful when the call can block for ever and is made by a threadpool.
        // 
        fp.waitFor(CentralPAPropertyRepository.PA_FUTURE_SYNCHREQUEST_TIMEOUT.getValue());

        // Returns the result or throws the exception
        if (fp.getRaisedException() != null) {
            throw fp.getRaisedException();
        } else {
            return fp.getResult();
        }
    }

    protected abstract void sendRequest(MethodCall methodCall, Future future) throws java.io.IOException,
            RenegotiateSessionException, CommunicationForbiddenException;

    protected abstract void sendRequest(MethodCall methodCall, Future future, Body sourceBody)
            throws java.io.IOException, RenegotiateSessionException, CommunicationForbiddenException;
}
