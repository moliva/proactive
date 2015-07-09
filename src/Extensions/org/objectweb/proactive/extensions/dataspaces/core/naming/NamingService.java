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
package org.objectweb.proactive.extensions.dataspaces.core.naming;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.annotation.RemoteObject;
import org.objectweb.proactive.extensions.dataspaces.core.DataSpacesURI;
import org.objectweb.proactive.extensions.dataspaces.core.SpaceInstanceInfo;
import org.objectweb.proactive.extensions.dataspaces.exceptions.ApplicationAlreadyRegisteredException;
import org.objectweb.proactive.extensions.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extensions.dataspaces.exceptions.WrongApplicationIdException;


/**
 * Naming Service for Data Spaces subsystem.
 * <p>
 * Naming Service behaves like an extended {@link SpacesDirectory}, being more aware of an
 * application lifetime context. It provides directory of registered applications and their data
 * spaces with access information. Every application needs to be explicitly registered and
 * unregistered, and spaces of given application are registered only for period when that
 * application is being registered.
 * <p>
 * Instances of this class are intended to work as remote objects and they are thread-safe.
 *
 * @see SpacesDirectory
 */
@RemoteObject
public class NamingService implements SpacesDirectory {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES_NAMING_SERVICE);

    /**
     * Connects to a remote NamingService object under specified URL.
     *
     * @param url
     *            to connect
     * @return stub
     * @throws ProActiveException
     *             when PA exception occurs (communication error) or when no valid NS found under
     *             specified URL
     * @throws URISyntaxException
     *             when URL cannot be parsed
     */
    public static NamingService createNamingServiceStub(String url) throws ProActiveException,
            URISyntaxException {

        Object stub = PARemoteObject.lookup(new URI(url));

        if (stub instanceof NamingService)
            return (NamingService) stub;
        else
            throw new ProActiveException("No valid NamingService instance can be found under specified URL");
    }

    private static void checkApplicationSpaces(long appId, Set<SpaceInstanceInfo> inSet)
            throws WrongApplicationIdException {
        for (SpaceInstanceInfo sii : inSet) {
            if (sii.getAppId() != appId) {
                logger.error("Application id does not match one found in its space: " + sii);
                throw new WrongApplicationIdException("Application id does not match one found in its space");
            }
        }
    }

    private final Set<Long> registeredApplications = new HashSet<Long>();

    private final SpacesDirectoryImpl directory = new SpacesDirectoryImpl();

    /**
     * Registers application along with its spaces definition.
     *
     * @param appId
     *            application identifier, must be unique
     * @param spaces
     *            bulked input and output spaces definitions for that application or
     *            <code>null</code> if there is no input/output space
     * @throws WrongApplicationIdException
     *             When given appId doesn't match one found in DataSpacesURI of spaces to register
     *             for application.
     * @throws ApplicationAlreadyRegisteredException
     *             When specified application id is already registered.
     */
    synchronized public void registerApplication(long appId, Set<SpaceInstanceInfo> spaces)
            throws ApplicationAlreadyRegisteredException, WrongApplicationIdException {
        logger.debug("Registering application with id " + appId);

        if (isApplicationIdRegistered(appId)) {
            throw new ApplicationAlreadyRegisteredException(
                "Application with the same application id is already registered.");
        }

        if (spaces != null)
            checkApplicationSpaces(appId, spaces);

        registeredApplications.add(appId);
        logger.debug("Registered application with id " + appId);
        if (spaces != null) {
            directory.register(spaces);
            if (logger.isDebugEnabled()) {
                for (final SpaceInstanceInfo info : spaces)
                    logger.debug("Registered space: " + info);
            }
        }
    }

    /**
     * Unregisters application with specified identifier together with all spaces registered by this
     * application.
     *
     * @param appId
     *            application identifier
     * @throws WrongApplicationIdException
     *             when specified application id is not registered
     */
    synchronized public void unregisterApplication(long appId) throws WrongApplicationIdException {
        logger.debug("Unregistering application with id " + appId);

        final boolean found = registeredApplications.remove(appId);
        if (!found)
            throw new WrongApplicationIdException("Application with specified appid is not registered.");

        final Set<SpaceInstanceInfo> spaces = lookupMany(DataSpacesURI.createURI(appId));

        if (spaces == null)
            return;

        final Set<DataSpacesURI> uris = new HashSet<DataSpacesURI>(spaces.size());

        for (SpaceInstanceInfo sii : spaces)
            uris.add(sii.getMountingPoint());

        directory.unregister(uris);
        if (logger.isDebugEnabled()) {
            for (final DataSpacesURI uri : uris)
                logger.debug("Unregistered space: " + uri);
            logger.debug("Unregistered application with id " + appId);
        }
    }

    /**
     * Registers provided data space instance for already registered application. If mounting point
     * of that space instance has been already in the directory, an exception is raised as directory
     * is append-only.
     * <p>
     * Note that this method has more constrained contract than
     * {@link SpacesDirectory#register(SpaceInstanceInfo)} regarding application id.
     *
     * @param spaceInstanceInfo
     *            space instance info to register (contract: SpaceInstanceInfo mounting point should
     *            have space part fully defined)
     * @throws WrongApplicationIdException
     *             when directory is aware of all registered applications and there is no such
     *             application for SpaceInstanceInfo being registered
     * @throws SpaceAlreadyRegisteredException
     *             when directory already contains any space instance under specified mounting point
     * @see SpacesDirectory#register(SpaceInstanceInfo)
     */
    synchronized public void register(SpaceInstanceInfo spaceInstanceInfo)
            throws WrongApplicationIdException, SpaceAlreadyRegisteredException {
        logger.debug("Registering space: " + spaceInstanceInfo);

        final long appid = spaceInstanceInfo.getAppId();

        if (!isApplicationIdRegistered(appid))
            throw new WrongApplicationIdException(
                "There is no application registered with specified application id.");

        directory.register(spaceInstanceInfo);
        logger.debug("Registered space: " + spaceInstanceInfo);
    }

    public Set<SpaceInstanceInfo> lookupMany(DataSpacesURI uri) throws IllegalArgumentException {
        if (logger.isTraceEnabled())
            logger.trace("LookupAll query for: " + uri);
        return directory.lookupMany(uri);
    }

    public SpaceInstanceInfo lookupOne(DataSpacesURI uri) throws IllegalArgumentException {
        if (logger.isTraceEnabled())
            logger.trace("Lookup query for: " + uri);
        return directory.lookupOne(uri);
    }

    public boolean unregister(DataSpacesURI uri) {
        final boolean result = directory.unregister(uri);
        logger.debug("Unregistered space: " + uri);
        return result;
    }

    synchronized public Set<Long> getRegisteredApplications() {
        return Collections.unmodifiableSet(registeredApplications);
    }

    synchronized public boolean isApplicationIdRegistered(long appid) {
        return getRegisteredApplications().contains(appid);
    }
}