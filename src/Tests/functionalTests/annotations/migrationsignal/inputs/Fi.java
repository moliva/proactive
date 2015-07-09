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

import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extensions.annotation.MigrationSignal;


@ActiveObject
public class Fi {

    // OK - both branches have migrateTo last 
    @MigrationSignal
    public void migrateToRight(boolean onCondition) throws MigrationException {
        if (onCondition) {
            org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
        } else {
            System.out.println("I refuze to migrate!");
            org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
        }
    }

    // error - else branch fwcked up
    @MigrationSignal
    public String migrateToWrong(boolean onCondition) throws MigrationException {
        if (onCondition) {
            org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
            return ""; // the sweet C-style hakz
        } else {
            org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
            System.out.println("I refuze to migrate!");
            return "";
        }
    }

    @MigrationSignal
    public void migrateToStSt(boolean onCondition) throws MigrationException {
        if (onCondition)
            migrateToRight(onCondition);
        else
            migrateToWrong(onCondition);
    }

    @MigrationSignal
    public String migrateToBlSt(boolean onCondition) throws MigrationException {
        if (onCondition) {
            System.out.println("Ich will migrate");
            migrateToRight(onCondition);
        } else
            migrateToWrong(onCondition);
        return "okay";
    }

    // wrong on one of the branches
    @MigrationSignal
    public void migrateToStBl(boolean onCondition) throws MigrationException {
        if (onCondition)
            migrateToRight(onCondition);
        else {
            System.out.println("Ich will migrate");
            migrateToWrong(onCondition);
            System.out.println("I'll fwck everything up!");
        }
    }

}
