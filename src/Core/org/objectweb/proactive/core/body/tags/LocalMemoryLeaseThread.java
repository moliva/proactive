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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.body.tags;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 *  Deamon thread of the Tag LocalMemory Leasing
 *  
 *  Run each "CentralProperties.PA_MEMORY_TAG_LEASE_PERIOD" value of time and
 *  check if the tags localmemory existing on all the bodies of this runtime
 *  get their lease value under 0 to clean them, or just decrement their lease 
 *  value.  
 *  
 */
public class LocalMemoryLeaseThread implements Runnable {

    /** Message Tagging LocalMemory Leasing Logger */
    private static Logger logger = ProActiveLogger.getLogger(Loggers.MESSAGE_TAGGING_LOCALMEMORY_LEASING);

    /** Instance singleton */
    private static final Thread singleton = new Thread(new LocalMemoryLeaseThread(),
        "ProActive LocalMemoryLeasing");

    /** Thread
     *  
     *   Get all the bodies of its runtime, check and decrement the lease value of each localmemory existing.
     *   If the lease value is under 0, clean it.
     */
    public void run() {
        final int period = CentralPAPropertyRepository.PA_MEMORY_TAG_LEASE_PERIOD.getValue();

        for (;;) {
            if (logger.isDebugEnabled()) {
                logger.debug("LEASING THREAD RUNNING - " + this);
            }
            try {
                Thread.sleep(period * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Iterator<UniversalBody> iter = LocalBodyStore.getInstance().getLocalBodies().bodiesIterator();
            while (iter.hasNext()) {
                UniversalBody body = iter.next();
                if (body instanceof AbstractBody) {
                    Map<String, LocalMemoryTag> memories = ((AbstractBody) body).getLocalMemoryTags();
                    for (LocalMemoryTag memory : memories.values()) {
                        memory.decCurrentLease(period);
                        if (memory.leaseExceeded()) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Remove local memory of the Tag \"" + memory.getTagIDReferer() +
                                    "\"");
                            }
                            memories.remove(memory.getTagIDReferer());
                        }
                    }
                }
            }
        }
    }

    /**
     * Start this thread as daemon
     */
    static public void start() {
        singleton.setDaemon(true);
        singleton.start();
    }

}
