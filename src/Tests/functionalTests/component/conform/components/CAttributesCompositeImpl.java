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
package functionalTests.component.conform.components;

public class CAttributesCompositeImpl implements CAttributes {
    private boolean x1;
    private byte x2;
    private char x3;
    private short x4;
    private int x5;
    private long x6;
    private float x7;
    private double x8;
    private String x9;
    private boolean x11;

    // ATTRIBUTE CONTROLLER
    public boolean getX1() {
        return x1;
    }

    public void setX1(boolean x1) {
        this.x1 = x1;
    }

    public byte getX2() {
        return x2;
    }

    public void setX2(byte x2) {
        this.x2 = x2;
    }

    public char getX3() {
        return x3;
    }

    public void setX3(char x3) {
        this.x3 = x3;
    }

    public short getX4() {
        return x4;
    }

    public void setX4(short x4) {
        this.x4 = x4;
    }

    public int getX5() {
        return x5;
    }

    public void setX5(int x5) {
        this.x5 = x5;
    }

    public long getX6() {
        return x6;
    }

    public void setX6(long x6) {
        this.x6 = x6;
    }

    public float getX7() {
        return x7;
    }

    public void setX7(float x7) {
        this.x7 = x7;
    }

    public double getX8() {
        return x8;
    }

    public void setX8(double x8) {
        this.x8 = x8;
    }

    public String getX9() {
        return x9;
    }

    public void setX9(String x9) {
        this.x9 = x9;
    }

    public boolean getReadOnlyX10() {
        return true;
    }

    public void setWriteOnlyX11(boolean x11) {
        this.setX11(x11);
    }

    public void setX11(boolean x11) {
        this.x11 = x11;
    }

    public boolean getX11() {
        return x11;
    }
}
