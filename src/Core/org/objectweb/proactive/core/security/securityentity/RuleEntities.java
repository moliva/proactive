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
package org.objectweb.proactive.core.security.securityentity;

import java.util.ArrayList;

import org.objectweb.proactive.core.security.securityentity.RuleEntity.Match;


public class RuleEntities extends ArrayList<RuleEntity> {

    /**
     *
     */
    public RuleEntities() {
        super();
    }

    public RuleEntities(RuleEntities entities) {
        super(entities);
    }

    public Match match(Entities entities) {
        if (isEmpty()) {
            return Match.DEFAULT;
        }

        for (RuleEntity entity : this) {
            if (entity.match(entities) == Match.FAILED) {
                return Match.FAILED;
            }
        }
        return Match.OK;
    }

    /**
     * level represents the specificity of the target entities of a rule, higher
     * level is more specific
     *
     * @return the maximum level among the RuleEnties
     */
    public int getLevel() {
        int maxLevel = RuleEntity.UNDEFINED_LEVEL;
        for (RuleEntity rule : this) {
            if (maxLevel < rule.getLevel()) {
                maxLevel = rule.getLevel();
            }
        }
        return maxLevel;
    }

    public boolean contains(Entity entity) {
        for (RuleEntity rule : this) {
            if (rule.match(entity) == Match.OK) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String result = new String();
        for (RuleEntity rule : this) {
            result += rule.toString() + ",";
        }
        return result;
    }
}
