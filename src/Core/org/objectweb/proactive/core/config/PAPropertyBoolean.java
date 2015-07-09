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
package org.objectweb.proactive.core.config;

import org.apache.log4j.Logger;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * A boolean ProActive Property
 *
 * @since ProActive 4.3.0
 */
@PublicAPI
public class PAPropertyBoolean extends PAPropertyImpl {

    public PAPropertyBoolean(String name, boolean isSystemProp) {
        super(name, PropertyType.BOOLEAN, isSystemProp, null);
    }

    public PAPropertyBoolean(String name, boolean isSystemProp, boolean defaultValue) {
        super(name, PropertyType.BOOLEAN, isSystemProp, Boolean.toString(defaultValue));
    }

    final public boolean getValue() {
        String str = super.getValueAsString();
        return Boolean.parseBoolean(str);
    }

    final public boolean isTrue() {
        return this.getValue();
    }

    final public void setValue(boolean value) {
        super.internalSetValue(Boolean.toString(value));
    }

    @Override
    public boolean isValid(String value) {
        if ("true".equals(value) || "false".equals(value))
            return true;

        return false;
    }

}
