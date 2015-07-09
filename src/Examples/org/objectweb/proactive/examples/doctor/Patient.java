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
package org.objectweb.proactive.examples.doctor;

import org.objectweb.proactive.extensions.annotation.ActiveObject;


@ActiveObject
public class Patient {
    private int id;
    private RandomTime rand;
    private Office off;
    private long meanWell;
    private long sigmaWell;

    public Patient() {
    }

    public Patient(Integer _id, Long _meanWell, Long _sigmaWell, Office _off, RandomTime _rand) {
        id = _id.intValue();
        meanWell = _meanWell.longValue();
        sigmaWell = _sigmaWell.longValue();
        off = _off;
        rand = _rand;
    }

    public void init() {
        isWell();
    }

    public void isWell() {
        long temps = rand.gaussianTime(meanWell, sigmaWell);

        try {
            Thread.sleep(temps);
        } catch (InterruptedException e) {
        }

        off.patientSick(id);
    }

    public void hasDoctor(int _doc) {
    }

    public void receiveCure(Cure _cure) {
        isWell();
    }
}
