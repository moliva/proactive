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
package functionalTests.mop.annotations.cache;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;

import functionalTests.FunctionalTest;


public class TestCacheAnnotation extends FunctionalTest {

    /**
     * test the @Cache annotation, returned objects must always be the same
     * but differ from the initial one due to serialization.
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    @Test
    public void test() throws ActiveObjectCreationException, NodeException {

        Object initialObject = new SerializableClass();
        int initialI = 25;

        CacheAnnotationClass cac = PAActiveObject.newActive(CacheAnnotationClass.class, new Object[] {
                initialI, initialObject });

        Object cachedObject = cac.getO();
        Object secondObject = cac.getO();

        Assert.assertNotSame(cachedObject, initialObject);
        Assert.assertSame(cachedObject, secondObject);

    }

}
