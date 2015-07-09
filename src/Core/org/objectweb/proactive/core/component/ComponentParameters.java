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
package org.objectweb.proactive.core.component;

import java.io.Serializable;
import java.util.ArrayList;

import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.annotation.PublicAPI;


/**
 * Contains the configuration of a component. <ul>
 * <li> type</li>
 * <li> interfaces (server and client) --> in contained ControllerDescription object</li>
 * <li> name --> in contained ControllerDescription object</li>
 * <li> hierarchical type (primitive or composite) --> in contained ControllerDescription object</li>
 * <li> a ref on the stub on the base object</li>
 * </ul>
 *
 * @author The ProActive Team
 */
@PublicAPI
public class ComponentParameters implements Serializable {
    private ComponentType componentType;
    private ControllerDescription controllerDesc;

    /**
     * Constructor
     * @param componentType the type of the component
     * @param controllerDesc a ControllerDescription object
     */
    public ComponentParameters(ComponentType componentType, ControllerDescription controllerDesc) {
        this.componentType = componentType;
        this.controllerDesc = controllerDesc;
    }

    /**
     * setter for the name
     * @param name name of the component
     */
    public void setName(String name) {
        controllerDesc.setName(name);
    }

    /**
     * Returns the componentType.
     * @return ComponentType
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    /**
     * getter
     * @return a ControllerDescription object
     */
    public ControllerDescription getControllerDescription() {
        return controllerDesc;
    }

    /**
     * setter
     * @param componentType the type of the component
     */
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    /**
     * setter
     * @param string the hierarchical type (primitive or composite)
     */
    public void setHierarchicalType(String string) {
        controllerDesc.setHierarchicalType(string);
    }

    /**
     * getter
     * @return the name
     */
    public String getName() {
        return controllerDesc.getName();
    }

    /**
     * Returns the hierarchicalType.
     * @return String
     */
    public String getHierarchicalType() {
        return controllerDesc.getHierarchicalType();
    }

    /**
     * @return the types of server interfaces
     */
    public InterfaceType[] getServerInterfaceTypes() {
        ArrayList<InterfaceType> server_interfaces = new ArrayList<InterfaceType>();
        InterfaceType[] interfaceTypes = componentType.getFcInterfaceTypes();
        for (int i = 0; i < interfaceTypes.length; i++) {
            if (!interfaceTypes[i].isFcClientItf()) {
                server_interfaces.add(interfaceTypes[i]);
            }
        }
        return server_interfaces.toArray(new InterfaceType[server_interfaces.size()]);
    }

    /**
     * @return the types of client interfacess
     */
    public InterfaceType[] getClientInterfaceTypes() {
        return Utils.getClientItfTypes(componentType);
    }

    /**
     * getter
     * @return a table of interface types
     */
    public InterfaceType[] getInterfaceTypes() {
        return componentType.getFcInterfaceTypes();
    }
}
