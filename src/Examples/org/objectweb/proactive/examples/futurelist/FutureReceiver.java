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
package org.objectweb.proactive.examples.futurelist;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.ext.util.FutureList;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


@ActiveObject
public class FutureReceiver implements java.io.Serializable {
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);
    private int etape = 0; // this is to count the jumps we have made so far
    private BlockedObject blocked;
    private java.util.Vector<EmptyFuture> waitingFutures = new java.util.Vector<EmptyFuture>();
    private FutureList futureList;

    public FutureReceiver() {
    }

    public void getFuture() {
        //System.out.println("FutureReceiver: Now processing getFuture()");
        //we  create a future objet through this call since the callee is blocked
        waitingFutures.add(blocked.createFuture());
    }

    public void getFutureAndAddToFutureList() {
        //System.out.println("FutureReceiver: Now processing getFutureAndAddToFutureList()");
        EmptyFuture f = blocked.createFuture();
        waitingFutures.add(f);
        this.addToFutureList(f);
    }

    public void displayAllFutures() {
        EmptyFuture temp;

        for (java.util.Enumeration<EmptyFuture> e = waitingFutures.elements(); e.hasMoreElements();) {
            temp = e.nextElement();
            logger.info("Result: " + temp.getName());
        }
    }

    //call a method on the blockedObject to unblock it
    public void unblockOtherObject() {
        blocked.go();
    }

    public void setBlockedObject(BlockedObject t) {
        blocked = t;
    }

    /**
     * Request its body futurePool and create an empty future list
     */
    public void createFutureList() {
        org.objectweb.proactive.core.body.ActiveBody b = (org.objectweb.proactive.core.body.ActiveBody) org.objectweb.proactive.api.PAActiveObject
                .getBodyOnThis();
        this.futureList = new FutureList();
    }

    public void addToFutureList(Object o) {
        this.futureList.add(o);
    }

    public void displayAwaited() {
        if (futureList != null) {
            logger.info("FutureReceiver: I am still waiting " + futureList.countAwaited() + " futures");
        }
    }

    public void displayAllAwaited() {
        if (futureList != null) {
            logger.info("FutureReceiver: I am waiting for all my futures:  " + futureList.allAwaited());
        }
    }

    public void displayNoneAwaited() {
        if (futureList != null) {
            logger.info("FutureReceiver: I don't have any pending future:  " + futureList.noneAwaited());
        }
    }

    public void waitAllFuture() {
        if (futureList != null) {
            logger.info("FutureReceiver: waiting all futures  ");
            futureList.waitAll();
        }
    }

    public void waitOneFuture() {
        if (futureList != null) {
            logger.info("FutureReceiver: waiting one future  ");
            futureList.waitOne();
        }
    }

    public void waitAndDisplayOneFuture() {
        Object tmp;
        if (futureList != null) {
            tmp = futureList.waitAndRemoveOne();
            logger.info("I got this future: " + tmp);
        }
    }

    public void waitAndDisplayAllFuture() {
        if (futureList != null) {
            this.waitAllFuture();
            this.displayAllFutures();
            //	System.out.println("I got this future: "+tmp);
        }
    }

    //call a method on the other agent to unblock it and wait for its replies
    public void unblockOtherObjectAndWaitAll() {
        blocked.go();
        this.waitAllFuture();
    }

    public void unblockOtherObjectAndWaitOne() {
        blocked.go();
        this.waitOneFuture();
    }
}
