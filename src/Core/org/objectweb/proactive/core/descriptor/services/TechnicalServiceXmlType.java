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
package org.objectweb.proactive.core.descriptor.services;

import java.util.Map;


public class TechnicalServiceXmlType {
    private String id;
    private Class<?> type;
    private Map<String, String> args;

    public TechnicalServiceXmlType() {
    }

    public TechnicalServiceXmlType(String id, Class<?> type, Map<String, String> args) {
        this.id = id;
        this.type = type;
        this.args = args;
    }

    /**
     * @return Returns the args.
     */
    public Map<String, String> getArgs() {
        return args;
    }

    /**
     * @param args The args to set.
     */
    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Returns the type.
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(Class<?> type) {
        this.type = type;
    }
}
