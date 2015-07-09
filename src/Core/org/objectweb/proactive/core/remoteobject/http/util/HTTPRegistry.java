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
package org.objectweb.proactive.core.remoteobject.http.util;

import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.proactive.core.remoteobject.AlreadyBoundException;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * An HTTP Registry that registers Bodies
 * @author The ProActive Team
 *
 */
public class HTTPRegistry {
    private static final String REGISTRY_NAME = "HTTP_REGISTRY";
    private static HTTPRegistry instance;
    private static ConcurrentHashMap<String, InternalRemoteRemoteObject> rRemteObjectMap = new ConcurrentHashMap<String, InternalRemoteRemoteObject>();

    private HTTPRegistry() {
    }

    /**
     * Gets the unique instance of the registry
     * @return the unique instance of the registry
     */
    public static synchronized HTTPRegistry getInstance() {
        if (instance == null) {
            instance = new HTTPRegistry();
        }
        return instance;
    }

    /**
     * Binds an internal remote remote object to an identifier
     * @param name the identifier
     * @param irro the internal remote remote object to store
     * @throws AlreadyBoundException if the name already exists
     */
    public void bind(String name, InternalRemoteRemoteObject irro, boolean rebind)
            throws AlreadyBoundException {
        if (rebind) {
            rRemteObjectMap.put(name, irro);
        } else {
            InternalRemoteRemoteObject r = rRemteObjectMap.putIfAbsent(name, irro);
            if (r != null) {
                throw new AlreadyBoundException(name + " is already bound");
            }
        }

        ProActiveLogger.getLogger(Loggers.REMOTEOBJECT).debug("registed remote object at " + name);

    }

    /**
     * Unbinds a body from a  name
     * @param name the name binded with a body
     */
    public void unbind(String name) {
        rRemteObjectMap.remove(name);
    }

    /**
     * Gives all the names registered in this registry
     * @return the names list
     */
    public String[] list() {
        String[] list = new String[rRemteObjectMap.size()];
        rRemteObjectMap.keySet().toArray(list);
        return list;
    }

    /**
     * Retrieves a body from a name
     * @param name The name of the remote object to be retrieved
     * @return the internal remote remote object matching the name
     */
    public InternalRemoteRemoteObject lookup(String name) {
        return rRemteObjectMap.get(name);
    }
}
