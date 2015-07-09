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
package functionalTests.annotations.migrationstrategy;

import junit.framework.Assert;
import functionalTests.annotations.CTreeTest;


/**
 * Test for the @OnDeparture/@OnArrival annotations; ctree version
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class TestCTree extends CTreeTest {

    @org.junit.Test
    public void action() throws Exception {

        // misplaced annotation
        Assert.assertEquals(new Result(2, 0), checkFile("MisplacedAnnotation"));

        // "simple" tests
        Assert.assertEquals(OK, checkFile("SimpleAccept"));
        Assert.assertEquals(new Result(2, 0), checkFile("ErrorReturnType"));
        Assert.assertEquals(new Result(2, 0), checkFile("ErrorParameters"));

        // "complex" tests
        Assert.assertEquals(new Result(2, 0), checkFile("ErrorMixedDeparture"));
        Assert.assertEquals(new Result(2, 0), checkFile("ErrorMixedArrival"));
    }
}