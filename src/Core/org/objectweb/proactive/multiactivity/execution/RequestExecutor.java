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
package org.objectweb.proactive.multiactivity.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.Context;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.future.Future;
import org.objectweb.proactive.core.body.future.FutureID;
import org.objectweb.proactive.core.body.future.FutureProxy;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestQueue;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.multiactivity.ServingController;
import org.objectweb.proactive.multiactivity.compatibility.CompatibilityTracker;
import org.objectweb.proactive.multiactivity.policy.ServingPolicy;
import org.objectweb.proactive.multiactivity.priority.PriorityConstraint;
import org.objectweb.proactive.multiactivity.priority.PriorityGroup;
import org.objectweb.proactive.multiactivity.priority.PriorityManager;


/**
 * The request executor that constitutes the multi-active service. It contains
 * two management threads: one listens to the queue and applies the scheduling
 * policy, while the other manages the execution of requests on threads.
 * 
 * @author The ProActive Team
 */
public class RequestExecutor implements FutureWaiter, ServingController {

    private static Logger log = ProActiveLogger.getLogger(Loggers.MULTIACTIVITY);

    /**
     * Number of concurrent threads allowed
     */
    private int THREAD_LIMIT = Integer.MAX_VALUE;

    /**
     * If set to true, then the THREAD_LIMIT refers to the total number of
     * serves. If false then it refers to actively executing serves, not the
     * waiting by necessity ones.
     */
    private boolean LIMIT_TOTAL_THREADS = false;

    /**
     * If true re-entrant calls will be hosted on the same thread as their
     * source. If false than all serves will be served on separate threads.
     */
    private boolean SAME_THREAD_REENTRANT = false;

    private final CompatibilityTracker compatibility;

    private final Body body;

    private final RequestQueue requestQueue;

    /**
     * Threadpool
     */
    private final ExecutorService executorService;

    /**
     * Requests currently being executed.
     */
    private final HashSet<RunnableRequest> active;

    /**
     * Requests blocked on some event.
     */
    private final HashSet<RunnableRequest> waiting;

    /**
     * Set of futures whose values have already arrived
     */
    private final HashSet<FutureID> hasArrived;

    /**
     * Associates with each thread a list of requests which represents the stack
     * of execution inside the thread. Only the top level request can be active.
     */
    private final HashMap<Long, List<RunnableRequest>> threadUsage;

    /**
     * Associates a session-tag with a set of requests -- the ones which are
     * part of the same execution path.
     */
    private HashMap<String, Set<RunnableRequest>> requestTags;

    /**
     * List of requests waiting for the value of a future
     */
    private final HashMap<FutureID, List<RunnableRequest>> waitingList;

    /**
     * Pairs of requests meaning which is hosting which inside it. Hosting means
     * that when a wait by necessity occurs the first request will perform a
     * serving of the second request instead of waiting for the future. It will
     * 'resume' the waiting when the second request finishes execution
     */
    private final Map<RunnableRequest, RunnableRequest> hostMap;

    /*
     * This counter allows to warn the multiactivity framework that a thread has 
     * been sent to sleep or awaken from sleep manually such that these states are 
     * considered in the soft and hard limit of the current multi-active object.
     */
    private final AtomicInteger extraActiveRequestCount = new AtomicInteger(0);

    private final PriorityManager priorityManager;

    /**
     * Default constructor.
     * 
     * @param body
     *            Body of the active object.
     * @param compatibility
     *            Compatibility information of the active object's class
     * @param priorityConstraints
     *            Priority constraints
     */
    public RequestExecutor(final Body body, final CompatibilityTracker compatibility,
            final List<PriorityConstraint> priorityConstraints) {
        this.compatibility = compatibility;
        this.body = body;
        this.requestQueue = body.getRequestQueue();
        this.priorityManager = new PriorityManager(priorityConstraints);

        // similar to Executors.newCachedThreadPool() 
        executorService = new ThreadPoolExecutorNameUpdater(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

        active = new HashSet<RunnableRequest>();
        waiting = new HashSet<RunnableRequest>();
        hasArrived = new HashSet<FutureID>();
        threadUsage = new HashMap<Long, List<RunnableRequest>>();
        waitingList = new HashMap<FutureID, List<RunnableRequest>>();
        hostMap = new ConcurrentHashMap<RunnableRequest, RunnableRequest>();

        FutureWaiterRegistry.putForBody(body.getID(), this);
    }

    private final class ThreadPoolExecutorNameUpdater extends ThreadPoolExecutor {

        private final String bodyID = body.getID().toString();

        public ThreadPoolExecutorNameUpdater(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
                final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void beforeExecute(final Thread thread, final Runnable r) {
            thread.setName("MAOs Executor Thread(" + thread.getId() + ") for " + this.bodyID);
            super.beforeExecute(thread, r);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void afterExecute(final Runnable r, final Throwable t) {
            final Thread thread = Thread.currentThread();
            thread.setName("IDLE MAOs Executor Thread(" + thread.getId() + ") for " + this.bodyID);
            super.afterExecute(r, t);
        }

    }

    /**
     * Constructor with all options.
     * 
     * @param body
     *            Body of the active object
     * @param compatibility
     *            Compatibility information of the active object's class
     * @param priorityConstraints
     *            Priority constraints
     * @param activeLimit
     *            Thread limit
     * @param hardLimit
     *            Hard or soft limit (limiting total nb of threads, or only
     *            those which are active)
     * @param hostReentrant
     *            Whether to serve re-entrant calls on the same thread as their
     *            source
     */
    public RequestExecutor(final Body body, final CompatibilityTracker compatibility,
            final List<PriorityConstraint> priorityConstraints, final int activeLimit, final boolean hardLimit,
            final boolean hostReentrant) {
        this(body, compatibility, priorityConstraints);

        THREAD_LIMIT = activeLimit;
        LIMIT_TOTAL_THREADS = hardLimit;
        SAME_THREAD_REENTRANT = hostReentrant;

        if (SAME_THREAD_REENTRANT) {
            requestTags = new HashMap<String, Set<RunnableRequest>>();
        }

    }

    /**
     * Method for changing the limits inside the executor during runtime.
     * 
     * @param activeLimit
     *            Thread limit
     * @param hardLimit
     *            Hard or soft limit (limiting total nb of threads, or only
     *            those which are active)
     * @param hostReentrant
     *            Whether to serve re-entrant calls on the same thread as their
     *            source
     */
    public void configure(final int activeLimit, final boolean hardLimit, final boolean hostReentrant) {
        synchronized (this) {

            THREAD_LIMIT = activeLimit;
            LIMIT_TOTAL_THREADS = hardLimit;

            if (SAME_THREAD_REENTRANT != hostReentrant) {
                if (hostReentrant == true) {
                    // must check if the tagging mechanism is activated in PA.
                    // if it has not been started, we are enable to do same
                    // thread re-entrance
                    if (CentralPAPropertyRepository.PA_TAG_DSF.isTrue()) {
                        SAME_THREAD_REENTRANT = hostReentrant;
                        // 'create the map and populate it with tags
                        requestTags = new HashMap<String, Set<RunnableRequest>>();
                        for (final RunnableRequest r : waiting) {
                            if (isNotAHost(r)) {
                                if (!requestTags.containsKey(r.getSessionTag())) {
                                    requestTags.put(r.getSessionTag(), new HashSet<RunnableRequest>());
                                }
                                requestTags.get(r.getSessionTag()).add(r);
                            }
                        }
                    } else {
                        requestTags = null;
                        log
                                .error("Same thread re-entrance was requested, but property 'PA_TAG_DSF' is set to false");
                    }
                } else {
                    // clean up
                    requestTags = null;
                }
            }

            this.notify();
        }
    }

    /**
     * This is the heart of the executor. It is an internal scheduling thread
     * that coordinates wake-ups, and waits and future value arrivals. Before
     * doing that it also starts a thread for the queue handler that uses a
     * custom policy for scheduling.
     * 
     * @param policy
     */
    public void execute(final ServingPolicy policy) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                requestQueueHandler(policy);

            }
        }, "Request listener for " + body).start();

        internalExecute();

    }

    /**
     * Method that retrieves the compatible requests from the queue based on a
     * custom policy.
     * 
     * @param policy
     */
    private void requestQueueHandler(final ServingPolicy policy) {

        // register thread, so we can look up the Body if needed
        LocalBodyStore.getInstance().pushContext(new Context(body, null));

        synchronized (requestQueue) {
            while (body.isActive()) {

                // get compatible ones from the queue
                final List<Request> rc = policy.runPolicy(compatibility);

                if (rc.size() >= 0) {
                    synchronized (this) {
                        // add them to the ready set
                        for (int i = 0; i < rc.size(); i++) {
                            final RunnableRequest runnableRequest = wrapRequest(rc.get(i));
                            priorityManager.register(runnableRequest);
                        }

                        // if anything can be done, let the other thread know
                        if (countActive() < THREAD_LIMIT) {
                            this.notify();
                        } else {
                            // same for boosted methods
                            final Iterator<List<PriorityConstraint>> it = this.priorityManager
                                    .getPriorityConstraints().values().iterator();

                            boolean notFound = true;
                            while (it.hasNext() && notFound) {
                                for (final PriorityConstraint pc : it.next()) {
                                    if (pc.hasFreeBoostThreads()) {
                                        notFound = false;
                                        this.notify();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (log.isDebugEnabled()) {
                    synchronized (this) {
                        final StringBuilder buf = new StringBuilder();
                        buf.append("Dumping Queues Content on ");
                        buf.append(body.getID());
                        buf.append("\nRequest queue=[");

                        Iterator<Request> it = this.requestQueue.getInternalQueue().iterator();
                        while (it.hasNext()) {
                            buf.append(RequestExecutor.toString(it.next()));

                            if (it.hasNext()) {
                                buf.append(" ");
                            }
                        }

                        buf.append("]\nReady queue=[");

                        it = this.getPriorityManager().getReadyRequests().iterator();
                        while (it.hasNext()) {
                            buf.append(RequestExecutor.toString(it.next()));
                            if (it.hasNext()) {
                                buf.append(" ");
                            }
                        }

                        buf.append("]\nExecuting queue=[");

                        it = compatibility.getExecutingRequests().iterator();
                        while (it.hasNext()) {
                            buf.append(RequestExecutor.toString(it.next()));
                            if (it.hasNext()) {
                                buf.append(" ");
                            }
                        }
                        buf.append("]");

                        log.trace(buf.toString());
                    }
                }

                try {
                    requestQueue.wait();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Serving and Thread management.
     */
    private void internalExecute() {
        final boolean isTraceEnabled = log.isTraceEnabled();

        synchronized (this) {
            while (body.isActive()) {

                Iterator<RunnableRequest> i;

                if (SAME_THREAD_REENTRANT) {
                    if (canServeOneHosted()) {
                        final PriorityGroup selectedPriorityGroup = this.priorityManager.getHighestPriorityGroup();
                        tracePriorityGroups(selectedPriorityGroup);

                        i = selectedPriorityGroup.iterator();

                        if (isTraceEnabled && i.hasNext()) {
                            log.trace("Requests served SAME_THREAD_REENTRANT");
                        }
                        // see if we can serve a request on the thread of an
                        // other one
                        while (canServeOneHosted() && i.hasNext()) {
                            final RunnableRequest parasite = i.next();

                            final String tag = parasite.getSessionTag();
                            if (tag != null) {
                                if (requestTags.containsKey(tag)) {
                                    for (final RunnableRequest host : requestTags.get(tag)) {
                                        if (host != null && isNotAHost(host)) {
                                            synchronized (host) {
                                                if (log.isTraceEnabled()) {
                                                    log.trace("  " + toString(parasite.getRequest()));
                                                }

                                                active.add(parasite);
                                                hostMap.put(host, parasite);
                                                requestTags.get(tag).remove(host);
                                                parasite.setHostedOn(host);
                                                i.remove();
                                                host.notify();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // WAKE any waiting thread that could resume execution and there
                // are free resources for it
                // i = waiting.iterator();
                final Iterator<List<RunnableRequest>> it = threadUsage.values().iterator();

                while (canResumeOne() && it.hasNext()) {

                    final List<RunnableRequest> list = it.next();
                    final RunnableRequest cont = list.get(0);
                    // check if the future has arrived + the request is not
                    // already engaged in a hosted serving
                    if (hasArrived.contains(cont.getWaitingOn()) && isNotAHost(cont)) {

                        synchronized (cont) {
                            // i.remove();
                            waiting.remove(cont);
                            resumeServing(cont, cont.getWaitingOn());
                            cont.notify();
                        }
                    }
                }

                // SERVE any request who is ready and there are resources
                // available but requests with highest priority in first
                if (canServeOne()) {
                    final PriorityGroup selectedPriorityGroup = this.priorityManager.getHighestPriorityGroup();
                    tracePriorityGroups(selectedPriorityGroup);

                    i = selectedPriorityGroup.iterator();

                    if (isTraceEnabled && i.hasNext()) {
                        log.trace("Requests served " + body.getReifiedObject().toString());
                    }

                    while (canServeOne() && i.hasNext()) {
                        final RunnableRequest current = i.next();
                        active.add(current);
                        executorService.execute(current);
                        i.remove();

                        if (isTraceEnabled) {
                            log.trace("  " + toString(current.getRequest()));
                        }
                    }
                }

                // all available threads are used, we try to use boost threads
                // for executing starved method calls. A method call is said
                // "starved" if the priority constraint it belongs to is not
                // associated to another request being served. Besides, the
                // priority constraint must have some free boostThreads.
                if (this.countActive() == THREAD_LIMIT && this.priorityManager.hasSomeRequestsRegistered()) {
                    final List<PriorityConstraint> starvedPriorityConstraints = findStarvedPriorityConstraints();

                    for (final PriorityConstraint pc : starvedPriorityConstraints) {
                        // find ready requests for the specified starved
                        // priority constraint
                        if (pc.hasFreeBoostThreads()) {
                            final List<RunnableRequest> l = this.priorityManager.getRequestsSatisfying(pc);

                            i = l.iterator();

                            StringBuilder buf = null;
                            if (isTraceEnabled) {
                                buf = new StringBuilder();

                                if (i.hasNext()) {
                                    buf.append("Requests served with boost for ");
                                    buf.append(pc);
                                    buf.append("\n");
                                }
                            }

                            while (pc.hasFreeBoostThreads() && i.hasNext()) {
                                final RunnableRequest current = i.next();
                                current.setBoosted();
                                pc.incrementActiveBoostThreads();
                                executorService.execute(current);
                                this.priorityManager.unregister(current, pc.getPriorityLevel());

                                if (isTraceEnabled) {
                                    buf.append("  ");
                                    buf.append(toString(current.getRequest()));

                                    if (i.hasNext()) {
                                        buf.append("\n");
                                    }
                                }
                            }

                            if (isTraceEnabled && buf.length() > 0) {
                                log.trace(buf.toString());
                            }
                        }
                    }
                }

                // SLEEP if nothing else to do
                // will wake up on 1) new submit, 2) finish of a request, 3)
                // arrival of a future, 4) wait of a request
                try {
                    this.wait();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private List<PriorityConstraint> findStarvedPriorityConstraints() {
        final List<PriorityConstraint> priorityConstraints = new ArrayList<PriorityConstraint>();

        for (final List<PriorityConstraint> pcs : this.priorityManager.getPriorityConstraints().values()) {
            for (final PriorityConstraint pc : pcs) {
                if (pc.hasFreeBoostThreads()) {
                    // priority constraint satisfied by one of the active
                    // requests
                    boolean priorityConstraintSatisfied = false;

                    for (final RunnableRequest r : active) {
                        if (r.getPriorityConstraint() == pc) {
                            priorityConstraintSatisfied = true;
                            break;
                        }
                    }

                    if (!priorityConstraintSatisfied) {
                        priorityConstraints.add(pc);
                    }
                }
            }
        }

        return priorityConstraints;
    }

    private void tracePriorityGroups(final PriorityGroup priorityGroup) {
        if (log.isTraceEnabled()) {
            final StringBuilder buf = new StringBuilder();

            buf.append("Priority groups snapshot\n");

            for (final PriorityGroup pg : this.priorityManager.getPriorityGroups().values()) {
                buf.append("  groupLevel=" + pg.getPriorityLevel() + "\t nbRequests=" + pg.size() +
                    (pg == priorityGroup ? "\t [selected]" : "") + "\n");
            }

            buf.append("  default threads usage ");
            buf.append(this.countActive());
            buf.append("/");
            buf.append(this.THREAD_LIMIT);
            buf.append("\n");

            log.trace(buf.toString());
        }
    }

    public static String toString(final Request request) {
        final StringBuilder result = new StringBuilder();

        result.append(request.getMethodCall().getName());
        result.append('(');

        for (int i = 0; i < request.getMethodCall().getNumberOfParameter(); i++) {
            final Object parameter = request.getMethodCall().getParameter(i);

            if (parameter == null) {
                result.append("null");
            } else {
                result.append(parameter.getClass());
            }

            result.append("|ihc=");
            result.append(System.identityHashCode(request));

            if (i < request.getMethodCall().getNumberOfParameter() - 1) {
                result.append(" ");
            }
        }

        result.append(')');

        return result.toString();
    }

    /**
     * Returns true if the request is not hosting any other serve in its thread.
     * 
     * @param r
     * @return
     */
    private boolean isNotAHost(final RunnableRequest r) {
        return !hostMap.keySet().contains(r) ||
            !active.contains(hostMap.get(r)) && !waiting.contains(hostMap.get(r));
    }

    /**
     * Returns true if it may be possible to find a request to be hosted inside
     * an other.
     * 
     * @return true if it may be possible to find a request to be hosted inside
     *         an other.
     */
    private boolean canServeOneHosted() {
        return this.priorityManager.getNbRequestsRegistered() > 0 && requestTags.size() > 0 &&
            countActive() < THREAD_LIMIT;
    }

    /**
     * Returns true if it may be possible to resume a previously blocked
     * request.
     * 
     * @return true if it may be possible to resume a previously blocked request
     */
    private boolean canResumeOne() {
        return LIMIT_TOTAL_THREADS
        // hard limit
        ? waiting.size() > 0 && hasArrived.size() > 0
                // soft limit
                : waiting.size() > 0 && hasArrived.size() > 0;// &&
        // countActive()
        // <
        // THREAD_LIMIT);
    }

    /**
     * Returns true if there are ready requests and free resources that permit
     * the serving of at least one additional one.
     * 
     * @return True if there are ready requests and free resources that permit
     *         the serving of at least one additional one.
     */
    private boolean canServeOne() {
        return LIMIT_TOTAL_THREADS
        // hard limit
        ? this.priorityManager.getNbRequestsRegistered() > 0 && threadUsage.keySet().size() < THREAD_LIMIT && countActive() < THREAD_LIMIT
                // soft limit
                : this.priorityManager.getNbRequestsRegistered() > 0 && countActive() < THREAD_LIMIT;
    }

    /**
     * Called from the {@link #waitForFuture(Future)} method to signal the
     * blocking of a request.
     * 
     * @param r
     *            Wrapper of the request that starts waiting.
     * @param f
     *            The future for whose value the wait occurred.
     */
    private void signalWaitFor(final RunnableRequest r, final FutureID fId) {
        synchronized (this) {
            active.remove(r);
            waiting.add(r);
            if (!waitingList.containsKey(fId)) {
                waitingList.put(fId, new LinkedList<RunnableRequest>());
            }
            waitingList.get(fId).add(r);

            if (SAME_THREAD_REENTRANT) {
                if (!requestTags.containsKey(r.getSessionTag())) {
                    requestTags.put(r.getSessionTag(), new HashSet<RunnableRequest>());
                }
                requestTags.get(r.getSessionTag()).add(r);
            }

            r.setCanRun(false);
            r.setWaitingOn(fId);
            this.notify();
        }
    }

    /**
     * Called from the executor's thread to signal a waiting request that it can
     * resume execution.
     * 
     * @param r
     *            The request's wrapper.
     * @param fId
     *            The future it was waiting for.
     */
    private void resumeServing(final RunnableRequest r, final FutureID fId) {
        synchronized (this) {
            active.add(r);

            hostMap.remove(r);

            r.setCanRun(true);
            r.setWaitingOn(null);

            waitingList.get(fId).remove(r);
            if (waitingList.get(fId).size() == 0) {
                waitingList.remove(fId);
                hasArrived.remove(fId);
            }

            if (SAME_THREAD_REENTRANT) {
                final String sessionTag = r.getSessionTag();
                if (sessionTag != null) {
                    requestTags.get(sessionTag).remove(r);
                    if (requestTags.get(sessionTag).size() == 0) {
                        requestTags.remove(sessionTag);
                    }
                }
            }
        }
    }

    public void serve(final RunnableRequest runnableRequest) {
        this.serveStarted(runnableRequest);
        body.serve(runnableRequest.getRequest());
        synchronized (this.requestQueue) {
            serveStopped(runnableRequest);
            compatibility.removeRunning(runnableRequest.getRequest());
            requestQueue.notify();
        }
    }

    /**
     * Tell the executor about the creation of a new thread, of the current
     * usage of an already existing thread.
     * 
     * @param r
     */
    public void serveStarted(final RunnableRequest r) {
        synchronized (this) {
            final Long tId = Thread.currentThread().getId();
            if (!threadUsage.containsKey(tId)) {
                threadUsage.put(tId, new LinkedList<RunnableRequest>());
            }
            threadUsage.get(tId).add(0, r);
        }
    }

    /**
     * Tell the executor about the termination, or updated usage stack of a
     * thread.
     * 
     * @param r
     */
    private void serveStopped(final RunnableRequest r) {
        synchronized (this) {
            if (r.isBoosted()) {
                r.getPriorityConstraint().decrementActiveBoostThreads();
            } else {
                active.remove(r);
            }

            final Long tId = Thread.currentThread().getId();
            if (!r.equals(threadUsage.get(tId).remove(0))) {
                System.err.println("Thread inconsistency -- Request is not found in the stack.");
            }

            if (threadUsage.get(tId).size() == 0) {
                threadUsage.remove(tId);
            }

            if (SAME_THREAD_REENTRANT) {
                if (r.getHostedOn() != null) {
                    if (!requestTags.containsKey(r.getHostedOn().getSessionTag())) {
                        requestTags.put(r.getHostedOn().getSessionTag(), new HashSet<RunnableRequest>());
                    }
                    requestTags.get(r.getSessionTag()).add(r.getHostedOn());
                }
            }

            this.notify();
        }
    }

    @Override
    public void waitForFuture(final Future future) {
        final RunnableRequest thisRequest = threadUsage.get(Thread.currentThread().getId()).get(0);
        synchronized (thisRequest) {
            synchronized (future) {
                if (((FutureProxy) future).isAvailable()) {
                    return;
                }

                signalWaitFor(thisRequest, future.getFutureID());
            }

            while (!thisRequest.canRun()) {

                try {
                    thisRequest.wait();
                } catch (final InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (hostMap.containsKey(thisRequest) && hostMap.get(thisRequest) != null) {
                    hostMap.get(thisRequest).run();
                }

            }
        }

    }

    @Override
    public void futureArrived(final Future future) {
        synchronized (this) {
            hasArrived.add(future.getFutureID());
            this.notify();
        }
    }

    /**
     * Makes a request runnable on a separate thread, by wrapping it in an
     * instance of {@link RunnableRequest}.
     * 
     * @param request
     * @return
     */
    protected RunnableRequest wrapRequest(final Request request) {
        return new RunnableRequest(this, request);
    }

    @Override
    public int getNumberOfConcurrent() {
        synchronized (this) {
            return THREAD_LIMIT;
        }
    }

    @Override
    public int decrementNumberOfConcurrent(final int cnt) {
        synchronized (this) {
            if (cnt > 0) {
                THREAD_LIMIT = THREAD_LIMIT > cnt ? THREAD_LIMIT - cnt : THREAD_LIMIT;
                return THREAD_LIMIT;
            } else {
                return THREAD_LIMIT;
            }
        }
    }

    @Override
		public int decrementNumberOfConcurrent() {
        return decrementNumberOfConcurrent(1);
    }

    @Override
    public int incrementNumberOfConcurrent(final int cnt) {
        synchronized (this) {
            if (cnt > 0) {
                THREAD_LIMIT = THREAD_LIMIT + cnt;
                this.notify();
                return THREAD_LIMIT;
            } else {
                return THREAD_LIMIT;
            }
        }
    }

    @Override
    public int incrementNumberOfConcurrent() {
        return incrementNumberOfConcurrent(1);
    }

    @Override
    public void setNumberOfConcurrent(final int numActive) {
        synchronized (this) {
            if (numActive > 0) {
                THREAD_LIMIT = numActive;
                this.notify();
            }
        }
    }

    public int countActive() {
        return active.size() - extraActiveRequestCount.get();
    }

    public void incrementExtraActiveRequestCount(final int i) {
        extraActiveRequestCount.addAndGet(i);
    }

    public void decrementExtraActiveRequestCount(final int i) {
        extraActiveRequestCount.addAndGet(i * -1);
    }

    public int getExtraActiveRequestCount() {
        return extraActiveRequestCount.get();
    }

    public PriorityManager getPriorityManager() {
        return this.priorityManager;
    }

    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

}
