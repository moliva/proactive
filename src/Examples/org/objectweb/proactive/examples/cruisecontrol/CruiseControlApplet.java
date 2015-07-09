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
package org.objectweb.proactive.examples.cruisecontrol;

import org.objectweb.proactive.core.config.ProActiveConfiguration;


/** The Applet : CruiseControlApplet
 * models a car and a speed controller,
 * and implements the primitives of the ProActive package.
 *
 *
 * This class is responsible for the painting of the Car and Cruise Control Pane,
 * recieve the user interaction on the 2 panes and send the corresponding messages to the Interface Dispatcher
 *
 */
public class CruiseControlApplet extends org.objectweb.proactive.examples.StandardFrame {
    // components

    /**
     * the Pane associated with the Active Object CruiseControl
     */
    private CruiseControlPanel controlPane;

    /**
     * the Pane associated with the Active Object CarModel
     */
    private CarPanel carPane;

    /**
     * the active Object working as dispatcher between the Applet and the others Active Objects
     */
    private Interface activeObject;

    public CruiseControlApplet(String name, int width, int height) {
        super(name);
        createActiveObject();
        init(width, height);
    }

    public static void main(String[] arg) {
        ProActiveConfiguration.load();
        new CruiseControlApplet("Cruise Control", 840, 420);
    }

    // Methods

    /**
     * Initializes the Applet
     * then creates the 3 objects Panels
     *
     * then creates the object Interface
     */
    public void createActiveObject() {
        try {
            activeObject = (Interface) org.objectweb.proactive.api.PAActiveObject.turnActive(new Interface(
                this));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        activeObject.initialize();
    }

    @Override
    protected void start() {
    }

    @Override
    protected javax.swing.JPanel createRootPanel() {
        javax.swing.JPanel rootPanel = new javax.swing.JPanel(new java.awt.GridLayout(1, 1));
        carPane = new CarPanel(this, activeObject);
        controlPane = new CruiseControlPanel(this, activeObject);
        javax.swing.JSplitPane horizontalSplitPane = new javax.swing.JSplitPane(
            javax.swing.JSplitPane.HORIZONTAL_SPLIT);
        horizontalSplitPane.setDividerLocation(500);
        horizontalSplitPane.setLeftComponent(carPane);
        horizontalSplitPane.setRightComponent(controlPane);
        rootPanel.add(horizontalSplitPane);
        return rootPanel;
    }

    /**
     * Calls the setDistance Method of the carPane
     * and asks it to change the distance and to repaint it
     */
    public void setDistance(double distance) {
        carPane.setDistance(1000 * distance);
    }

    /**
     * Calls the setSpeed Method of the carPane
     * to change the speed
     */
    public void setSpeed(double speed) {
        carPane.setSpeed(speed);
    }

    /**
     * Calls the setDesiredSpeed of the control pane,
     * asks the applet to repaint it
     */
    public void setDesiredSpeed(double m_speed) {
        controlPane.setDesiredSpeed(m_speed);
        repaint();
    }

    /**
     * Calls the engineOff Method of the carPane and the controlPane
     */
    public void engineOff() {
        controlPane.controlOff();
        carPane.engineOff();
    }

    public void engineOn() {
        carPane.engineOn();
    }

    ////////////////////////////////////////////////////////////

    /**
     * Calls the setAlpha Method of the carPane to change the road incline
     */
    public void setAlpha(double m_alpha) {
        carPane.setAlpha(m_alpha);
    }

    public void setAcceleration(double m_acc) {
        carPane.setAcceleration(m_acc);
    }

    public void controlPaneOff() {
        controlPane.controlOff();
    }

    public void brake() {
        carPane.brake();
    }

    public void controlOn() {
        activeObject.controlOn();
        carPane.controlOn();
        controlPane.controlOn();
    }

    public void controlOff() {
        activeObject.controlOff();
        carPane.controlOff();
        controlPane.controlOff();
    }
}
