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
package org.objectweb.proactive.core.body.ft.servers.recovery;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.ft.servers.util.ActiveQueueJob;
import org.objectweb.proactive.core.body.ft.servers.util.JobBarrier;


/**
 * An object implementing this interface provides recovery methods.
 * This server is an RMI object.
 * @author The ProActive Team
 * @since ProActive 2.2
 */
public interface RecoveryProcess extends Remote {

    /**
     * Active objects possible states.
     */
    public final static int FAILED = 0;
    public final static int RECOVERING = 1;
    public final static int RUNNING = 2;

    /**
     * Register the calling AO. Each AO is registred on creation by newActive.
     * Default state is RUNNING.
     * @param id the registered body id
     */
    public void register(UniqueID id) throws RemoteException;

    /**
     * Unregister the AO identified by id.
     * @param id the unregistered body id
     * @throws RemoteException
     */
    public void unregister(UniqueID id) throws RemoteException;

    /**
     * Notify the recovery process that the body passed in paramater is suspected to be failed.
     * @param id the id of the suspected AO
     */
    public void failureDetected(UniqueID id) throws RemoteException;

    /**
     * Update the current state of the active object id.
     * Its state can be failed, recovering or running.
     * @param id id of the AO to update
     * @param state state of the active object
     */
    public void updateState(UniqueID id, int state) throws RemoteException;

    /**
     * to submit a job to recovery process
     * @param job the job to submit
     * @throws RemoteException
     */
    public void submitJob(ActiveQueueJob job) throws RemoteException;

    /**
     * to submit a job to recovery process, with a barrier for waiting its completion
     * @param job the job to submit
     * @return the barrier on which waiting for the completion of the job
     * @throws RemoteException
     */
    public JobBarrier submitJobWithBarrier(ActiveQueueJob job) throws RemoteException;

    /**
     * Return the size of the system, i.e. the number of registred bodies.
     * @return the number of registred bodies
     * @throws RemoteException
     */
    public int getSystemSize() throws RemoteException;

    /**
     * Reinit the state of the recovery process.
     */
    public void initialize() throws RemoteException;
}
