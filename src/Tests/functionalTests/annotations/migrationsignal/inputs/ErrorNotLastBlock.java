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
package functionalTests.annotations.migrationsignal.inputs;

import static org.objectweb.proactive.api.PAMobileAgent.migrateTo;

import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extensions.annotation.MigrationSignal;


@ActiveObject
public class ErrorNotLastBlock {

    // error - one of the inner blocks is wrong
    @MigrationSignal
    public void freakyShiet(Node node) throws MigrationException {
        {
            {
                System.out.println();
                System.out.println();
                PAMobileAgent.migrateTo(node);
                System.out.println();
                System.out.println();
            }
        }
        {
            {
                {
                    System.out.println();
                    System.out.println();
                    migrateTo(node);
                }
            }
            {
                System.out.println();
                System.out.println();
                org.objectweb.proactive.api.PAMobileAgent.migrateTo(node);
            }
        }
    }

    // should be OK
    @MigrationSignal
    public void moreBlocks(Node node) throws MigrationException {
        {
            {
                System.out.println();
                System.out.println();
                migrateTo(node);
            }
        }
        {
            System.out.println();
            System.out.println();
        }
    }

    // error - if the inner block has the call, the outer block shouldn't have it
    @MigrationSignal
    public void blocksWrong(Node node) throws MigrationException {

        {
            System.out.println();
            org.objectweb.proactive.api.PAMobileAgent.migrateTo(node);
        }

        System.out.println();
        PAMobileAgent.migrateTo(node);
    }

    // OK - corrected above
    @MigrationSignal
    public void blocksRight(Node node) throws MigrationException {

        {
            System.out.println();
        }

        System.out.println();
        PAMobileAgent.migrateTo(node);
    }
}
