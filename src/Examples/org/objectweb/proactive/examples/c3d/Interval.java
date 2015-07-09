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
package org.objectweb.proactive.examples.c3d;

/**
 * Used to represent an interval of pixels to draw (or that were drawn) as part of an Image2D.
 * these are integer values, acting as counters on arrays, to says which values in a pixel
 * array are to be set/read.
 */
public class Interval implements java.io.Serializable {
    public int number; // each interval has a number (nice to see the progression - no use in the code)
    public int totalImageWidth; // width of total image
    public int totalImageHeight; // height of total image
    public int yfrom; // which line does this interval start from
    public int yto; // line of end of this interval

    public Interval(int number, int totalWidth, int totalHeight, int yfrom, int yto) {
        this.number = number;
        this.totalImageWidth = totalWidth;
        this.totalImageHeight = totalHeight;
        this.yfrom = yfrom;
        this.yto = yto;
    }
}
