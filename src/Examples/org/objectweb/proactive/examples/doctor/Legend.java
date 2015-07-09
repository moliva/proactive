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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Legend extends Dialog {
    public class LegendPanel extends Panel {
        DisplayPanel display;

        public LegendPanel(DisplayPanel _display) {
            display = _display;
        }

        @Override
        public void update(Graphics g) {
            FontMetrics fm = g.getFontMetrics();
            int h = fm.getAscent();

            g.setColor(display.wellOn);
            g.fillOval(30, 30 - h, h, h);
            g.setColor(display.patColor);
            g.drawString("Healthy patient", 40 + h, 30);

            g.setColor(display.sickOn);
            g.fillOval(30, (40 + h) - h, h, h);
            g.setColor(display.patColor);
            g.drawString("Sick patient", 40 + h, 40 + h);

            g.setColor(display.cureOn);
            g.fillOval(30, (50 + (2 * h)) - h, h, h);
            g.setColor(display.patColor);
            g.drawString("Patient with doctor", 40 + h, 50 + (2 * h));
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 90);
        }

        @Override
        public void paint(Graphics g) {
            update(g);
        }
    }

    public Legend(Frame dw, DisplayPanel display) {
        super(dw, "Legend", false);

        Point parLoc = dw.getLocation();
        setLocation(parLoc.x + (dw.getSize().width), parLoc.y);

        LegendPanel pan = new LegendPanel(display);
        add(pan);

        pack();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
    }
}
