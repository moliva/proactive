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
package org.objectweb.proactive.core.event;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.migration.MigrationException;


/**
 * <p>
 * <code>MigrationEvent</code>s occur during the migration of an active object. Several type
 * allow to determine when the event occured in the process of migration.
 * </p>
 *
 * @author The ProActive Team
 * @version 1.0,  2001/10/23
 * @since   ProActive 0.9
 *
 */
public class MigrationEvent extends ProActiveEvent implements java.io.Serializable {
    public static final int BEFORE_MIGRATION = 10;
    public static final int AFTER_MIGRATION = 20;
    public static final int RESTARTING_AFTER_MIGRATING = 30;
    public static final int MIGRATION_EXCEPTION = 40;

    /**
     * Creates a new <code>MigrationEvent</code> occuring during the migration of the
     * active object linked to the given body.
     * @param body the body associated to the migrating active object
     * @param type a number specifying when in the process of migration the event occured.
     */
    public MigrationEvent(Body body, int type) {
        super(body, type);
    }

    /**
     * Creates a new <code>MigrationEvent</code> based on an exception occuring during the process of migration.
     * @param exception the exception that occured
     */
    public MigrationEvent(MigrationException exception) {
        super(exception, MIGRATION_EXCEPTION);
    }
}
