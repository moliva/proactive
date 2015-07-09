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
package org.objectweb.proactive.core.security;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.Remote;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteStub;


/**
 * @author The ProActive Team
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SecurityOutputStream extends ObjectOutputStream {

    /**
     * @param out
     * @throws java.io.IOException
     */
    public SecurityOutputStream(OutputStream out) throws IOException {
        super(out);
        System.out.println("-*-**-*--*-*-*-**--**-*--*-**- instanciating securtityoutpoutstream");
        java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Object>() {
            public Object run() {
                enableReplaceObject(true);
                return null;
            }
        });
    }

    /**
     * @throws java.io.IOException
     * @throws java.lang.SecurityException
     */
    public SecurityOutputStream() throws IOException, SecurityException {
        super();
    }

    /**
     * replaceObject is extended to check for instances of Remote
     * that need to be serialized as proxy objects.  RemoteProxy.getProxy
     * is called to check for and find the stub.
     */
    @Override
    protected Object replaceObject(Object obj) throws IOException {
        System.out.println(" */*/*/*/*/*/*/* /*/*/**//**/ Inside replaceObject /*/**/*//*/**//*/*/**//*/*");
        if ((obj instanceof Remote) && !(obj instanceof RemoteStub)) {
            System.out.println(" */*/*/*/*/*/*/* /*/*/**//**/ found a Remote object " + obj +
                " /*/**/*//*/**//*/*/**//*/*");
            Remote target = RemoteObject.toStub((Remote) obj);
            if (target != null) {
                return target;
            }
        }
        System.out
                .println(" */*/*/*/*/*/*/* /*/*/**//**/ Normal obj : " + obj + "/*/**/*//*/**//*/*/**//*/*");
        return obj;
    }
}
