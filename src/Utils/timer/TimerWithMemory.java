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

import java.io.Serializable;


/**
 * A timer which retains the values measured in addition to calculating the average
 */

public class TimerWithMemory extends AverageMicroTimer implements Timer, Serializable {
    protected long[] memory;
    protected int position;

    public TimerWithMemory() {
        this.memory = new long[10];
        this.position = 0;
    }

    public TimerWithMemory(String name) {
        this();
        this.name = name;
    }

    @Override
    public void stop() {
        if (running) {
            timer.stop();
            currentElapsed += timer.getCumulatedTime();
            this.total += currentElapsed;
            this.nbrValues++;
            this.addToMemory(currentElapsed);
            currentElapsed = 0;
            running = false;
        }
    }

    /**
     * Called only to perform some testing
     * @param time
     */
    protected void addToMemoryTest(long time) {
        this.total += time;
        this.nbrValues++;
        this.addToMemory(time);
    }

    protected void addToMemory(long time) {
        if (this.position == this.memory.length) {
            //not enough space, need to increase the array
            long[] tmp = new long[2 * this.memory.length];
            System.arraycopy(memory, 0, tmp, 0, memory.length);
            this.memory = tmp;
        }
        this.memory[position] = time;
        this.position++;
    }

    /**
     * return a copy of the memory of this timer
     * @return a copy of the memory
     */
    public long[] getMemory() {
        if (this.position > 0) {
            long[] tmp = new long[this.position];
            System.arraycopy(memory, 0, tmp, 0, this.position);
            return tmp;
        } else {
            return null;
        }
    }

    public double getVariance() {
        double average = this.getAverage();
        double total2 = 0;
        for (int i = 0; i < this.position; i++) {
            total2 += Math.pow(memory[i], 2);
        }
        return ((total2 / this.position) - Math.pow(average, 2));
    }

    public double getStandardDeviation() {
        return Math.sqrt(this.getVariance());
    }

    @Override
    public void reset() {
        super.reset();
        this.memory = new long[10];
        this.position = 0;
    }

    @Override
    public void dump() {
        System.out.println("Dumping memory");
        for (int i = 0; i < this.position; i++) {
            System.out.print(memory[i] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        TimerWithMemory mt = new TimerWithMemory("Test");
        for (int i = 0; i < 10; i++) {
            mt.addToMemoryTest(i);
        }
        mt.dump();
        System.out.println("Requesting memory and displaying it");
        long[] m = mt.getMemory();
        for (int i = 0; i < m.length; i++) {
            System.out.print(m[i] + " ");
        }
        System.out.println();
        System.out.println("Average is " + mt.getAverage());
        System.out.println("Variance is " + mt.getVariance());

        System.out.println("Standard deviation is " + mt.getStandardDeviation());
    }
}
