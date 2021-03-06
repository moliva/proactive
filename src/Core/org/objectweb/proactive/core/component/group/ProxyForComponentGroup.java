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
package org.objectweb.proactive.core.component.group;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.group.ProxyForGroup;
import org.objectweb.proactive.core.mop.ConstructionOfReifiedObjectFailedException;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.mop.StubObject;


/**
 * An extension of the standard group proxy for handling groups of components.
 *
 * @author The ProActive Team
 *
 */
public class ProxyForComponentGroup<E> extends ProxyForGroup<E> {
    protected ComponentType componentType;
    protected ControllerDescription controllerDesc;

    public ProxyForComponentGroup() throws ConstructionOfReifiedObjectFailedException {
        super();
        className = Component.class.getName();
    }

    public ProxyForComponentGroup(ConstructorCall c, Object[] p)
            throws ConstructionOfReifiedObjectFailedException {
        super(c, p);
        className = Component.class.getName();
    }

    public ProxyForComponentGroup(String nameOfClass) throws ConstructionOfReifiedObjectFailedException {
        super(nameOfClass);
        className = Component.class.getName();
    }

    /*
     * @see org.objectweb.proactive.core.group.Group#getGroupByType()
     */
    @Override
    public Object getGroupByType() {
        try {
            Component result = PAComponentGroup
                    .newComponentRepresentativeGroup(componentType, controllerDesc);

            @SuppressWarnings("unchecked")
            ProxyForComponentGroup<E> proxy = (ProxyForComponentGroup<E>) ((StubObject) result).getProxy();
            proxy.memberList = this.memberList;
            proxy.className = this.className;
            proxy.componentType = this.componentType;
            proxy.controllerDesc = this.controllerDesc;
            proxy.proxyForGroupID = this.proxyForGroupID;
            proxy.waited = this.waited;
            return result;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return Returns the componentType.
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    /**
     * @param componentType The componentType to set.
     */
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    /**
     * @return Returns the controllerDesc.
     */
    public ControllerDescription getControllerDesc() {
        return controllerDesc;
    }

    /**
     * @param controllerDesc The controllerDesc to set.
     */
    public void setControllerDesc(ControllerDescription controllerDesc) {
        this.controllerDesc = controllerDesc;
    }
}
