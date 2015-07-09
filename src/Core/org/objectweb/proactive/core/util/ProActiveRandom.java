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
package org.objectweb.proactive.core.util;

import java.security.SecureRandom;


/**
 * Provides an easy to get a random values for a SecureRandom PRNG
 *
 * A single PRNG is shared for the whole ProActive Runtime.
 *
 * @see SecureRandom
 */
public class ProActiveRandom {
    final static private SecureRandom prng = new SecureRandom();

    static private char[] symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    /** Returns the next pseudorandom, uniformly distributed boolean value from this random number generator's sequence. */
    synchronized static public boolean nextBoolean() {
        return prng.nextBoolean();
    }

    /** Generates random bytes and places them into a user-supplied byte array. */
    synchronized static public void nextBytes(byte[] bytes) {
        prng.nextBytes(bytes);
    }

    /** Returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0 from this random number generator's sequence. */
    synchronized static public double nextDouble() {
        return prng.nextDouble();
    }

    /**  Returns the next pseudorandom, uniformly distributed float  value between 0.0 and 1.0 from this random number generator's sequence. */
    synchronized static public float nextFloat() {
        return prng.nextFloat();
    }

    /** Returns the next pseudorandom, uniformly distributed int  value from this random number generator's sequence.*/
    synchronized static public int nextInt() {
        return prng.nextInt();
    }

    /** Returns the next pseudorandom, uniformly distributed positive int  value from this random number generator's sequence.*/
    synchronized static public int nextPosInt() {
        return prng.nextInt(Integer.MAX_VALUE);
    }

    /** Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value (exclusive), drawn from this random number generator's sequence. */
    synchronized static public int nextInt(int n) {
        return prng.nextInt(n);
    }

    /** Returns the next pseudorandom, uniformly distributed long  value from this random number generator's sequence. */
    synchronized static public long nextLong() {
        return prng.nextLong();
    }

    synchronized static public long nextPosLong() {
        return Math.abs(nextLong());
    }

    /**
     * Returns a random string of fixed length
     * 
     * The string will only characters from {@link ProActiveRandom#symbols} 
     * (upper case alphanumeric ASCII symbols). 
     * 
     * @param size the length of the random string 
     * @return A random string
     */
    synchronized static public String nextString(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(symbols[nextInt(symbols.length)]);
        }

        return sb.toString();
    }
}
