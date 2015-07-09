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
package org.objectweb.proactive.examples.plugtest;

/**
 * @author The ProActive Team
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import org.objectweb.proactive.Body;
import org.objectweb.proactive.EndActive;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


@ActiveObject
public class ObjA implements InitActive, EndActive {
    protected String s;
    protected ObjB b;
    protected String node;
    private int i;

    public ObjA() {
    }

    public ObjA(String s, ObjB b) {
        this.s = s;
        this.i = new Integer(s.substring("object".length(), s.length())).intValue();
        this.b = b;
    }

    public String getInfo() {
        String property = System.getProperty("proactive.property");
        if (property != null) {
            return s + " " + property;
        } else {
            return s;
        }
    }

    public int getNumber() {
        return i;
    }

    public ObjB getB() {
        return b;
    }

    public String getNode() {
        return node;
    }

    @Override
    public String toString() {
        return s;
    }

    public String sayHello() {
        return b.sayHello();
    }

    //       -- implements InitActive
    public void initActivity(Body body) {
        node = body.getNodeURL();
        System.out.println("I'm starting my activity");
        System.out.println(" I am on node " + node);
    }

    public void endActivity(Body body) {
        System.out.println("I have finished my activity");
    }
}
