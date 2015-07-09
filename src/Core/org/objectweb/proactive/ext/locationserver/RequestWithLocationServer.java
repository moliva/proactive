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

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.future.FutureProxy;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.RequestImpl;
import org.objectweb.proactive.core.body.tags.MessageTags;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class RequestWithLocationServer extends RequestImpl implements java.io.Serializable {
    private static final int MAX_TRIES = 30;
    static Logger logger = ProActiveLogger.getLogger(Loggers.MIGRATION);

    /**
     * the number of time we try before reporting a failure
     */
    private int tries;
    private transient LocationServer server;

    public RequestWithLocationServer(MethodCall methodCall, UniversalBody sender, boolean isOneWay,
            long nextSequenceID, LocationServer server, MessageTags tags) {
        super(methodCall, sender, isOneWay, nextSequenceID);
        this.server = server;
    }

    public RequestWithLocationServer(MethodCall methodCall, UniversalBody sender, boolean isOneWay,
            long nextSequenceID, LocationServer server) {
        this(methodCall, sender, isOneWay, nextSequenceID, server, null);
    }

    @Override
    public Reply serve(Body targetBody) {
        Reply r = super.serve(targetBody);
        return r;
    }

    @Override
    protected int sendRequest(UniversalBody destinationBody) throws java.io.IOException {
        int ftres = FTManager.NON_FT;
        try {
            //   startTime = System.currentTimeMillis();
            ftres = destinationBody.receiveRequest(this);

            //    long endTime = System.currentTimeMillis();
        } catch (Exception e) {
            this.backupSolution(destinationBody);
        }
        return ftres;
    }

    /**
     * Implements the backup solution
     */
    protected void backupSolution(UniversalBody destinationBody) throws java.io.IOException {
        boolean ok = false;
        tries = 0;
        //get the new location from the server
        UniqueID bodyID = destinationBody.getID();
        while (!ok && (tries < MAX_TRIES)) {
            UniversalBody remoteBody = null;
            UniversalBody mobile = queryServer(bodyID);

            //we want to bypass the stub/proxy
            remoteBody = (UniversalBody) ((FutureProxy) ((StubObject) mobile).getProxy()).getResult();
            try {
                remoteBody.receiveRequest(this);

                //everything went fine, we have to update the current location of the object
                //so that next requests don't go through the server
                if (sender != null) {
                    sender.updateLocation(bodyID, remoteBody);
                } else {
                    LocalBodyStore.getInstance().getLocalBody(getSourceBodyID()).updateLocation(bodyID,
                            remoteBody);
                }
                ok = true;
            } catch (Exception e) {
                logger.debug("RequestWithLocationServer:  .............. FAILED = " + " for method " +
                    methodName);
                tries++;
            }
        }
    }

    protected UniversalBody queryServer(UniqueID bodyID) {
        if (server == null) {
            server = LocationServerFactory.getLocationServer();
        }
        UniversalBody mobile = server.searchObject(bodyID);

        logger.debug("RequestWithLocationServer: backupSolution() server has sent an answer");

        PAFuture.waitFor(mobile);
        return mobile;
    }
}
