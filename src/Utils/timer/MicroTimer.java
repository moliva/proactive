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
package timer;

public class MicroTimer implements Timeable {
    private static boolean nativeMode;

    static {
        try {
            System.loadLibrary("MicroTimer");
            nativeMode = true;
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println("WARNING: couldn't load native lib, falling back to milliseconds");
            nativeMode = false;
        }
    }

    private long[] startTime;
    private long[] endTime;

    public native long[] currentTime();

    public long startTime2;
    public long endTime2;

    public MicroTimer() {
    }

    public void start() {
        if (nativeMode) {
            this.startTime = currentTime();
            this.endTime = currentTime();
        } else {
            this.startTime2 = System.currentTimeMillis();
            this.endTime2 = this.startTime2;
        }
    }

    /**
     * Stop the timer and
     * returns the cumulated time
     */
    public void stop() {
        if (MicroTimer.nativeMode) {
            this.endTime = currentTime();
        } else {
            this.endTime2 = System.currentTimeMillis();
        }
    }

    /**
     * Return the newly computed cumulated time
     * as measured by this MicroTimer
     * in microseconds
     */
    public long getCumulatedTime() {
        if (nativeMode) {
            long[] tmp = this.updateCumulatedTime(startTime, endTime); //this.stop();
            return (tmp[0] * 1000000) + tmp[1];
        } else {
            return (endTime2 - startTime2);
        }
    }

    protected long[] updateCumulatedTime(long[] t1, long[] t2) {
        long[] tmp = new long[2]; // this.cumulatedTime;
        tmp[0] = t2[0] - t1[0];

        if ((t2[1] - t1[1]) < 0) {
            //one second has gone
            tmp[0]--;
            tmp[1] = (t2[1] + 1000000) - t1[1];
        } else {
            tmp[1] += (t2[1] - t1[1]);
        }
        return tmp;
    }

    public String getUnit() {
        if (nativeMode) {
            return "micros";
        } else {
            return "millis";
        }
    }

    public static void main(String[] args) {
        MicroTimer micro = new MicroTimer();
        System.out.println("Test starting...");
        micro.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        micro.stop();
        System.out.println("After 1000ms : " + micro.getCumulatedTime() + micro.getUnit());
    }
}
