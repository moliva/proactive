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
package org.objectweb.proactive.core.body.ft.servers.util;

import java.util.Hashtable;


/**
 * This class is a generic job queue.
 * @author The ProActive Team
 * @since 3.0
 */
public class ActiveQueue extends Thread {
    private java.util.ArrayList<ActiveQueueJob> queue;
    private int counter;
    private boolean kill;
    private Hashtable<ActiveQueueJob, JobBarrier> barriers;

    public ActiveQueue(String name) {
        queue = new java.util.ArrayList<ActiveQueueJob>();
        counter = 0;
        kill = false;
        barriers = new Hashtable<ActiveQueueJob, JobBarrier>();
        this.setName(name);
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //

    /**
     * return the current queue of jobs to perform
     * @return the current queue of jobs to perform
     */
    public java.util.ArrayList<ActiveQueueJob> getQueue() {
        return queue;
    }

    /**
     * Add a job in the active queue.
     * @param j the job to add.
     */
    public synchronized void addJob(ActiveQueueJob j) {
        queue.add(j);
        counter++;
        notifyAll();
    }

    /**
     * Add a job in the active queue. A barrier is created for this job; waiting on this
     * barrier is blocking until the job j ends.
     * @param j the job to add.
     * @return a barrier on the job j;
     */
    public synchronized JobBarrier addJobWithBarrier(ActiveQueueJob j) {
        JobBarrier b = new JobBarrier();
        this.barriers.put(j, b); // hash method of job !!!
        queue.add(j);
        counter++;
        notifyAll();
        return b;
    }

    /**
     * Return the oldest job in queue and remove it from the queue
     * @return the oldest job in queue and remove it from the queue
     */
    public synchronized ActiveQueueJob removeJob() {
        counter--;
        return (queue.remove(0));
    }

    /**
     * Stop the thread.
     */
    public synchronized void killMe() {
        kill = true;
        notifyAll();
    }

    /**
     * The run method of the thread. Serve jobs in a FIFO manner until
     * <code>killMe()</code> is called.
     */
    @Override
    public void run() {
        while (true) {
            // if there is no job to do, wait...
            waitForJob();
            // if someone want to kill me, break the loop
            if (kill) {
                break;
            }

            // there are jobs to do !           
            ActiveQueueJob toDo = this.removeJob();
            if (toDo != null) {
                toDo.doTheJob();
                // unlock barrier if any
                JobBarrier b = (this.barriers.get(toDo));
                if (b != null) {
                    // this job is barriered
                    b.signalJobCompletion();
                    this.barriers.remove(toDo);
                }
            }
        }
    }

    // synchronized wait on job queue
    private synchronized void waitForJob() {
        try {
            while ((counter == 0) && !kill) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
