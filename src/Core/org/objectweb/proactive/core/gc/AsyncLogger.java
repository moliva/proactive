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
package org.objectweb.proactive.core.gc;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * Just to bind together a log4j level and a message
 */
class LogMessage {
    private final Level level;
    private final String msg;

    LogMessage(final Level level, final String msg) {
        this.level = level;
        this.msg = msg;
    }

    Level getLevel() {
        return level;
    }

    String getMsg() {
        return msg;
    }
}

/**
 * Logging can print to stdout which can block, we don't want to be blocked
 * for an arbitrary amount of time, hence the thread.
 */
public class AsyncLogger implements Runnable {

    /**
     * The category name is "proactive.gc".
     */
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.GC);

    /**
     * The messages to log in the thread
     */
    private static final LinkedBlockingQueue<LogMessage> queue = new LinkedBlockingQueue<LogMessage>();

    /**
     * The thread that will log all messages in FIFO order
     */
    private static final Thread loggingThread = new Thread(new AsyncLogger(), "GC Logging Thread");

    static {
        loggingThread.setDaemon(true);
        loggingThread.start();
    }

    /**
     * Check if the message will be logged. Can be used to avoid building
     * argument strings just to drop them after.
     */
    public static boolean isEnabledFor(Level level) {
        return logger.isEnabledFor(level);
    }

    /**
     * Add a new message to be logged.
     */
    public static void queueLog(Level level, String msg) {
        queue.add(new LogMessage(level, msg));
    }

    /**
     * The thread simply waits for messages and log them.
     */
    public void run() {
        for (;;) {
            LogMessage message = null;
            try {
                message = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (message != null) {
                logger.log(message.getLevel(), message.getMsg());
            }
        }
    }
}
