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
package org.objectweb.proactive.examples.philosophers;

import org.objectweb.proactive.core.config.ProActiveConfiguration;


public class AppletPhil extends org.objectweb.proactive.examples.StandardFrame {
    //  private javax.swing.JButton bStart;
    private String url;
    private DinnerLayout theLayout;
    private javax.swing.JPanel theLayoutPanel;

    public AppletPhil(String name, int width, int height) {
        super(name, width, height);
    }

    public static void main(String[] args) {
        ProActiveConfiguration.load();
        AppletPhil phil = new AppletPhil("Philosophers", 450, 300);
        phil.receiveMessage("Applet running...");
        if (args.length == 1) {
            phil.setURL(args[0]);
        }
        phil.go();
    }

    private void go() {
        try {
            /*
             * le Layout est necessairement actif, puisqu'il est referenc? par tous les autres
             * objets.
             */
            theLayout = (DinnerLayout) org.objectweb.proactive.api.PAActiveObject.turnActive(theLayout);
            if (url != null) {
                theLayout.setNode(url);
            }

            /*
             * Builds the active Table and Philosophers:
             */
            org.objectweb.proactive.api.PAFuture.waitFor(theLayout.init());
            theLayout.activateButtons();
            receiveMessage("Objects activated...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setURL(String url) {
        this.url = url;
    }

    /* Called by AppletWrapper before creating the toplevel Frame: */
    @Override
    protected void start() {
    }

    //
    // -- PROTECTED METHODS -----------------------------------------------
    //

    /*
     * createRootPanel: abstract method of AppletWrapper. result: the JPanel to be inserted in the
     * upper part of the Main Frame.
     */
    @Override
    protected javax.swing.JPanel createRootPanel() {
        // Get the images
        javax.swing.Icon[] imgArray = new javax.swing.Icon[5];
        try {
            ClassLoader c = this.getClass().getClassLoader();
            imgArray[0] = new javax.swing.ImageIcon(c
                    .getResource("org/objectweb/proactive/examples/philosophers/think.gif"));
            imgArray[1] = new javax.swing.ImageIcon(c
                    .getResource("org/objectweb/proactive/examples/philosophers/wait.gif"));
            imgArray[2] = new javax.swing.ImageIcon(c
                    .getResource("org/objectweb/proactive/examples/philosophers/eat.gif"));
            imgArray[3] = new javax.swing.ImageIcon(c
                    .getResource("org/objectweb/proactive/examples/philosophers/fork0.gif"));
            imgArray[4] = new javax.swing.ImageIcon(c
                    .getResource("org/objectweb/proactive/examples/philosophers/fork1.gif"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // the DinnerLayout constructor creates the graphical objects:
        theLayout = new DinnerLayout(imgArray);
        return theLayout.getDisplay();
    }
}
