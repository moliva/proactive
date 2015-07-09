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
package org.objectweb.proactive.core.jmx.notification;

/**
 * @author The ProActive Team
 */
public class NotificationType {
    public final static String unknown = "unknown";
    public final static String setOfNotifications = "setOfNotifications";

    // --- Corresponds to the RequestQueueEvent --------------------
    /* Not Used */
    // public final static String requestQueueModified = "requestQueueModified";
    // public final static String addRequest = "addRequest";
    // public final static String removeRequest = "removeRequest";
    public final static String waitForRequest = "waitForRequest";

    // --- Corresponds to the MessageEvent -------------------------
    public final static String replyReceived = "replyReceived";
    public final static String replySent = "replySent";
    public final static String requestReceived = "requestReceived";
    public final static String requestSent = "requestSent";
    public final static String servingStarted = "servingStarted";
    public final static String voidRequestServed = "voidRequestServed";
    // cruz
    public final static String voidRequestSent = "voidRequestSent";
	public final static String realReplyReceived = "realReplyReceived";
    public final static String requestWbN = "requestWaitByNecessity";
	public final static String requestFutureUpdate = "requestFutureUpdate";
	public final static String replyAC = "replyAutomaticContinuation";
	public final static String realReplySent = "realReplySent";
	// -- cruz

    // --- Corresponds to the MigrationEvent -----------------------
    public final static String migratedBodyRestarted = "migratedBodyRestarted";
    public final static String migrationAboutToStart = "migrationAboutToStart";
    public final static String migrationExceptionThrown = "migrationExceptionThrown";
    /**
     * Emitted by {@link org.objectweb.proactive.core.body.migration.MigrationManagerImpl} 
     * when the migration is finished.
     * <p>
     * The associated notification data is the destination runtime url. This data is used 
     * to update all JMX related stuff (migration of the mbean and its listeners). 
     * @see org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean
     */
    public final static String migrationFinished = "migrationFinished";

    // --- Corresponds to the FuturEvent ---------------------------
    public final static String receivedFutureResult = "receivedFutureResult";
    public final static String waitByNecessity = "waitByNecessity";

    // --- Corresponds to the NodeCreationEvent --------------------
    public final static String nodeCreated = "nodeCreated";
    public final static String nodeDestroyed = "nodeDestroyed";

    // --- Corresponds to the BodyEventListener --------------------
    /* Not Used */
    // public final static String bodyChanged = "bodyChanged";
    public final static String bodyCreated = "bodyCreated";
    public final static String bodyDestroyed = "bodyDestroyed";

    // --- Corresponds to the RuntimeRegistrationEvent -------------
    public final static String runtimeRegistered = "runtimeRegistered";
    public final static String runtimeUnregistered = "runtimeUnregistered";
    public final static String runtimeAcquired = "runtimeAcquired";

    // --- GCM Deployment
    public final static String GCMRuntimeRegistered = "GCMRuntimeRegistered";

    // --- Step by Step
    public final static String stepByStepEnabled = "stepByStepEnabled";
    public final static String stepByStepDisabled = "stepByStepDisabled";
    public final static String stepByStepBlocked = "stepByStepBlocked";
    public final static String stepByStepResumed = "stepByStepResumed";
    public final static String stepByStepSlowMotionEnabled = "stepByStepSlowMotionEnabled";
    public final static String stepByStepSlowMotionDisabled = "stepByStepSlowMotionDisabled";
    public final static String stepByStepISEnabled = "stepByStepISEnabled";
    public final static String stepByStepISDisabled = "stepByStepISDisabled";

    // ExtendedDebugger
    public final static String connectDebugger = "connectDebugger";
    public final static String disconnectDebugger = "disconnectDebugger";
    public final static String requestQueueModified = "requestQueueModified";

    /* TODO Send this notification */
    // public final static String forwarderRuntimeRegistered = "forwarderRuntimeRegistered";
    public final static String runtimeDestroyed = "runtimeDestroyed";

    // --- Used in the message of the JMX notifications -------------
    public final static String migrationMessage = "Migration Finished";

    public final static String debuggerConnectionActivated = "debuggerConnectionActivated";
    public final static String debuggerConnectionTerminated = "debuggerConnectionTerminated";

}
