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
package org.objectweb.proactive.extensions.mixedlocation;

import org.objectweb.proactive.core.body.MetaObjectFactory;
import org.objectweb.proactive.core.body.ProActiveMetaObjectFactory;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.migration.MigrationManager;
import org.objectweb.proactive.core.body.migration.MigrationManagerFactory;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFactory;
import org.objectweb.proactive.core.body.tags.MessageTags;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.ext.locationserver.LocationServer;
import org.objectweb.proactive.ext.locationserver.LocationServerFactory;


/**
 * <p>
 * This class overrides the default Factory to provide Request and MigrationManager
 * with a mixed location server.
 * </p>
 *
 * @author The ProActive Team
 * @version 1.0,  2002/05
 * @since   ProActive 0.9.2
 */
public class MixedLocationMetaObjectFactory extends ProActiveMetaObjectFactory {
    //
    // -- PRIVATE MEMBERS -----------------------------------------------
    //
    private static MetaObjectFactory instance = null;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //

    /**
     * Constructor for LocationServerMetaObjectFactory.
     */
    protected MixedLocationMetaObjectFactory() {
        super();
    }

    //
    // -- PUBLICS METHODS -----------------------------------------------
    //
    public static synchronized MetaObjectFactory newInstance() {
        if (instance == null) {
            instance = new MixedLocationMetaObjectFactory();
        }
        return instance;
    }

    //
    // -- PROTECTED METHODS -----------------------------------------------
    //
    @Override
    protected RequestFactory newRequestFactorySingleton() {
        return new RequestWithMixedLocationFactory();
    }

    @Override
    protected MigrationManagerFactory newMigrationManagerFactorySingleton() {
        return new MigrationManagerFactoryImpl();
    }

    //
    // -- INNER CLASSES -----------------------------------------------
    //
    protected class RequestWithMixedLocationFactory implements RequestFactory, java.io.Serializable {
        transient private LocationServer server = LocationServerFactory.getLocationServer();

        public Request newRequest(MethodCall methodCall, UniversalBody sourceBody, boolean isOneWay,
                long sequenceID, MessageTags tags) {
            return new RequestWithMixedLocation(methodCall, sourceBody, isOneWay, sequenceID, server, tags);
        }
    }

    protected static class MigrationManagerFactoryImpl implements MigrationManagerFactory,
            java.io.Serializable {
        public MigrationManager newMigrationManager() {
            //System.out.println("BodyWithMixedLocation.createMigrationManager");
            return new MigrationManagerWithMixedLocation(LocationServerFactory.getLocationServer());
        }
    }

    //    @Override
    //    protected RemoteBodyFactory newRemoteBodyFactorySingleton() {
    //        if (Constants.IBIS_PROTOCOL_IDENTIFIER.equals(
    //                    ProActiveConfiguration.getInstance()
    //                                              .getProperty(Constants.PROPERTY_PA_COMMUNICATION_PROTOCOL))) {
    //            if (logger.isDebugEnabled()) {
    //                logger.debug("Factory is ibis");
    //            }
    //            return new RemoteIbisBodyFactoryImpl();
    //        } else {
    //            if (logger.isDebugEnabled()) {
    //                logger.debug("Factory is rmi");
    //            }
    //            return new RemoteRmiBodyFactoryImpl();
    //        }
    //    }

    //    protected static class RemoteIbisBodyFactoryImpl
    //        implements RemoteBodyFactory, java.io.Serializable {
    //        public UniversalBody newRemoteBody(UniversalBody body) {
    //            try {
    //                // 	System.out.println("Creating ibis remote body adapter");
    //                return new IbisBodyAdapter(body);
    //            } catch (ProActiveException e) {
    //                throw new ProActiveRuntimeException("Cannot create Ibis Remote body adapter ",
    //                    e);
    //            }
    //        }
    //    }
    //
    //    // end
    //    protected static class RemoteRmiBodyFactoryImpl implements RemoteBodyFactory,
    //        java.io.Serializable {
    //        public UniversalBody newRemoteBody(UniversalBody body) {
    //            try {
    //                return new org.objectweb.proactive.core.body.rmi.RmiBodyAdapter(body);
    //            } catch (ProActiveException e) {
    //                throw new ProActiveRuntimeException("Cannot create Remote body adapter ",
    //                    e);
    //            }
    //        }
    //    }
    //
    //    // end inner class RemoteBodyFactoryImpl
}
