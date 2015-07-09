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
package functionalTests.pamr;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.extensions.pamr.protocol.TypeHelper;

import functionalTests.FunctionalTest;


public class TestTypeHelper extends FunctionalTest {

    @Test
    public void testIntBound() {
        Random rand = new Random();
        int[] data = new int[] { Integer.MIN_VALUE, -2, -1, 0, 1, 2, Integer.MAX_VALUE };
        byte[] buf = new byte[32];

        for (int i = 0; i < data.length; i++) {
            int retval;
            int val = data[i];
            int offset = rand.nextInt(24);

            TypeHelper.intToByteArray(val, buf, offset);
            retval = TypeHelper.byteArrayToInt(buf, offset);

            Assert.assertEquals(val, retval);
        }
    }

    @Test
    public void testRandomInt() {
        Random rand = new Random();
        byte[] buf = new byte[32];

        for (int i = 0; i < 10000000; i++) {
            int retval;
            int val = rand.nextInt();
            int offset = rand.nextInt(24);

            TypeHelper.intToByteArray(val, buf, offset);
            retval = TypeHelper.byteArrayToInt(buf, offset);

            Assert.assertEquals(val, retval);
        }
    }

    @Test
    public void testLongBound() {
        Random rand = new Random();
        long[] data = new long[] { Long.MIN_VALUE, Integer.MIN_VALUE, -2, -1, 0, 1, 2, Integer.MAX_VALUE,
                Long.MAX_VALUE };
        byte[] buf = new byte[32];

        for (int i = 0; i < data.length; i++) {
            long retval;
            long val = data[i];
            int offset = rand.nextInt(24);

            TypeHelper.longToByteArray(val, buf, offset);
            retval = TypeHelper.byteArrayToLong(buf, offset);
            Assert.assertTrue((0L + val) == (0L + retval));
        }
    }

    @Test
    public void testRandomLong() {
        Random rand = new Random();
        byte[] buf = new byte[32];

        for (int i = 0; i < 10000000; i++) {
            long retval;
            long val = rand.nextLong();
            int offset = rand.nextInt(24);

            TypeHelper.longToByteArray(val, buf, offset);
            retval = TypeHelper.byteArrayToLong(buf, offset);

            Assert.assertEquals(val, retval);
        }
    }

}
