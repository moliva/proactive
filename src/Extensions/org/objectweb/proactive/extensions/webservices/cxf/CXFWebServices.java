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
package org.objectweb.proactive.extensions.webservices.cxf;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.AbstractWebServices;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.common.MethodUtils;
import org.objectweb.proactive.extensions.webservices.cxf.deployer.PADeployer;
import org.objectweb.proactive.extensions.webservices.cxf.initialization.CXFInitializer;
import org.objectweb.proactive.extensions.webservices.exceptions.WebServicesException;


/**
 * @author The ProActive Team
 *
 */
public class CXFWebServices extends AbstractWebServices implements WebServices {

    static private Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    /**
     * @param url
     */
    public CXFWebServices(String url) {
        super(url);
        CXFInitializer.init();
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#exposeAsWebService(java.lang.Object, java.lang.String, java.lang.reflect.Method[])
     */
    public void exposeAsWebService(Object o, String urn, Method[] methods) throws WebServicesException {

        if (methods == null || methods.length == 0) {
            exposeAsWebService(o, urn);
            return;
        }

        MethodUtils.checkMethodsClass(methods);
        PADeployer.deploy(o, this.url, urn, methods, false);

        logger.debug("The object of type '" + o.getClass().getSuperclass().getName() +
            "' has been deployed on " + this.url + WSConstants.SERVICES_PATH + urn + "?wsdl");
        logger.debug("Only the following methods of this object have been deployed: ");
        for (Method method : methods) {
            logger.debug(" - " + method.getName());
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#exposeAsWebService(java.lang.Object, java.lang.String, java.lang.String[])
     */
    public void exposeAsWebService(Object o, String urn, String[] methodsName) throws WebServicesException {

        if (methodsName == null || methodsName.length == 0) {
            exposeAsWebService(o, urn);
            return;
        }

        // Transforms the array methods' name into an array of
        // methods (of type Method)
        MethodUtils mc = new MethodUtils(o.getClass().getSuperclass());
        ArrayList<Method> methodsArrayList = mc.getCorrespondingMethods(methodsName);
        Method[] methods = new Method[methodsArrayList.size()];
        methodsArrayList.toArray(methods);
        PADeployer.deploy(o, this.url, urn, methods, false);

        logger.debug("The object of type '" + o.getClass().getSuperclass().getName() +
            "' has been deployed on " + this.url + WSConstants.SERVICES_PATH + urn + "?wsdl");
        logger.debug("Only the following methods of this object have been deployed: ");
        for (String name : methodsName) {
            logger.debug(" - " + name);
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#exposeAsWebService(java.lang.Object, java.lang.String)
     */
    public void exposeAsWebService(Object o, String urn) throws WebServicesException {
        PADeployer.deploy(o, this.url, urn, null, false);

        logger.debug("The object of type '" + o.getClass().getSuperclass().getName() +
            "' has been deployed on " + this.url + WSConstants.SERVICES_PATH + urn + "?wsdl");
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#unExposeAsWebService(java.lang.String)
     */
    public void unExposeAsWebService(String urn) {
        PADeployer.undeploy(this.url, urn);

        logger.debug("The service '" + urn + "' previously deployed on " + this.url +
            WSConstants.SERVICES_PATH + urn + "?wsdl " + "has been undeployed");
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#exposeComponentAsWebService(org.objectweb.fractal.api.Component, java.lang.String, java.lang.String[])
     */
    public void exposeComponentAsWebService(Component component, String componentName, String[] interfaceNames)
            throws WebServicesException {

        if (interfaceNames == null || interfaceNames.length == 0) {
            exposeComponentAsWebService(component, componentName);
            return;
        }

        PADeployer.deployComponent(component, this.url, componentName, interfaceNames);

        for (String name : interfaceNames) {
            logger.debug("The component interface '" + name + "' has been deployed on " + this.url +
                WSConstants.SERVICES_PATH + componentName + "_" + name + "?wsdl");
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#unExposeComponentAsWebService(org.objectweb.fractal.api.Component, java.lang.String, java.lang.String[])
     */
    public void unExposeComponentAsWebService(Component component, String componentName,
            String[] interfaceNames) throws WebServicesException {
        if (interfaceNames == null || interfaceNames.length == 0) {
            unExposeComponentAsWebService(component, componentName);
        } else {
            unExposeComponentAsWebService(componentName, interfaceNames);
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#exposeComponentAsWebService(org.objectweb.fractal.api.Component, java.lang.String)
     */
    public void exposeComponentAsWebService(Component component, String componentName)
            throws WebServicesException {
        PADeployer.deployComponent(component, this.url, componentName, null);

        Object[] interfaces = component.getFcInterfaces();
        for (Object o : interfaces) {
            Interface interface_ = (Interface) o;
            String interfaceName = interface_.getFcItfName();
            if (!Utils.isControllerItfName(interfaceName) &&
                !((InterfaceType) interface_.getFcItfType()).isFcClientItf()) {

                logger.debug("The component interface '" + interfaceName + "' has been deployed on " +
                    this.url + WSConstants.SERVICES_PATH + componentName + "_" + interfaceName + "?wsdl");
            }
        }
    }

    /* (non-Javadoc)
     *
     * With CXF, this method
     * can only be used if you have previously deployed all the client interfaces of the component.
     * Otherwise, it will raise an exception trying to undeploy a client interface which has not been
     * deployed before.
     *
     * @see org.objectweb.proactive.extensions.webservices.WebServices#unExposeComponentAsWebService(org.objectweb.fractal.api.Component, java.lang.String)
     */
    public void unExposeComponentAsWebService(Component component, String componentName) {
        PADeployer.undeployComponent(component, this.url, componentName);

        Object[] interfaces = component.getFcInterfaces();
        for (Object o : interfaces) {
            Interface interface_ = (Interface) o;
            String interfaceName = interface_.getFcItfName();
            if (!Utils.isControllerItfName(interfaceName) &&
                !((InterfaceType) interface_.getFcItfType()).isFcClientItf()) {
                logger.debug("The component interface '" + interfaceName + "' previously deployed on " +
                    this.url + WSConstants.SERVICES_PATH + componentName + "_" + interfaceName +
                    "?wsdl has been undeployed");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#unExposeComponentAsWebService(java.lang.String, java.lang.String[])
     */
    public void unExposeComponentAsWebService(String componentName, String[] interfaceNames) {
        PADeployer.undeployComponent(this.url, componentName, interfaceNames);

        for (String name : interfaceNames) {
            logger.debug("The component interface '" + name + "' previously deployed on " + this.url +
                WSConstants.SERVICES_PATH + componentName + "_" + name + "?wsdl has been undeployed");
        }
    }

}
