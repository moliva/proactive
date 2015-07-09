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

import org.apache.log4j.Logger;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.calcium.exceptions.TaskException;
import org.objectweb.proactive.extensions.calcium.task.Task;


public class TaskDispatcher extends Thread {
    static Logger logger = ProActiveLogger.getLogger(Loggers.SKELETONS_ENVIRONMENT);
    private AOTaskPool taskpool;
    private AOInterpreterPool interpool;
    private boolean shutdown;

    public TaskDispatcher(AOTaskPool taskpool, AOInterpreterPool interpool) {
        super();
        shutdown = false;

        this.taskpool = taskpool;
        this.interpool = interpool;
    }

    @Override
    public void run() {
        shutdown = false;

        while (!shutdown) {
            //TODO fix this blocking call
            Task task = taskpool.getReadyTask(0);
            task = (Task) PAFuture.getFutureValue(task);

            try {
                //block until there is an available interpreter
                AOStageIn interp = null;

                while (interp == null) {
                    interp = interpool.get();

                    if (shutdown) {
                        interpool.put(interp);
                        task.setException(new TaskException("Shutting down"));
                        taskpool.putProcessedTask(task);

                        return;
                    }
                }

                interp.stageIn(task); //remote async call
            } catch (Exception e) {
                if (task != null) {
                    task.setException(e);
                    taskpool.putProcessedTask(task);
                }
            }
        }

        logger.info("TaskDispatcher has shutdown");
    }

    public void shutdown() {
        logger.info("TaskDispatcher is shutting down");
        shutdown = true;
    }
}
