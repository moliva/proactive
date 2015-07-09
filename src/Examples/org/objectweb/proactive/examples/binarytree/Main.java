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
package org.objectweb.proactive.examples.binarytree;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


@ActiveObject
public class Main {
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    public static void main(String[] args) {
        Main theMainActiveObject = null;

        // Creates an active instance of this class
        ProActiveConfiguration.load();
        try {
            theMainActiveObject = org.objectweb.proactive.api.PAActiveObject.newActive(Main.class, null);
        } catch (Exception e) {
            logger.error(e);
            System.exit(1);
        }

        // Asks it to perform the test
        theMainActiveObject.doStuff();
        return;
    }

    public void doStuff() {
        BinaryTree myTree = null;

        // This is the code for instanciating a passive version of the binary tree
        //        myTree = new BinaryTree ();
        // This is the code for instanciating an active version of the binary tree
        // If you want to test the pasive version of this test program, simply comment out
        // the next line and comment in the line of code above
        //
        // * The first parameter means that we want to get an active instance of class org.objectweb.proactive.examples.binarytree.ActiveBinaryTree
        // * The second parameter ('null') means we instancate this object through its empty (no-arg) constructor
        //  'null' is actually a convenience for 'new Object [0]'
        // * The last parameter 'null' means we want to instanciate this object in the current virtual machine
        try {
            //          Object o = new org.objectweb.proactive.examples.binarytree.ActiveBinaryTree ();
            myTree = org.objectweb.proactive.api.PAActiveObject.newActive(ActiveBinaryTree.class, null);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }

        // Now we insert 4 elements in the tree
        // Note that this code is the same for the passive or active version of the tree
        myTree.put(1, "one");
        myTree.put(2, "two");
        myTree.put(3, "three");
        myTree.put(4, "four");
        // Now we get all these 4 elements out of the tree
        // method get in class BinaryTree returns a future object if
        // myTree is an active object, but as System.out actually calls toString()
        // on the future, the execution of each of the following 4 calls to System.out
        // blocks until the future object is available.
        ObjectWrapper tmp1 = myTree.get(3);
        ObjectWrapper tmp2 = myTree.get(4);
        ObjectWrapper tmp3 = myTree.get(2);
        ObjectWrapper tmp4 = myTree.get(1);
        logger.info("Value associated to key 1 is " + tmp4);
        logger.info("Value associated to key 2 is " + tmp3);
        logger.info("Value associated to key 3 is " + tmp1);
        logger.info("Value associated to key 4 is " + tmp2);
        logger.info("Use CTRL+C to stop the program");
    }
}
