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
 *  Contributor(s): ActiveEon Team - http://www.activeeon.com
 *
 * ################################################################
 * $$ACTIVEEON_CONTRIBUTOR$$
 */
package org.objectweb.proactive.core.body;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActiveInternalObject;
import org.objectweb.proactive.annotation.ImmediateService;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.benchmarks.timit.util.CoreTimersContainer;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.exceptions.InactiveBodyException;
import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.ft.service.FaultToleranceTechnicalService;
import org.objectweb.proactive.core.body.future.BodiesAndTags;
import org.objectweb.proactive.core.body.future.Future;
import org.objectweb.proactive.core.body.future.FuturePool;
import org.objectweb.proactive.core.body.future.MethodCallResult;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.reply.ReplyImpl;
import org.objectweb.proactive.core.body.reply.ReplyReceiver;
import org.objectweb.proactive.core.body.request.BlockingRequestQueue;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFactory;
import org.objectweb.proactive.core.body.request.RequestQueue;
import org.objectweb.proactive.core.body.request.RequestReceiver;
import org.objectweb.proactive.core.body.request.RequestReceiverImpl;
import org.objectweb.proactive.core.body.tags.MessageTags;
import org.objectweb.proactive.core.body.tags.Tag;
import org.objectweb.proactive.core.body.tags.tag.CMTag;
import org.objectweb.proactive.core.body.tags.tag.DsiTag;
import org.objectweb.proactive.core.component.ComponentMethodCallMetadata;
import org.objectweb.proactive.core.component.ComponentParameters;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.body.ComponentBodyImpl;
import org.objectweb.proactive.core.component.control.PAMulticastController;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.representative.PAComponentRepresentative;
import org.objectweb.proactive.core.component.request.ComponentRequestImpl;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.debug.debugger.BreakpointType;
import org.objectweb.proactive.core.gc.GarbageCollector;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapper;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.jmx.notification.RequestNotificationData;
import org.objectweb.proactive.core.jmx.server.ServerConnector;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.MOPException;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.ObjectReferenceReplacer;
import org.objectweb.proactive.core.mop.ObjectReplacer;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.security.exceptions.CommunicationForbiddenException;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.util.profiling.Profiling;
import org.objectweb.proactive.core.util.profiling.TimerWarehouse;
import org.objectweb.proactive.multiactivity.MultiActiveService;
import org.objectweb.proactive.multiactivity.execution.FutureWaiterRegistry;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * <p>
 * This class gives a common implementation of the Body interface. It provides
 * all the non specific behavior allowing sub-class to write the detail
 * implementation.
 * </p>
 * <p>
 * Each body is identify by an unique identifier.
 * </p>
 * <p>
 * All active bodies that get created in one JVM register themselves into a
 * table that allows to tack them done. The registering and deregistering is
 * done by the AbstractBody and the table is managed here as well using some
 * static methods.
 * </p>
 * <p>
 * In order to let somebody customize the body of an active object without
 * subclassing it, AbstractBody delegates lot of tasks to satellite objects that
 * implements a given interface. Abstract protected methods instantiate those
 * objects allowing subclasses to create them as they want (using customizable
 * factories or instance).
 * </p>
 *
 * @author The ProActive Team
 * @version 1.0, 2001/10/23
 * @see org.objectweb.proactive.Body
 * @see UniqueID
 * @since ProActive 0.9
 */
public abstract class BodyImpl extends AbstractBody implements java.io.Serializable, BodyImplMBean {
    //
    // -- STATIC MEMBERS -----------------------------------------------
    //

    //
    // -- PROTECTED MEMBERS -----------------------------------------------
    //

    /**
     * The component in charge of receiving reply
     */
    protected ReplyReceiver replyReceiver;

    /**
     * The component in charge of receiving request
     */
    protected RequestReceiver requestReceiver;

    // already checked methods
    private HashMap<String, HashSet<List<Class<?>>>> checkedMethodNames;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //

    /**
     * Creates a new AbstractBody. Used for serialization.
     */
    public BodyImpl() {
    }

    /**
     * Creates a new AbstractBody for an active object attached to a given node.
     *
     * @param reifiedObject the active object that body is for
     * @param nodeURL       the URL of the node that body is attached to
     * @param factory       the factory able to construct new factories for each type of meta objects
     *                      needed by this body
     */
    public BodyImpl(Object reifiedObject, String nodeURL, MetaObjectFactory factory)
            throws ActiveObjectCreationException {
        super(reifiedObject, nodeURL, factory);

        super.isProActiveInternalObject = reifiedObject instanceof ProActiveInternalObject;

        // TIMING
        if (!super.isProActiveInternalObject) {
            super.timersContainer = CoreTimersContainer.create(super.bodyID, reifiedObject, factory, nodeURL);

            if (super.timersContainer != null) {
                TimerWarehouse.enableTimers();
                // START TOTAL TIMER
                TimerWarehouse.startTimer(super.bodyID, TimerWarehouse.TOTAL);
            }
        }

        this.checkedMethodNames = new HashMap<String, HashSet<List<Class<?>>>>();

        this.requestReceiver = factory.newRequestReceiverFactory().newRequestReceiver();
        this.replyReceiver = factory.newReplyReceiverFactory().newReplyReceiver();

        setLocalBodyImpl(new ActiveLocalBodyStrategy(reifiedObject, factory.newRequestQueueFactory()
                .newRequestQueue(this.bodyID), factory.newRequestFactory()));
        this.localBodyStrategy.getFuturePool().setOwnerBody(this);

        // FAULT TOLERANCE=
        try {
            Node node = NodeFactory.getNode(this.getNodeURL());
            if ("true".equals(node.getProperty(FaultToleranceTechnicalService.FT_ENABLED))) {
                // if the object is a ProActive internal object, FT is disabled
                if (!super.isProActiveInternalObject) {
                    // if the object is not serializable or instance of non static enclosing class (PROACTIVE-277), FT is disabled
                    Class reifiedClass = this.localBodyStrategy.getReifiedObject().getClass();
                    if ((this.localBodyStrategy.getReifiedObject() instanceof Serializable) ||
                        (reifiedClass.isMemberClass() && !Modifier.isStatic(reifiedClass.getModifiers()))) {
                        try {
                            // create the fault tolerance manager
                            int protocolSelector = FTManager.getProtoSelector(node
                                    .getProperty(FaultToleranceTechnicalService.PROTOCOL));
                            this.ftmanager = factory.newFTManagerFactory().newFTManager(protocolSelector);
                            this.ftmanager.init(this);
                            if (bodyLogger.isDebugEnabled()) {
                                bodyLogger.debug("Init FTManager on " + this.getNodeURL());
                            }
                        } catch (ProActiveException e) {
                            bodyLogger
                                    .error("**ERROR** Unable to init FTManager. Fault-tolerance is disabled " +
                                        e);
                            this.ftmanager = null;
                        }
                    } else {
                        // target body is not serilizable
                        bodyLogger
                                .error("**WARNING** Activated object is not serializable or instance of non static member class (" +
                                    this.localBodyStrategy.getReifiedObject().getClass() +
                                    "). Fault-tolerance is disabled for this active object");
                        this.ftmanager = null;
                    }
                }
            } else {
                this.ftmanager = null;
            }
        } catch (ProActiveException e) {
            bodyLogger.error("**ERROR** Unable to read node configuration. Fault-tolerance is disabled");
            this.ftmanager = null;
        }

        this.gc = new GarbageCollector(this);

        // JMX registration
        if (!super.isProActiveInternalObject) {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName oname = FactoryName.createActiveObjectName(this.bodyID);
            if (!mbs.isRegistered(oname)) {
                super.mbean = new BodyWrapper(oname, this);
                try {
                    mbs.registerMBean(mbean, oname);
                } catch (InstanceAlreadyExistsException e) {
                    bodyLogger.error("A MBean with the object name " + oname + " already exists", e);
                } catch (MBeanRegistrationException e) {
                    bodyLogger.error("Can't register the MBean of the body", e);
                } catch (NotCompliantMBeanException e) {
                    bodyLogger.error("The MBean of the body is not JMX compliant", e);
                }
            }
        }

        // ImmediateService 
        initializeImmediateService(reifiedObject);
    }

    //
    // -- PROTECTED METHODS -----------------------------------------------
    //

    /**
     * Receives a request for later processing. The call to this method is non blocking unless the
     * body cannot temporary receive the request.
     *
     * @param request the request to process
     * @throws java.io.IOException if the request cannot be accepted
     */
    @Override
    protected int internalReceiveRequest(Request request) throws java.io.IOException,
            RenegotiateSessionException {
        // JMX Notification
        if (!isProActiveInternalObject && (this.mbean != null)) {
            String tagNotification = createTagNotification(request.getTags());
            RequestNotificationData requestNotificationData = new RequestNotificationData(request
                    .getSourceBodyID(), request.getSenderNodeURL(), this.bodyID, this.nodeURL, request
                    .getMethodName(), getRequestQueue().size() + 1, request.getSequenceNumber(),
                tagNotification);
            this.mbean.sendNotification(NotificationType.requestReceived, requestNotificationData);
        }

        // END JMX Notification

        // request queue length = number of requests in queue
        // + the one to add now
        try {
            return this.requestReceiver.receiveRequest(request, this);
        } catch (CommunicationForbiddenException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Receives a reply in response to a former request.
     *
     * @param reply the reply received
     * @throws java.io.IOException if the reply cannot be accepted
     */
    @Override
    protected int internalReceiveReply(Reply reply) throws java.io.IOException {
    	// cruz:
    	// Only want to treat replies that have the Future instead of more automatic continuations, but an
    	// Automatic Continuation can also have the final reply. Before, this if didn't exist and the notification
    	// was sent on any reply. (also the reply.getResultObject()!=null was not checked)
    	if(!reply.isAutomaticContinuation()) {
	        // JMX Notification
	        if (!isProActiveInternalObject && (this.mbean != null) && reply.getResult().getResultObjet() != null
	        		&& reply.getResult().getException() == null) {

	            String tagNotification = createTagNotification(reply.getTags());
	            RequestNotificationData requestNotificationData = new RequestNotificationData(
	                BodyImpl.this.bodyID, BodyImpl.this.getNodeURL(), reply.getSourceBodyID(), this.nodeURL,
	                reply.getMethodName(), getRequestQueue().size() + 1, reply.getSequenceNumber(),
	                tagNotification);
	            this.mbean.sendNotification(NotificationType.replyReceived, requestNotificationData);
	        }
	        // END JMX Notification
    	}

        return replyReceiver.receiveReply(reply, this, getFuturePool());
    }

    /**
     * Signals that the activity of this body, managed by the active thread has just stopped.
     *
     * @param completeACs if true, and if there are remaining AC in the futurepool, the AC thread is
     *                    not killed now; it will be killed after the sending of the last remaining AC.
     */
    @Override
    protected void activityStopped(boolean completeACs) {
        super.activityStopped(completeACs);

        try {
            this.localBodyStrategy.getRequestQueue().destroy();
        } catch (ProActiveRuntimeException e) {
            // this method can be called twos times if the automatic
            // continuation thread
            // is killed *after* the activity thread.
            bodyLogger.debug("Terminating already terminated body " + this.getID());
        }

        this.getFuturePool().terminateAC(completeACs);

        if (!completeACs) {
            setLocalBodyImpl(new InactiveLocalBodyStrategy());
        } else {
            // the futurepool is still needed for remaining ACs
            setLocalBodyImpl(new InactiveLocalBodyStrategy(this.getFuturePool()));
        }

        // terminate request receiver
        this.requestReceiver.terminate();
    }

    public boolean checkMethod(String methodName) {
        return checkMethod(methodName, null);
    }

    @Deprecated
    public void setImmediateService(String methodName) {
        setImmediateService(methodName, false);
    }

    public void setImmediateService(String methodName, boolean uniqueThread) {
        checkImmediateServiceMode(methodName, null, uniqueThread);
        ((RequestReceiverImpl) this.requestReceiver).setImmediateService(methodName, uniqueThread);
    }

    public void setImmediateService(String methodName, Class<?>[] parametersTypes, boolean uniqueThread) {
        checkImmediateServiceMode(methodName, parametersTypes, uniqueThread);
        ((RequestReceiverImpl) this.requestReceiver).setImmediateService(methodName, parametersTypes,
                uniqueThread);
    }

    protected void initializeImmediateService(Object reifiedObject) {
        Method[] methods = reifiedObject.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            ImmediateService is = m.getAnnotation(ImmediateService.class);
            if (is != null) {
                setImmediateService(m.getName(), m.getParameterTypes(), is.uniqueThread());
            }
        }
    }

    private void checkImmediateServiceMode(String methodName, Class<?>[] parametersTypes, boolean uniqueThread) {
        // see PROACTIVE-309
        if (!((ComponentBodyImpl) this).isComponent()) {
            if (parametersTypes == null) { // all args
                if (!((ComponentBodyImpl) this).isComponent()) {
                    if (!checkMethod(methodName)) {
                        throw new NoSuchMethodError(methodName + " is not defined in " +
                            getReifiedObject().getClass().getName());
                    }
                }
            } else { // args are specified
                if (!checkMethod(methodName, parametersTypes)) {
                    String signature = methodName + "(";
                    for (int i = 0; i < parametersTypes.length; i++) {
                        signature += parametersTypes[i] + ((i < parametersTypes.length - 1) ? "," : "");
                    }
                    signature += " is not defined in " + getReifiedObject().getClass().getName();
                    throw new NoSuchMethodError(signature);
                }
            }
        }

        // cannot use IS with unique thread with fault-tolerant active object
        if (uniqueThread && this.ftmanager != null) {
            throw new ProActiveRuntimeException("The method " + methodName +
                " cannot be set as immediate service with unique thread since the active object " +
                this.getID() + " has enabled fault-tolerance.");
        }
    }

    public void removeImmediateService(String methodName) {
        ((RequestReceiverImpl) this.requestReceiver).removeImmediateService(methodName);
    }

    public void removeImmediateService(String methodName, Class<?>[] parametersTypes) {
        ((RequestReceiverImpl) this.requestReceiver).removeImmediateService(methodName, parametersTypes);
    }

    public void updateNodeURL(String newNodeURL) {
        this.nodeURL = newNodeURL;
    }

    @Override
    public boolean isInImmediateService() throws IOException {
        return this.requestReceiver.isInImmediateService();
    }

    public boolean checkMethod(String methodName, Class<?>[] parametersTypes) {
        if (this.checkedMethodNames.containsKey(methodName)) {
            if (parametersTypes != null) {
                // the method name with the right signature has already been
                // checked
                List<Class<?>> parameterTlist = Arrays.asList(parametersTypes);
                HashSet<List<Class<?>>> signatures = this.checkedMethodNames.get(methodName);

                if (signatures.contains(parameterTlist)) {
                    return true;
                }
            } else {
                // the method name has already been checked
                return true;
            }
        }

        // check if the method is defined as public
        Class<?> reifiedClass = getReifiedObject().getClass();
        boolean exists = org.objectweb.proactive.core.mop.Utils.checkMethodExistence(reifiedClass,
                methodName, parametersTypes);

        if (exists) {
            storeInMethodCache(methodName, parametersTypes);

            return true;
        }

        return false;
    }

    /**
     * Stores the given method name with the given parameters types inside our method signature
     * cache to avoid re-testing them
     *
     * @param methodName      name of the method
     * @param parametersTypes parameter type list
     */
    private void storeInMethodCache(String methodName, Class<?>[] parametersTypes) {
        List<Class<?>> parameterTlist = null;

        if (parametersTypes != null) {
            parameterTlist = Arrays.asList(parametersTypes);
        }

        // if we already know a version of this method, we store the new version
        // in the existing set
        if (this.checkedMethodNames.containsKey(methodName) && (parameterTlist != null)) {
            HashSet<List<Class<?>>> signatures = this.checkedMethodNames.get(methodName);
            signatures.add(parameterTlist);
        }
        // otherwise, we create a set containing a single element
        else {
            HashSet<List<Class<?>>> signatures = new HashSet<List<Class<?>>>();

            if (parameterTlist != null) {
                signatures.add(parameterTlist);
            }

            checkedMethodNames.put(methodName, signatures);
        }
    }

    // Create the string from tag data for the notification
    private String createTagNotification(MessageTags tags) {
        String result = "";
        if (tags != null) {
            for (Tag tag : tags.getTags()) {
                result += tag.getNotificationMessage();
            }
        }
        return result;
    }

    //
    // -- PRIVATE METHODS -----------------------------------------------
    //
    //
    // -- inner classes -----------------------------------------------
    //
    private class ActiveLocalBodyStrategy implements LocalBodyStrategy, java.io.Serializable {
        /**
         * A pool future that contains the pending future objects
         */
        protected FuturePool futures;

        /**
         * The reified object target of the request processed by this body
         */
        protected Object reifiedObject;
        protected BlockingRequestQueue requestQueue;
        protected RequestFactory internalRequestFactory;
        private long absoluteSequenceID;

        //
        // -- CONSTRUCTORS -----------------------------------------------
        //
        public ActiveLocalBodyStrategy(Object reifiedObject, BlockingRequestQueue requestQueue,
                RequestFactory requestFactory) {
            this.reifiedObject = reifiedObject;
            this.futures = new FuturePool();
            this.requestQueue = requestQueue;
            this.internalRequestFactory = requestFactory;
        }

        //
        // -- PUBLIC METHODS -----------------------------------------------
        //
        //
        // -- implements LocalBody
        // -----------------------------------------------
        //
        public FuturePool getFuturePool() {
            return this.futures;
        }

        public BlockingRequestQueue getRequestQueue() {
            return this.requestQueue;
        }

        public Object getReifiedObject() {
            return this.reifiedObject;
        }

        /**
         * Serves the request. The request should be removed from the request queue before serving,
         * which is correctly done by all methods of the Service class. However, this condition is
         * not ensured for custom calls on serve.
         */
        public void serve(Request request) {
            if (Profiling.TIMERS_COMPILED) {
                TimerWarehouse.startServeTimer(bodyID, request.getMethodCall().getReifiedMethod());
            }

            // push the new context
            LocalBodyStore.getInstance().pushContext(
                    new Context(BodyImpl.this, request, FutureWaiterRegistry
                            .getForBody(BodyImpl.this.getID())));

            try {
                serveInternal(request, null);
            } finally {
                LocalBodyStore.getInstance().popContext();
            }

            if (Profiling.TIMERS_COMPILED) {
                TimerWarehouse.stopServeTimer(BodyImpl.this.bodyID);
            }
        }

        /**
         * Serves the request with the given exception as result instead of the normal execution.
         * The request should be removed from the request queue before serving,
         * which is correctly done by all methods of the Service class. However, this condition is
         * not ensured for custom calls on serve.
         */
        public void serveWithException(Request request, Throwable exception) {
            if (Profiling.TIMERS_COMPILED) {
                TimerWarehouse.startServeTimer(bodyID, request.getMethodCall().getReifiedMethod());
            }

            // push the new context
            LocalBodyStore.getInstance().pushContext(new Context(BodyImpl.this, request));

            try {
                serveInternal(request, exception);
            } finally {
                LocalBodyStore.getInstance().popContext();
            }

            if (Profiling.TIMERS_COMPILED) {
                TimerWarehouse.stopServeTimer(BodyImpl.this.bodyID);
            }
        }

        private void serveInternal(Request request, Throwable exception) {
            if (request == null) {
                return;
            }

            if (!isProActiveInternalObject) {
                if (((RequestReceiverImpl) requestReceiver).immediateExecution(request)) {
                    debugger.breakpoint(BreakpointType.NewImmediateService, request);
                } else {
                    debugger.breakpoint(BreakpointType.NewService, request);
                }
            }

            // JMX Notification
            if (!isProActiveInternalObject && (mbean != null)) {
                String tagNotification = createTagNotification(request.getTags());
                RequestNotificationData data = new RequestNotificationData(request.getSourceBodyID(), request
                        .getSenderNodeURL(), BodyImpl.this.bodyID, BodyImpl.this.nodeURL, request
                        .getMethodName(), getRequestQueue().size(), request.getSequenceNumber(),
                    tagNotification);
                mbean.sendNotification(NotificationType.servingStarted, data);
            }

            // END JMX Notification
            Reply reply = null;

            // If the request is not a "terminate Active Object" request,
            // it is served normally.
            if (!isTerminateAORequest(request)) {
                if (exception != null) {

                    if ((exception instanceof Exception) && !(exception instanceof RuntimeException)) {
                        // if the exception is a checked exception, the method must declare in its throws statement, otherwise
                        // the future sent to the user will be invalid
                        boolean thrownFound = false;
                        for (Class exptype : request.getMethodCall().getReifiedMethod().getExceptionTypes()) {
                            thrownFound = thrownFound || exptype.isAssignableFrom(exception.getClass());
                        }
                        if (!thrownFound) {
                            throw new IllegalArgumentException("Invalid Exception " + exception.getClass() +
                                ". The method " + request.getMethodCall().getReifiedMethod() +
                                " don't declare it to be thrown.");
                        }
                        reply = new ReplyImpl(BodyImpl.this.getID(), request.getSequenceNumber(), request
                                .getMethodName(), new MethodCallResult(null, exception), securityManager);
                    } else {
                        reply = new ReplyImpl(BodyImpl.this.getID(), request.getSequenceNumber(), request
                                .getMethodName(), new MethodCallResult(null, exception), securityManager);
                    }

                } else {
                    reply = request.serve(BodyImpl.this);
                }
            }

            if (!isProActiveInternalObject) {
                try {
                    if (isInImmediateService())
                        debugger.breakpoint(BreakpointType.EndImmediateService, request);
                    else
                        debugger.breakpoint(BreakpointType.EndService, request);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (reply == null) {
                if (!isActive()) {
                    return; // test if active in case of terminate() method
                    // otherwise eventProducer would be null
                }

                // JMX Notification
                if (!isProActiveInternalObject && (mbean != null)) {
                    String tagNotification = createTagNotification(request.getTags());
                    RequestNotificationData data = new RequestNotificationData(request.getSourceBodyID(),
                        request.getSenderNodeURL(), BodyImpl.this.bodyID, BodyImpl.this.nodeURL, request
                                .getMethodName(), getRequestQueue().size(), request.getSequenceNumber(),
                        tagNotification);
                    mbean.sendNotification(NotificationType.voidRequestServed, data);
                }

                // END JMX Notification
                return;
            }

            if (Profiling.TIMERS_COMPILED) {
                TimerWarehouse.startTimer(BodyImpl.this.bodyID, TimerWarehouse.SEND_REPLY);
            }

            // cruz: at this time, the reply has not been sent
            // But! ... when the reply.send() returns, the destinationBody.receiveReply(this)
            //   has already been called, and the reception notification has been issued.
            //   So, the JMX notification of ReplyReceived will have a greater timestamp than ReplySent.
            // JMX Notification
            if (!isProActiveInternalObject && (mbean != null) && reply.getResult().getResultObjet() != null
            		&& reply.getResult().getException() == null) {

                String tagNotification = createTagNotification(request.getTags());
                RequestNotificationData data = new RequestNotificationData(request.getSourceBodyID(), request
                        .getSenderNodeURL(), BodyImpl.this.bodyID, BodyImpl.this.nodeURL, request
                        .getMethodName(), getRequestQueue().size(), request.getSequenceNumber(),
                    tagNotification);
                mbean.sendNotification(NotificationType.replySent, data);
            }
            // END JMX Notification

            ArrayList<BodiesAndTags> destinations = new ArrayList<BodiesAndTags>();
            destinations.add(new BodiesAndTags(request.getSender(), request.getTags()));
            this.getFuturePool().registerDestinations(destinations);

            // Modify result object
            Object initialObject = null;
            Object stubOnActiveObject = null;
            Object modifiedObject = null;
            ObjectReplacer objectReplacer = null;
            if (CentralPAPropertyRepository.PA_IMPLICITGETSTUBONTHIS.isTrue()) {
                initialObject = reply.getResult().getResultObjet();
                try {
                    PAActiveObject.getStubOnThis();
                    stubOnActiveObject = (Object) MOP.createStubObject(BodyImpl.this.getReifiedObject()
                            .getClass().getName(), BodyImpl.this.getRemoteAdapter());
                    objectReplacer = new ObjectReferenceReplacer(BodyImpl.this.getReifiedObject(),
                        stubOnActiveObject);
                    modifiedObject = objectReplacer.replaceObject(initialObject);
                    reply.getResult().setResult(modifiedObject);
                } catch (InactiveBodyException e) {
                    e.printStackTrace();
                } catch (MOPException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            // FAULT-TOLERANCE
            if (BodyImpl.this.ftmanager != null) {
                BodyImpl.this.ftmanager.sendReply(reply, request.getSender());
            } else {
                // if the reply cannot be sent, try to sent the thrown exception
                // as result
                // Useful if the exception is due to the content of the result
                // (e.g. InvalidClassException)
                try {
                    reply.send(request.getSender());
                } catch (Throwable e1) {
                    // see PROACTIVE-1172
                    // previously only IOException were caught but now that new communication protocols
                    // can be added dynamically (remote objects) we can no longer suppose that only IOException
                    // will be thrown i.e. a runtime exception sent by the protocol can go through the stack and
                    // kill the service thread if not caught here.
                    // We do not want the AO to be killed if he cannot send the result.
                    try {
                        // trying to send the exception as result to fill the future.
                        // we want to inform the caller that the result cannot be set in
                        // the future for any reason. let's see if we can put the exception instead.
                        // works only if the exception is not due to a communication issue.
                        this.retrySendReplyWithException(reply, e1, request.getSender());
                    } catch (Throwable retryException1) {
                        // log the issue on the AO side for debugging purpose
                        // the initial exception must be the one to appear in the log.
                        sendReplyExceptionsLogger.error(shortString() + " : Failed to send reply to method:" +
                            request.getMethodName() + " sequence: " + request.getSequenceNumber() + " by " +
                            request.getSenderNodeURL() + "/" + request.getSender(), e1);
                    }
                }
            }

            if (Profiling.TIMERS_COMPILED) {
                TimerWarehouse.stopTimer(BodyImpl.this.bodyID, TimerWarehouse.SEND_REPLY);
            }

            this.getFuturePool().removeDestinations();

            // Restore Result Object
            if (CentralPAPropertyRepository.PA_IMPLICITGETSTUBONTHIS.isTrue() && (objectReplacer != null)) {
                try {
                    objectReplacer.restoreObject();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // If a reply sending has failed, try to send the exception as reply
        private void retrySendReplyWithException(Reply reply, Throwable e, UniversalBody destination)
                throws Exception {

            //            Get current request TAGs from current context
            //            Request currentreq = LocalBodyStore.getInstance().getContext().getCurrentRequest();
            //            MessageTags tags = null;
            //
            //            if (currentreq != null)
            //                tags = currentreq.getTags();

            Reply exceptionReply = new ReplyImpl(reply.getSourceBodyID(), reply.getSequenceNumber(), reply
                    .getMethodName(), new MethodCallResult(null, e), BodyImpl.this.securityManager/*, tags*/);
            exceptionReply.send(destination);
        }

        public void sendRequest(MethodCall methodCall, Future future, UniversalBody destinationBody)
                throws IOException, RenegotiateSessionException, CommunicationForbiddenException {
            long sequenceID = getNextSequenceID();

            // MessageTags tags = applyTags(sequenceID);
            MessageTags tags = applyTags(sequenceID, destinationBody, methodCall);

            Request request = this.internalRequestFactory.newRequest(methodCall, BodyImpl.this,
                    future == null, sequenceID, tags);

            // COMPONENTS : generate ComponentRequest for component messages
            if (methodCall.getComponentMetadata() != null) {
                request = new ComponentRequestImpl(request);
            }

            if (future != null) {
                future.setID(sequenceID);
                //TO DELETE: add the methodName to the future
                future.setMethodName(methodCall.getName());
                //TO DELETE: now I would need to add the name of the request that is currently being served
                //future.setParentMethodName(LocalBodyStore.getInstance().getContext().getCurrentRequest().getMethodName());
                //TO CONSERVER: adds the tags of the Request to the local Future.
                //This way it's possible to know which method is the Future waiting for, and generate
                //the notification when the final reply arrives.
                future.setTags(tags);
                // add the tags to the destinations stored 
                for(BodiesAndTags bt : FuturePool.getBodiesDestination()) {
                	if(bt.getBody().getID() == destinationBody.getID() ) {
                		bt.setTags(tags);
                	}
                }
                this.futures.receiveFuture(future);
            }

            // JMX Notification
            // TODO Write this section, after the commit of Arnaud
            // TODO Send a notification only if the destination doesn't
            // implement ProActiveInternalObject
            if (!isProActiveInternalObject && (mbean != null)) {
                // ServerConnector serverConnector = ProActiveRuntimeImpl.getProActiveRuntime()
                //        .getJMXServerConnector();

                // If the connector server is not active the connectorID can be
                // null
                // if ((serverConnector != null) && serverConnector.getConnectorServer().isActive()) {
                //    UniqueID connectorID = serverConnector.getUniqueID();

                //    if (!connectorID.equals(destinationBody.getID())) {
                String tagNotification = createTagNotification(tags);
                RequestNotificationData data = new RequestNotificationData(BodyImpl.this.bodyID,
                        BodyImpl.this.getNodeURL(), destinationBody.getID(), destinationBody.getNodeURL(),
                        methodCall.getName(), -1, request.getSequenceNumber(), tagNotification);
                mbean.sendNotification(request.getMethodCall().getReifiedMethod().getReturnType().equals(Void.TYPE) ?
                        NotificationType.voidRequestSent : NotificationType.requestSent, data);
                //    }
            	// }
                        //cruz: debug
                        if(BodyImpl.this instanceof ComponentBodyImpl) {
                        	PAComponent pac = ((ComponentBodyImpl)BodyImpl.this).getPAComponentImpl();
                        	ComponentParameters cp = pac.getComponentParameters();
                        	String componentName = cp.getName();
                        }
                        //--cruz

            }

            // END JMX Notification

            // FAULT TOLERANCE
            if (BodyImpl.this.ftmanager != null) {
                BodyImpl.this.ftmanager.sendRequest(request, destinationBody);
            } else {
                request.send(destinationBody);
            }
        }

        /**
         * Returns a unique identifier that can be used to tag a future, a request
         *
         * @return a unique identifier that can be used to tag a future, a request.
         */
        public synchronized long getNextSequenceID() {
            return BodyImpl.this.bodyID.hashCode() + ++this.absoluteSequenceID;
        }

        //
        // -- PROTECTED METHODS -----------------------------------------------
        //

        /**
         * Test if the MethodName of the request is "terminateAO" or "terminateAOImmediately". If
         * true, AbstractBody.terminate() is called
         *
         * @param request The request to serve
         * @return true if the name of the method is "terminateAO" or "terminateAOImmediately".
         */
        private boolean isTerminateAORequest(Request request) {
            boolean terminateRequest = (request.getMethodName()).startsWith("_terminateAO");

            if (terminateRequest) {
                terminate();
            }

            return terminateRequest;
        }

        /**
         * Propagate all tags attached to the current served request.
         *
         * @return The MessageTags for the propagation
         */
        private MessageTags applyTags(long sequenceID, UniversalBody destinationBody, MethodCall methodCall) {
            // apply the code of all message TAGs from current context
            Request currentreq = LocalBodyStore.getInstance().getContext().getCurrentRequest();
            MessageTags currentMessagetags = null;
            MessageTags nextTags = messageTagsFactory.newMessageTags();

            if (currentreq != null && (currentMessagetags = currentreq.getTags()) != null) {
                // there is a request with a MessageTags object in the current context
                for (Tag t : currentMessagetags.getTags()) {
                    Tag newTag = t.apply();
                    if (newTag != null)
                        nextTags.addTag(newTag);
                }
            }
            // Check the presence of the DSI Tag if enabled
            // Ohterwise add it
            if (CentralPAPropertyRepository.PA_TAG_DSF.isTrue()) {
                if (!nextTags.check(DsiTag.IDENTIFIER)) {
                    nextTags.addTag(new DsiTag(bodyID, sequenceID));
                }
            }
            
            // if the request is a Component request, propagate the tag accordingly
            if(currentreq != null) {
            	if(currentreq instanceof ComponentRequestImpl) {
            		String componentSourceName = "-";
            		String componentDestName = "-";
            		String interfaceDestName = "-";
            		String methodName = "-";
            		String interfaceSourceName = "-";
            		// ugly!
            		// This behaviour should be part of the Tag t.apply(), but how can the Tag have access to the BodyImpl ?
            		// Moreover, Component specific behaviour shouldn't be in ComponentBodyImpl, instead of here? 
            		// So, it wouldn't be necessary to do things like if(methodCall.getComponentMetadata != null) to know if it's a ComponentRequest
            		// (but that's like a "major" thing)

            		ComponentMethodCallMetadata cmcmd = methodCall.getComponentMetadata();
            		PAComponent pac = ((ComponentBodyImpl)BodyImpl.this).getPAComponentImpl();
            		
            		if(pac != null && cmcmd != null) {
            			ComponentParameters cp = pac.getComponentParameters();
            			componentSourceName = pac.getComponentParameters().getName();
            			interfaceDestName = cmcmd.getComponentInterfaceName();
            			if(cmcmd.getSenderItfID() != null) {
            				interfaceSourceName = cmcmd.getSenderItfID().getItfName();
            			}

            			//System.out.println("Sending request from ["+ componentSourceName +"]."+interfaceSourceName + " to destination interface "+ interfaceDestName);	
            			
            			// why does this return false????
            			// System.out.println("::::::::::"+cmcmd.isComponentMethodCall());
            			// if this component is generating a request, it should have a bound client interface,
            			// and should have BindingController, so it shouldn't throw a NoSuchInterfaceException here
            			BindingController bc = null;
            			
						// more ugliness ... name must not end with "-nf" 
						// avoid propagating calls through calls inside the membrane ... (I don't want to monitor that, and it generates errors)
            			if(!Utils.isControllerItfName(interfaceDestName) && !interfaceDestName.endsWith("-nf") ) {

            				try {
            					// Binding Controller shouldn't be null, because I'm in a component making a component request
            					bc = Fractal.getBindingController(pac);
            					
            					// tags propagation for singleton interface ... normal, as usual
            					if(Utils.isGCMSingletonItf(interfaceSourceName, pac)) {
            						//if(bc != null) {
            						componentDestName = ((PAComponentRepresentative)((PAInterface) bc.lookupFc(interfaceSourceName)).getFcItfOwner()).getComponentParameters().getName();
            						//System.out.println("Calling from ["+ componentSourceName +"."+interfaceSourceName+"] to ["+ componentDestName +"."+ interfaceDestName+"]");
            						//}
            					}

            					else if(Utils.isGCMMulticastItf(interfaceSourceName, pac)) {
            						
            						UniqueID destBodyID = destinationBody.getID();
            						PAMulticastController pamc = Utils.getPAMulticastController(pac);
            						Object[] bindedInterfaces = pamc.lookupGCMMulticast(interfaceSourceName);
            						for(Object bindedInterface : bindedInterfaces) {
            							UniqueID destinationMulticastBodyID = ((PAComponentRepresentative)((PAInterface) bindedInterface).getFcItfOwner()).getID();
            							if(destinationMulticastBodyID.equals(destBodyID)) {
            								componentDestName = ((PAComponentRepresentative)((PAInterface) bindedInterface).getFcItfOwner()).getComponentParameters().getName();
            							}
            						}
        							//System.out.println("MULTICAST call from ["+ componentSourceName + "."+interfaceSourceName+"] to ["+ componentDestName +"."+ interfaceDestName+"]");

            					}



            				} catch (NoSuchInterfaceException e) {
            					// FIXME I shouldn't add tags if the component is NF, I don't want to monitor them
            					// For now I will ignore them, but it should be solved in clean way (detecting before the fact that the component is NF)
            					System.out.println("Couldn't find interface [" + interfaceDestName + "] on component ["+ componentSourceName + "]");
            					//e.printStackTrace();

            				} catch (Exception e) {
            					System.out.println("FOUND NotNoSuchInterfaceException exception");
            					e.printStackTrace();
            				}						

            			}
            		}
            		methodName = methodCall.getName();

            		// Remove the current CMTag (if exists), and keep the ID of the previous, as it will be used for the new CMTag attached to this request
        			// Also keep the root ID
        			long oldSeqID = 0;
        			long rootID = 0;
            		if(nextTags.check(CMTag.IDENTIFIER)) {
            			CMTag oldTag = (CMTag) nextTags.removeTag(CMTag.IDENTIFIER);
            			//cruz debug
            			//if(componentSourceName.equals("componentB") || componentSourceName.equals("componentF")) {
            				//System.out.println("OldTags ["+ oldTag + "]");	
            			//}
            			//--cruz
            			oldSeqID = oldTag.getNewSeqID();
            			rootID = oldTag.getRootID();
            		}
            		// if there was no CMTag
            		else {
            			rootID = sequenceID;
            		}
            		
            		// Avoid the propagation of tag through NF requests.
            		// How to identify such a request? ... if componentDestName is "-". That happens if Utils.isControllerInterfaceName(interfaceName) OR interfaceName.endsWith("-nf")
            		// The tag has already been removed, so it's enough not to add it to stop the propagation
            		if(!componentDestName.equals("-")) {
                		// sequenceID is the new one, just generated (in sendRequest).
                		// needed to find the request that was being served, to be able to associate it in the callLog
            			nextTags.addTag(new CMTag(bodyID, oldSeqID, sequenceID, componentSourceName, componentDestName, interfaceDestName, methodName, rootID));            			
            		}
            		

            	}
            }

            return nextTags;
        }
    }

    // end inner class LocalBodyImpl
    private class InactiveLocalBodyStrategy implements LocalBodyStrategy, java.io.Serializable {
        // An inactive body strategy can have a futurepool if some ACs to do
        // remain after the termination of the active object
        private FuturePool futures;

        //
        // -- CONSTRUCTORS -----------------------------------------------
        //
        public InactiveLocalBodyStrategy() {

        }

        public InactiveLocalBodyStrategy(FuturePool remainingsACs) {
            this.futures = remainingsACs;
        }

        //
        // -- PUBLIC METHODS -----------------------------------------------
        //
        //
        // -- implements LocalBody
        // -----------------------------------------------
        //
        public FuturePool getFuturePool() {
            return this.futures;
        }

        public BlockingRequestQueue getRequestQueue() {
            throw new InactiveBodyException(BodyImpl.this);
        }

        public RequestQueue getHighPriorityRequestQueue() {
            throw new InactiveBodyException(BodyImpl.this);
        }

        public Object getReifiedObject() {
            throw new InactiveBodyException(BodyImpl.this);
        }

        public void serve(Request request) {
            throw new InactiveBodyException(BodyImpl.this, (request != null) ? request.getMethodName()
                    : "null request");
        }

        @Override
        public void serveWithException(Request request, Throwable exception) {
            throw new InactiveBodyException(BodyImpl.this, (request != null) ? request.getMethodName()
                    : "null request");
        }

        public void sendRequest(MethodCall methodCall, Future future, UniversalBody destinationBody)
                throws java.io.IOException {
            throw new InactiveBodyException(BodyImpl.this, destinationBody.getNodeURL(), destinationBody
                    .getID(), methodCall.getName());
        }

        /*
         * @see org.objectweb.proactive.core.body.LocalBodyStrategy#getNextSequenceID()
         */
        public long getNextSequenceID() {
            return 0;
        }
    }

    // end inner class InactiveBodyException
}
