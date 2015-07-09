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
package org.objectweb.proactive.extensions.calcium.environment.proactive;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFilter;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


@ActiveObject
public class AOInterpreterPool implements RunActive, InitActive {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.SKELETONS_ENVIRONMENT);

    private Vector<AOStageIn> pool;
    private boolean shutdown;
    private AOInterpreterPool thisStub;

    /**
     * Empty constructor for ProActive  MOP
     * Do not use directly!!!
     */
    @Deprecated
    public AOInterpreterPool() {
    }

    /**
     * Add each AOInterpreter once to the AOInterpreterPool
     *
     * @param aoi The AOInterpreter array
     */
    public AOInterpreterPool(Boolean proactiveNoise) {
        shutdown = false;
        pool = new Vector<AOStageIn>();
    }

    public void initActivity(Body body) {
        this.thisStub = (AOInterpreterPool) PAActiveObject.getStubOnThis();
    }

    public synchronized void put(AOInterpreter list[], final int times) {

        for (int i = 0; i < times; i++) {
            for (AOInterpreter interpreter : list) {
                pool.add(interpreter.getStageIn(thisStub));
            }
        }
    }

    public synchronized void put(AOStageIn aoi) {
        pool.add(aoi);
    }

    public void putInRandomPosition(AOStageIn aoi, int times) {

        for (int i = 0; i < times; i++) {
            int position = (int) Math.round((Math.random() * pool.size()));
            pool.add(position, aoi);
        }
    }

    public synchronized AOStageIn get() throws ProActiveException {
        if (shutdown) {
            throw new ProActiveException("Interpreter pool is shutting down");
        }
        return pool.remove(0);
    }

    public void shutdown() {
        logger.info("InterpreterPool is shutting down");
        this.shutdown = true;
    }

    //Producer-Consumer
    public void runActivity(Body body) {
        Service service = new Service(body);

        while (true) {
            String allowedMethodNames = "put|putInRandomPosition|shutdown";

            if ((pool != null) && !pool.isEmpty()) {
                allowedMethodNames += "get";
            }

            service.blockingServeOldest(new RequestFilterOnAllowedMethods(allowedMethodNames));
        }
    }

    protected class RequestFilterOnAllowedMethods implements RequestFilter, java.io.Serializable {
        private String allowedMethodNames;

        public RequestFilterOnAllowedMethods(String allowedMethodNames) {
            this.allowedMethodNames = allowedMethodNames;
        }

        public boolean acceptRequest(Request request) {
            return allowedMethodNames.indexOf(request.getMethodName()) >= 0;
        }
    }
}
