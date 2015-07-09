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
package org.objectweb.proactive.extensions.calcium.diagnosis.inferences;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.calcium.diagnosis.causes.Cause;
import org.objectweb.proactive.extensions.calcium.statistics.Stats;


public abstract class AbstractInference implements Inference {
    static Logger logger = ProActiveLogger.getLogger(Loggers.SKELETONS_DIAGNOSIS);
    protected List<Cause> causes;
    protected List<Inference> inferences;
    protected double threshold;

    public AbstractInference(double threshold, Inference... inferences) {
        this(threshold, inferences, new Cause[0]);
    }

    public AbstractInference(double threshold, Cause... cause) {
        this(threshold, new Inference[0], cause);
    }

    public AbstractInference(double threshold, Inference[] inferences, Cause[] causes) {
        this.threshold = threshold;
        this.inferences = Arrays.asList(inferences);
        this.causes = Arrays.asList(causes);
    }

    public List<Cause> getCauses(Stats stats) {
        Vector<Cause> v = new Vector<Cause>();
        if (!hasSymptom(stats)) {
            return v;
        }

        v.addAll(causes);

        for (Inference inf : inferences) {
            v.addAll(inf.getCauses(stats));
        }

        return v;
    }

    /**
     * This condition returns true if the inference is symptomatic. That is to say,
     * further subinferences should be analyzed, and if no further subinferences
     * are present the causes linked to this inference should be returned.
     *
     * @param stats The statistics over which the inference's symptom analysis must be made
     * @return True if the inference is symptomatic, false otherwise.
     */
    abstract boolean hasSymptom(Stats stats);
}
