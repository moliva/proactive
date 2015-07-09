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
package org.objectweb.proactive.core.util.converter.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;


/**
 * An input stream which defines the deserialization behaviour
 *   
 * If a class cannot be found locally, then it is downloaded from the remote
 * runtime.
 * 
 * @since ProActive 4.3.0
 */
public class ProActiveMarshalInputStream extends ObjectInputStream {

    private final ProActiveRemoteClassLoader remoteLoader;

    public ProActiveMarshalInputStream(InputStream in) throws IOException {
        super(in);
        remoteLoader = new ProActiveRemoteClassLoader();
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            // first, try to resolve the class locally
            return super.resolveClass(desc);
        } catch (ClassNotFoundException e) {
            // try to load the class using the pamr class loader
            String clazzName = desc.getName();
            try {
                String runtimeURL = readRuntimeURL();
                return this.remoteLoader.loadClass(clazzName, runtimeURL);
            } catch (ClassCastException e1) {
                throw new ClassNotFoundException("Cannot load the class " + clazzName +
                    " - violation of the pamr serialization protocol.");
            }
        }
    }

    private String readRuntimeURL() throws IOException, ClassNotFoundException {
        // the protocol guarantees that the URL is the next object in the stream
        return (String) readObject();
    }

}
