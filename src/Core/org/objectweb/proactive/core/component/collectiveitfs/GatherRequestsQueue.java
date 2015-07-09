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
package org.objectweb.proactive.core.component.collectiveitfs;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.body.future.MethodCallResult;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.component.exceptions.GathercastTimeoutException;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.representative.ItfID;
import org.objectweb.proactive.core.component.request.ComponentRequest;
import org.objectweb.proactive.core.component.type.annotations.gathercast.MethodSynchro;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.SerializableMethod;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.utils.SafeTimerTask;


/**
 * <p>This class manages a queue of requests on a gather interface: a list of reified invocations from connected client interfaces.</p>
 *
 * <p>Asynchronicity is provided with a third-party active object and automatic continuations.</p>
 *
 * <p>A timeout exception is thrown if some of the connected client interfaces fail to send a request before the timeout; the countdown
 * is triggered right after serving the first invocation on the gathercast interface.</p>
 *
 * @author The ProActive Team
 *
 */
public class GatherRequestsQueue implements Serializable {
    private GatherFuturesHandler futuresHandler; // primitive pooling
    private List<Object> connectedClientItfs; // consistency?
    private Map<ItfID, ComponentRequest> requests;
    private String serverItfName;
    private SerializableMethod itfTypeInvokedMethod;
    private boolean waitForAll = true;
    transient long creationTime = System.currentTimeMillis(); // TODO do not reinitialize after deserialization
    public static final long DEFAULT_TIMEOUT = 1000000; // TODO use a proactive default property
    private Timer timeoutTimer = null;
    boolean timedout = false;
    boolean thrownTimeoutException = false;
    long timeout = DEFAULT_TIMEOUT;
    private static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_GATHERCAST);
    GatherFuturesHandlerPool gatherFuturesHandlerPool;
    boolean resultsReturned = false;
    boolean oneWayCall = true;

    public GatherRequestsQueue(PAComponent owner, String serverItfName, Method itfTypeMethod,
            List<Object> connectedClientItfs, GatherFuturesHandlerPool gatherFuturesHandlerPool) {
        //this.owner = owner;
        this.serverItfName = serverItfName;
        //        this.conditionChecker = gatherConditionChecker;
        //        this.invokedMethodSignature = methodSignature;
        itfTypeInvokedMethod = new SerializableMethod(itfTypeMethod);
        this.gatherFuturesHandlerPool = gatherFuturesHandlerPool;
        this.connectedClientItfs = connectedClientItfs;
        MethodSynchro sc = itfTypeInvokedMethod.getMethod().getAnnotation(MethodSynchro.class);
        if (sc != null) {
            this.waitForAll = sc.waitForAll();
        } else {
            this.waitForAll = true;
        }

        requests = new HashMap<ItfID, ComponentRequest>();

        // add first request
        //        requests.put(r.getMethodCall().getComponentMetadata().getSenderItfID(), r);
    }

    public boolean containsRequestFrom(ItfID clientItfID) {
        return requests.containsKey(clientItfID);
    }

    public synchronized Object put(ItfID clientItfID, ComponentRequest request) {
        if (isFull()) {
            throw new ProActiveRuntimeException("gather requests queue is full");
        }
        requests.put(clientItfID, request);

        // evaluate waitForAll
        if (!waitForAll) {
            // Non synchronized method, we should not expect other request
            connectedClientItfs = new ArrayList<Object>();
            connectedClientItfs.add(clientItfID);
        }

        //use a pool!
        if ((futuresHandler == null) && (!Void.TYPE.equals(itfTypeInvokedMethod.getMethod().getReturnType()))) {
            oneWayCall = false;
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("adding futures handler for requests on " + serverItfName + "." +
                        itfTypeInvokedMethod.getMethod().getName());
                }
                futuresHandler = GatherFuturesHandlerPool.instance().borrowFuturesHandler();
                futuresHandler.setConnectedClientItfs(connectedClientItfs);
            } catch (Exception e) {
                throw new ProActiveRuntimeException("cannot create futures handler for gather interface", e);
            }
        }

        if (!oneWayCall) {
            // evaluate timeout
            if (timeoutTimer == null) {
                timeoutTimer = new Timer();
                MethodSynchro sc = itfTypeInvokedMethod.getMethod().getAnnotation(MethodSynchro.class);
                if (waitForAll && (sc != null)) {
                    timeout = sc.timeout();
                } else {
                    timeout = DEFAULT_TIMEOUT;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("gather request queue timer starting with timeout = " + timeout);
                }
                timeoutTimer.schedule(new TimeoutTask(this), timeout);
            }

            if (isFull()) {
                timeoutTimer.cancel();
            }

            if ((System.currentTimeMillis() - creationTime) >= timeout) {
                // we need to check this for small timeouts because timer runs concurrently
                timedout = true;
                addFutureForGatheredRequest(null);
            }
            Object reply = futuresHandler.distribute(clientItfID);

            // return future result (will be computed when gather request is processed)
            return reply;
        } else {
            return null;
        }
    }

    public ComponentRequest get() {
        // return the first one
        if (requests.isEmpty()) {
            return null;
        }
        return requests.get(requests.keySet().iterator().next());
    }

    public ComponentRequest get(ItfID id) {
        return requests.get(id);
    }

    public boolean waitForAll() {
        return waitForAll;
    }

    public boolean isFull() {
        return (requests.size() == connectedClientItfs.size());
    }

    public int size() {
        return requests.size();
    }

    public Method getInvokedMethod() {
        // return the first one
        if (requests.isEmpty()) {
            return null;
        }
        return requests.get(requests.keySet().iterator().next()).getMethodCall().getReifiedMethod();
    }

    public boolean oneWayMethods() {
        if (requests.isEmpty()) {
            return false;
        }
        return requests.get(requests.keySet().iterator().next()).isOneWay();
    }

    public void addFutureForGatheredRequest(MethodCallResult futureResult) {
        if (timedout && !resultsReturned) {
            // avoids race condition with small timeouts (result is replaced with a timeout exception)
            if (!thrownTimeoutException) {
                if (logger.isDebugEnabled()) {
                    logger.debug("timeout reached at " + timeout + "for gather request on [" +
                        itfTypeInvokedMethod.getMethod().getName() + "]");
                }
                thrownTimeoutException = true;
                futuresHandler
                        .setFutureOfGatheredInvocation(new MethodCallResult(
                            null,
                            new GathercastTimeoutException(
                                "timeout of " +
                                    timeout +
                                    " reached before invocations from all clients were received for gather invocation (method " +
                                    itfTypeInvokedMethod.getMethod().toGenericString() +
                                    " on gather interface " + serverItfName)));
            }

            // else ignore
        } else {
            if (!resultsReturned) {
                // this will trigger automatically the distribution of result for clients of the gather itf
                resultsReturned = true;
                futuresHandler.setFutureOfGatheredInvocation(futureResult);
            } else {
                // ignore
            }
            try {
                GatherFuturesHandlerPool.instance().returnFuturesHandler(futuresHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        timeoutTimer.cancel();
    }

    /**
     * @return the creationTime.
     */
    public long getCreationTime() {
        return creationTime;
    }

    public void returnFuturesHandlerToPool() {
        if (futuresHandler != null) {
            futuresHandler.passivate();
        }
    }

    /**
     * @return Returns the requests.
     */
    public Map<ItfID, ComponentRequest> getRequests() {
        return requests;
    }

    /**
     * @return Returns the connectedClientItfs.
     */
    public List<Object> getConnectedClientItfs() {
        return connectedClientItfs;
    }

    private class TimeoutTask extends SafeTimerTask {
        GatherRequestsQueue requestsQueue;

        public TimeoutTask(GatherRequestsQueue requestsQueue) {
            this.requestsQueue = requestsQueue;
        }

        @Override
        public void safeRun() {
            requestsQueue.timedout = true;
            if (!resultsReturned) {
                if (!thrownTimeoutException) {
                    requestsQueue.addFutureForGatheredRequest(null);
                }
            }
        }
    }

    public void migrateFuturesHandlerTo(Node node) throws MigrationException {
        futuresHandler.migrateTo(node);
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        //    System.out.println("writing gather requests queue");
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        //    System.out.println("reading gather requests queue");
        in.defaultReadObject();
    }
}
