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
package org.objectweb.proactive.core.component.control;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.etsi.uri.gcm.api.type.GCMInterfaceType;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.tags.MessageTags;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.collectiveitfs.MulticastBindingChecker;
import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;
import org.objectweb.proactive.core.component.group.PAComponentGroup;
import org.objectweb.proactive.core.component.group.ProxyForComponentInterfaceGroup;
import org.objectweb.proactive.core.component.representative.ItfID;
import org.objectweb.proactive.core.component.request.ComponentRequest;
import org.objectweb.proactive.core.component.type.PAComponentType;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactoryImpl;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatch;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.Proxy;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.util.SerializableMethod;


/**
 * Implementation of the {@link PAMulticastController multicast controller}.
 * 
 * @author The ProActive Team
 */
public class PAMulticastControllerImpl extends AbstractCollectiveInterfaceController implements
        PAMulticastController, Serializable, ControllerStateDuplication {
    private Map<String, PAInterface> multicastItfs = new HashMap<String, PAInterface>();
    private Map<String, Proxy> clientSideProxies = new HashMap<String, Proxy>();
    // Mapping between methods of client side and methods of server side
    // Map<clientSideItfName, Map<serverSideItfSignature, Map<clientSideMethod, serverSideMethod>>>
    private Map<String, Map<String, Map<SerializableMethod, SerializableMethod>>> matchingMethods = new HashMap<String, Map<String, Map<SerializableMethod, SerializableMethod>>>();

    /**
     * Creates a {@link PAMulticastControllerImpl}.
     * 
     * @param owner Component owning the controller.
     */
    public PAMulticastControllerImpl(final Component owner) {
        super(owner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initController() {
        // this method is called once the component is fully instantiated with all its interfaces created
        InterfaceType[] itfTypes = null;
        final ComponentType componentType = (ComponentType) owner.getFcType();
        if (componentType instanceof PAComponentType) {
            itfTypes = ((PAComponentType) componentType).getAllFcInterfaceTypes();
        } else {
            itfTypes = componentType.getFcInterfaceTypes();
        }

        for (final InterfaceType itfType : itfTypes) {
            final PAGCMInterfaceType type = (PAGCMInterfaceType) itfType;
            if (type.isGCMMulticastItf()) {
                try {
                    addClientSideProxy(type.getFcItfName(), (PAInterface) owner.getFcInterface(type
                            .getFcItfName()));
                } catch (final NoSuchInterfaceException e) {
                    throw new ProActiveRuntimeException(e);
                }
            }
        }
        final List<InterfaceType> interfaceTypes = Arrays.asList(itfTypes);
        final Iterator<InterfaceType> it = interfaceTypes.iterator();

        while (it.hasNext()) {
            // keep ref on interfaces of cardinality multicast
            addManagedInterface((PAGCMInterfaceType) it.next());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ensureGCMCompatibility(final InterfaceType itfType, final Interface itf) throws IllegalBindingException {
        try {
            Map<String, Map<SerializableMethod, SerializableMethod>> matchingMethodsForThisClientItf = matchingMethods
                    .get(itfType.getFcItfName());
            if (matchingMethodsForThisClientItf == null)
                matchingMethodsForThisClientItf = new HashMap<String, Map<SerializableMethod, SerializableMethod>>();

            final PAGCMInterfaceType serverSideItfType = (PAGCMInterfaceType) itf.getFcItfType();

            if (!matchingMethodsForThisClientItf.containsKey(serverSideItfType.getFcItfSignature())) {

                Class<?> clientSideItfClass;
                clientSideItfClass = Class.forName(itfType.getFcItfSignature());
                final Class<?> serverSideItfClass = Class.forName(serverSideItfType.getFcItfSignature());

                final Method[] clientSideItfMethods = clientSideItfClass.getMethods();
                final Method[] serverSideItfMethods = serverSideItfClass.getMethods();

                if (clientSideItfMethods.length != serverSideItfMethods.length) {
                    throw new IllegalBindingException("incompatible binding between client interface " +
                        itfType.getFcItfName() + " (" + itfType.getFcItfSignature() +
                        ")  and server interface " + serverSideItfType.getFcItfName() + " (" +
                        serverSideItfType.getFcItfSignature() +
                        ") : there is not the same number of methods (including those inherited) in both interfaces !");
                }

                final Map<SerializableMethod, SerializableMethod> matchingMethodsForThisServerItf = new HashMap<SerializableMethod, SerializableMethod>(
                    clientSideItfMethods.length);

                for (final Method method : clientSideItfMethods) {
                    final Method serverSideMatchingMethod = searchMatchingMethod(method, serverSideItfMethods,
                            ((GCMInterfaceType) itfType).isGCMMulticastItf(), serverSideItfType
                                    .isGCMGathercastItf(), (PAInterface) itf);
                    if (serverSideMatchingMethod == null) {
                        throw new IllegalBindingException("binding incompatibility between " +
                            itfType.getFcItfName() + " (" + itfType.getFcItfSignature() + ") and " +
                            serverSideItfType.getFcItfName() + " (" + serverSideItfType.getFcItfSignature() +
                            ") interfaces : cannot find matching method");
                    }
                    matchingMethodsForThisServerItf.put(new SerializableMethod(method),
                            new SerializableMethod(serverSideMatchingMethod));
                }

                matchingMethodsForThisClientItf.put(serverSideItfType.getFcItfSignature(),
                        matchingMethodsForThisServerItf);
                matchingMethods.put(itfType.getFcItfName(), matchingMethodsForThisClientItf);
            }
        } catch (final ClassNotFoundException e) {
            final IllegalBindingException ibe = new IllegalBindingException(
                "cannot find class corresponding to given signature " + e.getMessage());
            ibe.initCause(e);
            throw ibe;
        }
    }

    /*
     * @seeorg.objectweb.proactive.core.component.control.AbstractCollectiveInterfaceController#
     * searchMatchingMethod(java.lang.reflect.Method, java.lang.reflect.Method[])
     */
    @Override
    protected Method searchMatchingMethod(final Method clientSideMethod, final Method[] serverSideMethods,
            final boolean clientItfIsMulticast, final boolean serverItfIsGathercast, final PAInterface serverSideItf) {
        try {
            return MulticastBindingChecker.searchMatchingMethod(clientSideMethod, serverSideMethods,
                    serverItfIsGathercast, serverSideItf);
        } catch (final ParameterDispatchException e) {
            e.printStackTrace();
            return null;
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setControllerItfType() {
        try {
            setItfType(PAGCMTypeFactoryImpl.instance().createFcItfType(Constants.MULTICAST_CONTROLLER,
                    PAMulticastController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                    TypeFactory.SINGLE));
        } catch (final InstantiationException e) {
            throw new ProActiveRuntimeException("cannot create controller type for controller " +
                this.getClass().getName());
        }
    }

    private boolean addManagedInterface(final PAGCMInterfaceType itfType) {
        if (!itfType.isGCMMulticastItf()) {
            return false;
        }
        if (multicastItfs.containsKey(itfType.getFcItfName())) {
            //            logger.error("the interface named " + itfType.getFcItfName() +
            //                " is already managed by the collective interfaces controller");
            return false;
        }

        try {
            final PAInterface multicastItf = (PAInterface) owner.getFcInterface(itfType.getFcItfName());
            multicastItfs.put(itfType.getFcItfName(), multicastItf);
        } catch (final NoSuchInterfaceException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    protected Group<PAInterface> getDelegatee(final String clientItfName) {
        final ProxyForComponentInterfaceGroup clientSideProxy = (ProxyForComponentInterfaceGroup) clientSideProxies
                .get(clientItfName);
        return clientSideProxy.getDelegatee();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindGCMMulticast(final String multicastItfName, final Object serverItf) {
        try {
            // bindFcMulticast is just a renaming of the bindFc method in the BindingController
            // this avoid to rewrite similar code
            // the specific part is in the bindFc method in this class
            GCM.getBindingController(owner).bindFc(multicastItfName, serverItf);
        } catch (final NoSuchInterfaceException e) {
            controllerLogger.warn("No such interface: " + multicastItfName, e);
        } catch (final IllegalBindingException e) {
            controllerLogger.warn("Illegal binding between " + multicastItfName + " and " +
                ((Interface) serverItf).getFcItfName(), e);
        } catch (final IllegalLifeCycleException e) {
            controllerLogger.warn("Illegal life cycle component for binding " + multicastItfName + " and " +
                ((Interface) serverItf).getFcItfName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindGCMMulticast(final String multicastItfName, final Object serverItf) throws NoSuchInterfaceException {
        if (multicastItfs.containsKey(multicastItfName)) {
            final Group<PAInterface> g = getDelegatee(multicastItfName);
            //PAInterface itf = multicastItfs.get(clientItfName);
            //Group<PAInterface> g = PAGroup.getGroup(itf);
            if (g.remove(serverItf)) {
                controllerLogger.debug("removed connected interface from multicast interface : " +
                    multicastItfName);
            } else {
                controllerLogger.error("cannot remove connected interface from multicast interface : " +
                    multicastItfName);
            }
        } else {
            throw new NoSuchInterfaceException("No such interface: " + multicastItfName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] lookupGCMMulticast(final String multicastItfName) throws NoSuchInterfaceException {
        if (multicastItfs.containsKey(multicastItfName)) {
            final ProxyForComponentInterfaceGroup delegatee = ((ProxyForComponentInterfaceGroup) ((PAInterface) multicastItfs
                    .get(multicastItfName).getFcItfImpl()).getProxy()).getDelegatee();
            Object[] bindedServerItf;
            if (delegatee != null) {
                bindedServerItf = delegatee.toArray();
            } else {
                bindedServerItf = new Object[0];
            }
            return bindedServerItf;
        } else {
            throw new NoSuchInterfaceException("No such interface: " + multicastItfName);
        }
    }

    @Override
		public List<MethodCall> generateMethodCallsForMulticastDelegatee(final MethodCall mc,
            final ProxyForComponentInterfaceGroup delegatee) throws ParameterDispatchException {
        // read from annotations
        final Object[] clientSideEffectiveArguments = mc.getEffectiveArguments();

        final PAGCMInterfaceType itfType = (PAGCMInterfaceType) multicastItfs.get(
                mc.getComponentMetadata().getComponentInterfaceName()).getFcItfType();

        Method matchingMethodInClientInterface; // client itf as well as parent interfaces

        try {
            matchingMethodInClientInterface = Class.forName(itfType.getFcItfSignature()).getMethod(
                    mc.getReifiedMethod().getName(), mc.getReifiedMethod().getParameterTypes());
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ParameterDispatchException(e.fillInStackTrace());
        }

        final Class<?>[] clientSideParamTypes = matchingMethodInClientInterface.getParameterTypes();
        final ParamDispatch[] clientSideParamDispatchModes = MulticastBindingChecker
                .getDispatchModes(matchingMethodInClientInterface);

        final List<List<Object>> dispatchedParameters = new ArrayList<List<Object>>();

        int expectedMethodCallsNb = 0;

        // compute dispatch sizes for annotated parameters
        final Vector<Integer> dispatchSizes = new Vector<Integer>();

        for (int i = 0; i < clientSideParamTypes.length; i++) {
            dispatchSizes.addElement(clientSideParamDispatchModes[i].expectedDispatchSize(
                    clientSideEffectiveArguments[i], delegatee.size()));
        }

        // -1 mean there are no suggested max dispatch size
        int max = -1;
        if (dispatchSizes.size() > 0) {
            max = dispatchSizes.get(0);
        }

        for (int i = 1; i < dispatchSizes.size(); i++) {
            if (dispatchSizes.get(i) > max) {
                max = dispatchSizes.get(i);
            }
        }
        if (max == -1) {
            max = delegatee.size();
        }
        for (int i = 0; i < dispatchSizes.size(); i++) {
            if (dispatchSizes.get(i) == -1) {
                dispatchSizes.set(i, max);
            }
        }

        if (dispatchSizes.size() > 0) {
            // ok, found some annotated elements
            expectedMethodCallsNb = dispatchSizes.get(0);

            for (int i = 1; i < dispatchSizes.size(); i++) {
                if (dispatchSizes.get(i).intValue() != expectedMethodCallsNb) {
                    throw new ParameterDispatchException(
                        "cannot generate invocation for multicast interface " + itfType.getFcItfName() +
                            " because the specified distribution of parameters is incorrect in method " +
                            matchingMethodInClientInterface.getName() + "(expect " +
                            dispatchSizes.get(i).intValue() + " method calls for the " + i +
                            "th parameter instead of " + expectedMethodCallsNb + ")");
                }
            }
        } else {
            // broadcast to every member of the group
            expectedMethodCallsNb = delegatee.size();
        }

        // get distributed parameters
        for (int i = 0; i < clientSideParamTypes.length; i++) {
            final List<Object> dispatchedParameter = clientSideParamDispatchModes[i].partition(
                    clientSideEffectiveArguments[i], expectedMethodCallsNb);
            //delegatee.size());
            dispatchedParameters.add(dispatchedParameter);
        }

        final List<MethodCall> result = new ArrayList<MethodCall>(expectedMethodCallsNb);

        // need to find matching method in server interface
        try {
            //            if (matchingMethods.get(mc.getComponentMetadata()
            //                                          .getComponentInterfaceName()) == null) {
            //                System.out.println("########## \n" +
            //                    matchingMethods.toString());
            //            }

            // now we have all dispatched parameters
            // proceed to generation of method calls
            // first, generate indexes
            //            List<Integer> indexesOfGeneratedMethodCalls = new LinkedList<Integer>();
            //			for (int i = 0; i < expectedMethodCallsNb; i++) {
            //				indexesOfGeneratedMethodCalls.add(i);
            //			}
            //
            //			// if dispatch mode is random, randomize the affectation of workers
            //			if (MulticastHelper.dynamicDispatch(mc)) {
            //			Annotation[] annotations = mc.getReifiedMethod().getAnnotations();
            //			int groupDispatchAnnotationIndex = Arrays.binarySearch(annotations,
            //					MethodDispatchMetadata.class);
            //			if (groupDispatchAnnotationIndex >= 0) {
            //				ParamDispatchMetadata pdm = ((MethodDispatchMetadata) annotations[groupDispatchAnnotationIndex])
            //						.mode();
            //				if (pdm.mode().equals(ParamDispatchMode.RANDOM)) {
            //					Collections.shuffle(indexes);
            //				}
            //			}

            for (int generatedMethodCallIndex = 0; generatedMethodCallIndex < expectedMethodCallsNb; generatedMethodCallIndex++) {
                final Method matchingMethodInServerInterface = matchingMethods.get(
                        mc.getComponentMetadata().getComponentInterfaceName()).get(
                        ((InterfaceType) ((PAInterface) delegatee.get(generatedMethodCallIndex %
                            delegatee.size())).getFcItfType()).getFcItfSignature()).get(
                        new SerializableMethod(mc.getReifiedMethod())).getMethod();
                final Object[] individualEffectiveArguments = new Object[matchingMethodInServerInterface
                        .getParameterTypes().length];

                for (int parameterIndex = 0; parameterIndex < individualEffectiveArguments.length; parameterIndex++) {
                    individualEffectiveArguments[parameterIndex] = dispatchedParameters.get(parameterIndex)
                            .get(generatedMethodCallIndex); // initialize
                }

                //cruz : What if I want a componentCall ?
                final String sourceInterfaceName = mc.getComponentMetadata().getComponentInterfaceName();
                final String destinationInterfaceName = ((PAInterface) delegatee.get(generatedMethodCallIndex % delegatee.size())).getFcItfName();
                final ItfID senderID = null; //new ItfID(sourceInterfaceName, owner.getID());
                MessageTags currentTags = null;
                final Request currentRequest = LocalBodyStore.getInstance().getContext().getCurrentRequest();
                if(currentRequest != null) {
                	currentTags = currentRequest.getTags();
                }
                final MethodCall mcMethodCall = MethodCall.getComponentMethodCall(matchingMethodInServerInterface, 
                		individualEffectiveArguments, mc.getGenericTypesMapping(),  
                		destinationInterfaceName, senderID, ComponentRequest.STRICT_FIFO_PRIORITY); //, currentTags, currentRequest );
                
                result.add(mcMethodCall);

//                System.out.println("[PAMulticastControllerImpl.generateMethodsCallsForMulticastDelegatee] Src:     "+ sourceInterfaceName);
//                System.out.println("                                                                      SrcName: "+ owner.getComponentParameters().getName());
//                System.out.println("                                                                      SrcID:   "+ owner.getID());
//                System.out.println("                                                                      Dest:    "+ destinationInterfaceName);
//                if(currentTags != null) {
//                	System.out.println("                                                                      Tags:    "+ currentTags.getTag(CMTag.IDENTIFIER));
//                }
                //--cruz

                // no need for a "component" method call
                // result.add(MethodCall.getMethodCall(matchingMethodInServerInterface, mc
                //        .getGenericTypesMapping(), individualEffectiveArguments, mc.getExceptionContext()));
                //                      generatedMethodCallIndex % delegatee.size()); // previous workaround deemed unecessary with new initialization of result group
                // default is to do some round robin when nbGeneratedMethodCalls > nbReceivers
            }
        } catch (final SecurityException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
		public int allocateServerIndex(final MethodCall mc, final int partitioningIndex, final int nbConnectedServerInterfaces) {
        // preserve index defined during partitioning operation
        return partitioningIndex;
        //		use this method somewhere!
    }

    protected void bindFc(final String clientItfName, final PAInterface serverItf) {
        if (controllerLogger.isDebugEnabled()) {
            try {
                if (!PAGroup.isGroup(serverItf.getFcItfOwner())) {
                    controllerLogger.debug("multicast binding : " + clientItfName + " to : " +
                        GCM.getNameController(serverItf.getFcItfOwner()).getFcName() + "." +
                        serverItf.getFcItfName());
                }
            } catch (final NoSuchInterfaceException e) {
                e.printStackTrace();
            }
        }
        if (multicastItfs.containsKey(clientItfName)) {
            try {
                final ProxyForComponentInterfaceGroup clientSideProxy = (ProxyForComponentInterfaceGroup) clientSideProxies
                        .get(clientItfName);

                if (clientSideProxy.getDelegatee() == null) {
                    final PAInterface groupItf = PAComponentGroup.newComponentInterfaceGroup(
                            (PAGCMInterfaceType) serverItf.getFcItfType(), owner);
                    final ProxyForComponentInterfaceGroup proxy = (ProxyForComponentInterfaceGroup) ((StubObject) groupItf)
                            .getProxy();
                    clientSideProxy.setDelegatee(proxy);
                }

                ((Group<PAInterface>) clientSideProxy.getDelegatee()).add(serverItf);
            } catch (final ClassNotReifiableException e) {
                e.printStackTrace();
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBoundTo(final String multicastItfName, final Object[] serverItfs) throws NoSuchInterfaceException {
        if (clientSideProxies.containsKey(multicastItfName)) {
            final ProxyForComponentInterfaceGroup clientSideProxy = (ProxyForComponentInterfaceGroup) clientSideProxies
                    .get(multicastItfName);
            for (final Object serverItf : serverItfs) {
                final Interface curServerItf = (Interface) serverItf;
                if (clientSideProxy.getDelegatee() != null) {
                	if (((Group<PAInterface>) clientSideProxy.getDelegatee()).contains(curServerItf))
                    return true;
                }
            }
            return false;
        } else {
            throw new NoSuchInterfaceException("No such interface: " + multicastItfName);
        }
    }

    private boolean hasClientSideProxy(final String itfName) {
        return clientSideProxies.containsKey(itfName);
    }

    private void addClientSideProxy(final String itfName, final PAInterface itf) {
        final Proxy proxy = ((PAInterface) itf.getFcItfImpl()).getProxy();

        if (!(proxy instanceof Group)) {
            throw new ProActiveRuntimeException(
                "client side proxies for multicast interfaces must be Group instances");
        }

        clientSideProxies.put(itfName, proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void duplicateController(final Object c) {
        if (c instanceof MulticastItfState) {

            final MulticastItfState state = (MulticastItfState) c;
            clientSideProxies = state.getClientSideProxies();
            matchingMethods = state.getMatchingMethods();
            multicastItfs = state.getMulticastItfs();
        } else {
            throw new ProActiveRuntimeException(
                "MulticastControllerImpl : Impossible to duplicate the controller " + this +
                    " from the controller" + c);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerState getState() {

        return new ControllerState(new MulticastItfState((HashMap) clientSideProxies,
            (HashMap<String, PAInterface>) multicastItfs,
            (HashMap<String, Map<String, Map<SerializableMethod, SerializableMethod>>>) matchingMethods));
    }

    class MulticastItfState implements Serializable {
        private HashMap clientSideProxies;
        private HashMap<String, PAInterface> multicastItfs;
        private HashMap<String, Map<String, Map<SerializableMethod, SerializableMethod>>> matchingMethods;

        public MulticastItfState(final HashMap clientSideProxies, final HashMap<String, PAInterface> multicastItfs,
                final HashMap<String, Map<String, Map<SerializableMethod, SerializableMethod>>> matchingMethods) {

            this.clientSideProxies = clientSideProxies;
            this.multicastItfs = multicastItfs;
            this.matchingMethods = matchingMethods;
        }

        public HashMap getClientSideProxies() {
            return clientSideProxies;
        }

        public void setClientSideProxies(final HashMap clientSideProxies) {
            this.clientSideProxies = clientSideProxies;
        }

        public HashMap<String, Map<String, Map<SerializableMethod, SerializableMethod>>> getMatchingMethods() {
            return matchingMethods;
        }

        public void setMatchingMethods(
                final HashMap<String, Map<String, Map<SerializableMethod, SerializableMethod>>> matchingMethods) {
            this.matchingMethods = matchingMethods;
        }

        public HashMap<String, PAInterface> getMulticastItfs() {
            return multicastItfs;
        }

        public void setMulticastItfs(final HashMap<String, PAInterface> multicastItfs) {
            this.multicastItfs = multicastItfs;
        }
    }
}
