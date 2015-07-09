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
import java.util.List;

import org.etsi.uri.gcm.api.control.GCMLifeCycleController;
import org.etsi.uri.gcm.api.type.GCMInterfaceType;
import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.AttributeController;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.body.ActiveBody;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.identity.PAComponentImpl;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactoryImpl;


/**
 * Implementation of the {@link PAGCMLifeCycleController life cycle controller}.
 *
 * @author The ProActive Team
 * @see PAGCMLifeCycleController
 */
public class PAGCMLifeCycleControllerImpl extends AbstractPAController implements PAGCMLifeCycleController,
        Serializable, ControllerStateDuplication {
    protected String fcState = LifeCycleController.STOPPED;

    /**
     * Creates a {@link PAGCMLifeCycleControllerImpl}.
     * 
     * @param owner Component owning the controller.
     */
    public PAGCMLifeCycleControllerImpl(final Component owner) {
        super(owner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setControllerItfType() {
        try {
            setItfType(PAGCMTypeFactoryImpl.instance().createFcItfType(Constants.LIFECYCLE_CONTROLLER,
                    PAGCMLifeCycleController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                    TypeFactory.SINGLE));
        } catch (final InstantiationException e) {
            throw new ProActiveRuntimeException("cannot create controller " + this.getClass().getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFcState() {
        return fcState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    /*
     * Recursive if composite (recursivity is allowed here as we do not implement sharing )
     */
    public void startFc() throws IllegalLifeCycleException {
        try {
            //check that all mandatory client interfaces are bound
            final InterfaceType[] itfTypes = ((ComponentType) getFcItfOwner().getFcType()).getFcInterfaceTypes();

            for (int i = 0; i < itfTypes.length; i++) {
                if (itfTypes[i].isFcClientItf() && !itfTypes[i].isFcOptionalItf() ||
                    isComposite() && !itfTypes[i].isFcClientItf() && !itfTypes[i].isFcOptionalItf() && !AttributeController.class
                            .isAssignableFrom(Class.forName(itfTypes[i].getFcItfSignature()))) {
                    if (itfTypes[i].isFcCollectionItf()) {
                        // look for collection members
                        final Object[] itfs = owner.getFcInterfaces();
                        for (final Object itf2 : itfs) {
                            final Interface itf = (Interface) itf2;
                            if (itf.getFcItfName().startsWith(itfTypes[i].getFcItfName())) {
                                if (itf.getFcItfName().equals(itfTypes[i].getFcItfName())) {
                                    throw new IllegalLifeCycleException(
                                        "invalid collection interface name at runtime (suffix required)");
                                }
                                if (GCM.getBindingController(owner).lookupFc(itf.getFcItfName()) == null) {
                                    if (itfTypes[i].isFcClientItf()) {
                                        throw new IllegalLifeCycleException(
                                            "compulsory collection client interface " +
                                                itfTypes[i].getFcItfName() + " in component " +
                                                GCM.getNameController(getFcItfOwner()).getFcName() +
                                                " is not bound.");
                                    } else { // itfTypes[i] is a server interface of a composite
                                        throw new IllegalLifeCycleException(
                                            "compulsory collection server interface " +
                                                itfTypes[i].getFcItfName() + " in composite component " +
                                                GCM.getNameController(getFcItfOwner()).getFcName() +
                                                " is not bound to any sub component.");
                                    }
                                }
                            }
                        }
                    } else if (((GCMInterfaceType) itfTypes[i]).isGCMMulticastItf() &&
                        !itfTypes[i].isFcOptionalItf()) {
                        final Object[] bindedServerItf = GCM.getMulticastController(getFcItfOwner())
                                .lookupGCMMulticast(itfTypes[i].getFcItfName());
                        if (bindedServerItf == null || bindedServerItf.length == 0) {
                            if (itfTypes[i].isFcClientItf()) {
                                throw new IllegalLifeCycleException("compulsory multicast client interface " +
                                    itfTypes[i].getFcItfName() + " in component " +
                                    GCM.getNameController(getFcItfOwner()).getFcName() + " is not bound.");
                            } else { // itfTypes[i] is a server interface of a composite
                                throw new IllegalLifeCycleException("compulsory multicast server interface " +
                                    itfTypes[i].getFcItfName() + " in composite component " +
                                    GCM.getNameController(getFcItfOwner()).getFcName() +
                                    " is not bound to any sub component.");
                            }
                        }
                    } else if ((((GCMInterfaceType) itfTypes[i]).getGCMCardinality().equals(
                            GCMTypeFactory.SINGLETON_CARDINALITY) || ((GCMInterfaceType) itfTypes[i])
                            .getGCMCardinality().equals(GCMTypeFactory.GATHERCAST_CARDINALITY)) &&
                        GCM.getBindingController(getFcItfOwner()).lookupFc(itfTypes[i].getFcItfName()) == null) {
                        if (itfTypes[i].isFcClientItf()) {
                            throw new IllegalLifeCycleException("compulsory client interface " +
                                itfTypes[i].getFcItfName() + " in component " +
                                GCM.getNameController(getFcItfOwner()).getFcName() + " is not bound.");
                        } else { // itfTypes[i] is a server interface of a composite
                            throw new IllegalLifeCycleException("compulsory server interface " +
                                itfTypes[i].getFcItfName() + " in composite component " +
                                GCM.getNameController(getFcItfOwner()).getFcName() +
                                " is not bound to any sub component.");
                        }
                    }

                    // tests for internal client interface of composite component
                    if (isComposite() && itfTypes[i].isFcClientItf() && !itfTypes[i].isFcOptionalItf()) {
                        if (itfTypes[i].isFcCollectionItf()) {
                            // TODO Check binding
                        } else if (((GCMInterfaceType) itfTypes[i]).getGCMCardinality().equals(
                                GCMTypeFactory.GATHERCAST_CARDINALITY)) {
                            final List<Object> connectedClientItfs = GCM.getGathercastController(getFcItfOwner())
                                    .getGCMConnectedClients(itfTypes[i].getFcItfName());
                            if (connectedClientItfs == null || connectedClientItfs.isEmpty()) {
                                throw new IllegalLifeCycleException(
                                    "compulsory gathercast client interface " + itfTypes[i].getFcItfName() +
                                        " in composite component " +
                                        GCM.getNameController(getFcItfOwner()).getFcName() +
                                        " is not bound to any sub component.");
                            }
                        } else { // client interface is a single or multicast interface
                            boolean isBound = false;
                            final Component[] subComponents = GCM.getContentController(getFcItfOwner())
                                    .getFcSubComponents();
                            for (int j = 0; j < subComponents.length && !isBound; j++) {
                                try {
                                    final String[] subComponentItfs = Fractal
                                            .getBindingController(subComponents[j]).listFc();
                                    for (final String subComponentItf : subComponentItfs) {
                                        if (((GCMInterfaceType) ((Interface) subComponents[j]
                                                .getFcInterface(subComponentItf)).getFcItfType())
                                                .isGCMMulticastItf()) {
                                            final Object[] serverItfs = GCM
                                                    .getMulticastController(subComponents[j])
                                                    .lookupGCMMulticast(subComponentItf);
                                            if (serverItfs != null && serverItfs.length != 0) {
                                                for (final Object serverItf : serverItfs) {
                                                    if (((PAComponent) ((Interface) serverItf)
                                                            .getFcItfOwner()).getID().equals(owner.getID())) {
                                                        isBound = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        } else {
                                            Object subComponentItfImpl = null;
                                            try {
                                                subComponentItfImpl = GCM.getBindingController(
                                                        subComponents[j]).lookupFc(subComponentItf);
                                            } catch (final NoSuchInterfaceException nsie) {
                                                // should never happen
                                                controllerLogger.error("Interface " + subComponentItf +
                                                    " in component " + subComponents[j] + " does not exist",
                                                        nsie);
                                            }
                                            if (subComponentItfImpl != null) {
                                                if (((PAComponent) ((PAInterface) subComponentItfImpl)
                                                        .getFcItfOwner()).getID().equals(owner.getID())) {
                                                    isBound = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                } catch (final NoSuchInterfaceException nsie) {
                                    // sub component does not have a binding controller
                                }
                            }
                            if (!isBound) {
                                throw new IllegalLifeCycleException("compulsory client interface " +
                                    itfTypes[i].getFcItfName() + " in composite component " +
                                    GCM.getNameController(getFcItfOwner()).getFcName() +
                                    " is not bound to any sub component.");
                            }
                        }
                    }
                }
            }

            //try {
            //   PAInterface it = (PAInterface) Fractive.getMembraneController(getFcItfOwner());
            //   Object obj = it.getFcItfImpl();
            //   ((PAMembraneControllerImpl) obj).checkInternalInterfaces();
            //} catch (NoSuchInterfaceException nsie) {
            //Nothing to do, if the component does not have any membrane controller
            //}

            final String hierarchical_type = owner.getComponentParameters().getHierarchicalType();
            if (hierarchical_type.equals(Constants.COMPOSITE)) {
                // start all inner components
                final Component[] inner_components = GCM.getContentController(getFcItfOwner()).getFcSubComponents();
                if (inner_components != null) {
                    for (final Component inner_component : inner_components) {
                        try {
                            if (Utils.getPAMembraneController(inner_component).getMembraneState().equals(
                                    PAMembraneController.MEMBRANE_STOPPED)) {
                                throw new IllegalLifeCycleException(
                                    "Before starting all subcomponents, make sure that the membrane of all of them is started");
                            }
                        } catch (final NoSuchInterfaceException e) {
                            //the subComponent doesn't have a PAMembraneController, no need to check what's is in the previous try block
                        }
                        GCM.getGCMLifeCycleController(inner_component).startFc();
                    }
                }
            } else {
                // primitive component: check if the implementation class implements LifeCycleController
                if (owner.getReferenceOnBaseObject() instanceof LifeCycleController) {
                    ((LifeCycleController) owner.getReferenceOnBaseObject()).startFc();
                }
            }

            //getRequestQueue().start();
            fcState = LifeCycleController.STARTED;
            if (controllerLogger.isDebugEnabled()) {
                controllerLogger.debug("started " + GCM.getNameController(owner).getFcName());
            }
        } catch (final ClassNotFoundException cnfe) {
            controllerLogger.error("class not found : " + cnfe.getMessage(), cnfe);
        } catch (final NoSuchInterfaceException nsie) {
            controllerLogger.error("interface not found : " + nsie.getMessage(), nsie);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopFc() {
        try {
            final String hierarchical_type = owner.getComponentParameters().getHierarchicalType();
            if (hierarchical_type.equals(Constants.COMPOSITE)) {
                // stop all inner components
                final Component[] inner_components = GCM.getContentController(getFcItfOwner()).getFcSubComponents();
                if (inner_components != null) {
                    for (final Component inner_component : inner_components) {
                        try {
                            if (Utils.getPAMembraneController(inner_component).getMembraneState().equals(
                                    PAMembraneController.MEMBRANE_STOPPED)) {
                                throw new IllegalLifeCycleException(
                                    "Before stopping all subcomponents, make sure that the membrane of all them is started");
                            }
                        } catch (final NoSuchInterfaceException e) {
                            //the subComponent doesn't have a PAMembraneController, no need to check what's is in the previous try block
                        }
                        GCM.getGCMLifeCycleController(inner_component).stopFc();
                    }
                }
            } else {
                // primitive component: check if the implementation class implements LifeCycleController
                if (owner.getReferenceOnBaseObject() instanceof LifeCycleController) {
                    ((LifeCycleController) owner.getReferenceOnBaseObject()).stopFc();
                }
            }

            //getRequestQueue().stop();
            fcState = LifeCycleController.STOPPED;
            if (controllerLogger.isDebugEnabled()) {
                controllerLogger.debug("stopped" + GCM.getNameController(owner).getFcName());
            }
        } catch (final NoSuchInterfaceException nsie) {
            controllerLogger.error("interface not found : " + nsie.getMessage());
        } catch (final IllegalLifeCycleException ilce) {
            controllerLogger.error("illegal life cycle operation : " + ilce.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminateGCMComponent() throws IllegalLifeCycleException {
        if (fcState.equals(LifeCycleController.STOPPED)) {
            final String hierarchical_type = owner.getComponentParameters().getHierarchicalType();
            if (hierarchical_type.equals(Constants.PRIMITIVE)) {
                // primitive component: check if the implementation class implements GCMLifeCycleController
                if (owner.getReferenceOnBaseObject() instanceof GCMLifeCycleController) {
                    ((GCMLifeCycleController) owner.getReferenceOnBaseObject()).terminateGCMComponent();
                }
            }
            PAActiveObject.terminateActiveObject(true);
        } else {
            throw new IllegalLifeCycleException(
                "Cannot terminate component because the component is not stopped");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminateGCMComponent(final boolean immediate) throws IllegalLifeCycleException {
        if (fcState.equals(LifeCycleController.STOPPED)) {
            final String hierarchical_type = owner.getComponentParameters().getHierarchicalType();
            if (hierarchical_type.equals(Constants.PRIMITIVE)) {
                // primitive component: check if the implementation class implements PAGCMLifeCycleController
                if (owner.getReferenceOnBaseObject() instanceof PAGCMLifeCycleController) {
                    ((PAGCMLifeCycleController) owner.getReferenceOnBaseObject())
                            .terminateGCMComponent(immediate);
                }
            }
            PAActiveObject.terminateActiveObject(immediate);
        } else {
            throw new IllegalLifeCycleException(
                "Cannot terminate component because the component is not stopped");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void duplicateController(final Object c) {
        if (c instanceof String) {
            fcState = (String) c;

        } else {
            throw new ProActiveRuntimeException(
                "PAGCMLifeCycleControllerImpl: Impossible to duplicate the controller " + this +
                    " from the controller" + c);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerState getState() {
        return new ControllerState(fcState);
    }
}
