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
package org.objectweb.proactive.examples;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Inria
 * @author The ProActive Team
 * @author The ProActive Team
 * @version 1.1
 */
public abstract class StandardFrame extends javax.swing.JFrame {
    static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);
    protected final static int MESSAGE_ZONE_HEIGHT = 250;
    protected String name;
    protected int width;
    protected int height;
    protected DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    protected transient JTextPane messageArea;
    protected transient Style regularStyle;
    protected javax.swing.JSplitPane verticalSplitPane;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public StandardFrame(String name, int width, int height) {
        super(name);
        this.name = name;
        init(width, height);
    }

    public StandardFrame(String name) {
        super(name);
        this.name = name;
    }

    public StandardFrame() {
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //
    public void receiveMessage(final String message) {
        final String date = dateFormat.format(new java.util.Date());
        final String threadName = Thread.currentThread().getName();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Document doc = messageArea.getDocument();
                try {
                    doc.insertString(doc.getLength(), date + " (" + threadName + ")\n      => " + message +
                        "\n", regularStyle);
                } catch (Exception e) {
                    logger.error("Couldn't insert initial text.");
                }
            }
        });
    }

    //Method with possibility of color text
    public void receiveMessage(final String message, final java.awt.Color color) {
        final String date = dateFormat.format(new java.util.Date());
        final String threadName = Thread.currentThread().getName();
        final Style s = messageArea.addStyle("colored", regularStyle);
        StyleConstants.setForeground(s, color);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Document doc = messageArea.getDocument();
                try {
                    doc
                            .insertString(doc.getLength(), date + " (" + threadName + ")\n      => ",
                                    regularStyle);
                    doc.insertString(doc.getLength(), message + "\n", s);
                } catch (Exception e) {
                    logger.error("Couldn't insert initial text.");
                }
            }
        });
    }

    //
    // -- PROTECTED METHODS -----------------------------------------------
    //
    protected void init(int width, int height) {
        initFrame(name, width, height);
        setVisible(true);
        start();
    }

    protected abstract void start();

    protected abstract javax.swing.JPanel createRootPanel();

    protected javax.swing.JPanel createMessageZonePanel(final javax.swing.JTextPane area) {
        Style styleDef = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        regularStyle = area.addStyle("regular", styleDef);
        area.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 10));

        JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());
        javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Messages");
        panel.setBorder(border);
        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.BorderLayout());

        // clear log button
        javax.swing.JButton clearLogButton = new javax.swing.JButton("clear messages");
        clearLogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                area.setText("");
            }
        });
        topPanel.add(clearLogButton, java.awt.BorderLayout.WEST);
        panel.add(topPanel, java.awt.BorderLayout.NORTH);
        javax.swing.JScrollPane pane = new javax.swing.JScrollPane(area);
        panel.add(pane, java.awt.BorderLayout.CENTER);
        return panel;
    }

    //
    // -- PRIVATE METHODS -----------------------------------------------
    //
    private void initFrame(String name, int width, int height) {
        java.awt.Container c = getContentPane();
        c.setLayout(new java.awt.GridLayout(1, 1));

        // create topPanel
        JPanel topPanel = new JPanel(new java.awt.GridLayout(1, 1));
        TitledBorder border = new javax.swing.border.TitledBorder(name);
        topPanel.setBorder(border);
        topPanel.add(createRootPanel());

        // create bottom Panel
        messageArea = new javax.swing.JTextPane();
        messageArea.setEditable(false);
        javax.swing.JPanel bottomPanel = createMessageZonePanel(messageArea);

        // create an vertical split Panel
        verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setDividerLocation(height);
        verticalSplitPane.setTopComponent(topPanel);
        verticalSplitPane.setBottomComponent(bottomPanel);
        c.add(verticalSplitPane);

        setSize(width, height + MESSAGE_ZONE_HEIGHT);
        setLocation(30, 30);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
