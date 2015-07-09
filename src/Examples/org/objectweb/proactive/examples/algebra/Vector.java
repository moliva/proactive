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
package org.objectweb.proactive.examples.algebra;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class Vector extends Object implements Serializable, Cloneable {
    static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);
    int size;
    double[] elements;

    public Vector() {
        super();
    }

    public Vector(int _size) {
        super();
        this.elements = new double[_size];
        this.size = _size;
    }

    public Vector(double[] tab) {
        this(tab.length);
        int index;
        for (index = 0; index < tab.length; index++) {
            this.setElement(index, tab[index]);
        }
    }

    public void randomizeFillIn() {
        int i;

        for (i = 0; i < this.size; i++) {
            this.setElement(i, Math.random());
        }
        return;
    }

    public int getSize() {
        return this.size;
    }

    public synchronized void setElement(int index, double x) {
        this.elements[index] = x;
        return;
    }

    public synchronized double getElement(int index) {
        return this.elements[index];
    }

    public synchronized Vector multiplicate(double a) {
        int index;
        Vector v;

        v = new Vector(this.size);

        for (index = 0; index < this.size; index++) {
            v.setElement(index, a * this.getElement(index));
        }

        return v;
    }

    public double distance(Vector _v) {
        if (_v.getSize() != this.getSize()) {
            return -1;
        }

        int i;
        double max = 0;
        double current = 0;
        for (i = 0; i < _v.getSize(); i++) {
            current = Math.abs(_v.getElement(i) - this.getElement(i));
            if (current > max) {
                max = current;
            }
        }
        return max;
    }

    public Vector concat(Vector _v) {
        int i;
        Vector result = new Vector(this.getSize() + _v.getSize());

        for (i = 0; i < this.getSize(); i++) {
            result.setElement(i, this.getElement(i));
        }
        for (i = 0; i < _v.getSize(); i++) {
            result.setElement(i + this.getSize(), _v.getElement(i));
        }
        return result;
    }

    public void display() {
        int i;
        logger.info("___");
        for (i = 0; i < this.size; i++) {
            logger.info("|" + this.getElement(i) + "|");
        }
        logger.info("---");
        return;
    }

    public synchronized Vector add(Vector a) {
        int index;
        Vector v;

        v = new Vector(this.size);

        for (index = 0; index < size; index++) {
            v.setElement(index, a.getElement(index) + this.getElement(index));
        }

        return v;
    }
}
