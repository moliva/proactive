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
package org.objectweb.proactive.core.component.type;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.Type;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.webservices.WSInfo;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * Fictive component used as owner for the proxy generated to bind the client interface
 * of a component to a web service.
 * <br>
 * This component implements the {@link Serializable}, the {@link Component}, the
 * {@link NameController} and the {@link LifeCycleController} interfaces.
 * <br>
 * This component is always in the started state and cannot be stopped.
 *
 * @author The ProActive Team
 * @see Serializable
 * @see Component
 * @see NameController
 * @see LifeCycleController
 */
@PublicAPI
public class WSComponent implements Serializable, Component, NameController, LifeCycleController {
    protected static final transient Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS);

    /**
     * Functional interface implementation.
     */
    private Object fcInterfaceImpl;

    /**
     * Web service informations.
     */
    private WSInfo wsInfo;

    /**
     * Constructor specifying the {@link WSInfo} corresponding to the web service to bind to.
     *
     * @param wsInfo {@link WSInfo} corresponding to the web service to bind to.
     */
    public WSComponent(WSInfo wsInfo) {
        this.wsInfo = wsInfo;
    }

    /**
     * Getter for the functional interface implementation corresponding to the generated proxy.
     *
     * @return Functional interface implementation corresponding to the generated proxy.
     */
    public Object getFcInterfaceImpl() {
        return fcInterfaceImpl;
    }

    /**
     * Setter for the functional interface implementation corresponding to the generated proxy.
     *
     * @param fcInterfaceImpl Functional interface implementation corresponding to the generated proxy.
     */
    public void setFcInterfaceImpl(Object fcInterfaceImpl) {
        this.fcInterfaceImpl = fcInterfaceImpl;
    }

    /**
     * Getter for the {@link WSInfo} corresponding to the web service to bind to.
     *
     * @return {@link WSInfo} corresponding to the web service to bind to.
     */
    public WSInfo getWSInfo() {
        return wsInfo;
    }

    public Object getFcInterface(String interfaceName) throws NoSuchInterfaceException {
        if (interfaceName.equals(Constants.COMPONENT)) {
            return this;
        }
        if (interfaceName.equals(Constants.NAME_CONTROLLER)) {
            return this;
        }
        if (interfaceName.equals(Constants.LIFECYCLE_CONTROLLER)) {
            return this;
        }
        if (interfaceName.equals(((Interface) fcInterfaceImpl).getFcItfName())) {
            return fcInterfaceImpl;
        }
        throw new NoSuchInterfaceException(interfaceName);
    }

    public Object[] getFcInterfaces() {
        return new Object[] { this, this, fcInterfaceImpl };
    }

    public Type getFcType() {
        try {
            Component boot = Utils.getBootstrapComponent();
            GCMTypeFactory tf = GCM.getGCMTypeFactory(boot);
            return tf.createFcType(new InterfaceType[] { (InterfaceType) ((Interface) fcInterfaceImpl)
                    .getFcItfType() });
        } catch (InstantiationException e) {
            // should never append
            logger.error("Could not generate type for web service component", e);
            return null;
        } catch (NoSuchInterfaceException e) {
            // should never append
            logger.error("Could not generate type for web service component", e);
            return null;
        }
    }

    public String getFcName() {
        return "WSComponent-" +
            ((InterfaceType) ((Interface) fcInterfaceImpl).getFcItfType()).getFcItfSignature().replaceAll(
                    "\\.", "_") + "-" + ((Interface) fcInterfaceImpl).getFcItfName();
    }

    public void setFcName(String name) {
    }

    public String getFcState() {
        return LifeCycleController.STARTED;
    }

    public void startFc() throws IllegalLifeCycleException {
    }

    public void stopFc() throws IllegalLifeCycleException {
    }
}
