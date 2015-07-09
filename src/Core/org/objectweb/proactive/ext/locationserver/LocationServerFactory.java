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
package org.objectweb.proactive.ext.locationserver;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;


public class LocationServerFactory {
    //
    // -- PUBLIC MEMBERS -----------------------------------------------
    //
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public LocationServerFactory() {
    }

    //
    // -- PUBLIC METHOD -----------------------------------------------
    //
    //  public static String getLocationServerClassName() {
    //       return  ProActiveProperties.getLocationServerClass();
    //     }
    //     public static String getLocationServerClassName(UniqueID id) {
    //       return  LocationServerFactory.getLocationServerClassName();
    //     }
    //     public static String getLocationServerName() {
    // 	return ProActiveProperties.getLocationServerRmi();
    //     }
    //     public static String getLocationServerName(UniqueID unique) {
    // 	return LocationServerFactory.getLocationServerName();
    //     }
    public static LocationServer getLocationServer() {
        LocationServer server = null;
        try {
            server = (LocationServer) PAActiveObject.lookupActive(
                    CentralPAPropertyRepository.PA_LOCATION_SERVER.getValue(),
                    CentralPAPropertyRepository.PA_LOCATION_SERVER_RMI.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return server;
        //	try {
        //		Object t = null;
        //		t.toString();
        //	} catch (Exception e) {
        //		System.out.println("-----------------------");
        //		e.printStackTrace();
        //			System.out.println("-----------------------");
        //	}
        //
        //	return null;
    }

    /**
     * Return the location server associated with the
     * <code>id</code>
     *
     */
    public static LocationServer getLocationServer(UniqueID id) {
        return LocationServerFactory.getLocationServer();
    }
}
