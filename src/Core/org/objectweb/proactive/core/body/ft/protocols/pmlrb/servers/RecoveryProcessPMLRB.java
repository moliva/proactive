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
package org.objectweb.proactive.core.body.ft.protocols.pmlrb.servers;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.ft.servers.FTServer;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryJob;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcessImpl;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * This class defines a recovery process for PMLRB protocol.
 * @author The ProActive Team
 * @since 3.0
 */
public class RecoveryProcessPMLRB extends RecoveryProcessImpl {
    //logger
    protected static Logger logger = ProActiveLogger.getLogger(Loggers.FAULT_TOLERANCE_PML);

    /**
     * Constructor.
     * @param server the corresponding global server.
     */
    public RecoveryProcessPMLRB(FTServer server) {
        super(server);
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcessImpl#recover(org.objectweb.proactive.core.UniqueID)
     */
    @Override
    protected void recover(UniqueID failed) {
        try {
            Checkpoint toSend = this.server.getLastCheckpoint(failed);

            //look for a new Runtime for this oa
            Node node = this.server.getFreeNode();

            //if (node==null)return;
            RecoveryJob job = new RecoveryJob(toSend, FTManager.DEFAULT_TTC_VALUE, node);
            this.submitJob(job);
        } catch (RemoteException e) {
            logger.error("[RECOVERY] **ERROR** Cannot contact other servers : ");
            e.printStackTrace();
        }
    }
}
