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

public class SearchModel extends javax.swing.table.AbstractTableModel {
    protected static int NUM_COLUMNS = 3;
    protected static int START_NUM_ROWS = 17;
    protected int nextEmptyRow = 0;
    protected int numRows = 0;
    protected java.util.Vector data = null;

    public SearchModel() {
        data = new java.util.Vector();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Key";
            case 1:
                return "Value";
            case 2:
                return "Order";
        }
        return "";
    }

    public synchronized int getColumnCount() {
        return NUM_COLUMNS;
    }

    public synchronized int getRowCount() {
        if (numRows < START_NUM_ROWS) {
            return START_NUM_ROWS;
        } else {
            return numRows;
        }
    }

    public synchronized Object getValueAt(int row, int column) {
        try {
            String[] t = (String[]) data.elementAt(row);
            switch (column) {
                case 0:
                    return t[0];
                case 1:
                    return t[1];
                case 2:
                    return t[2];
            }
        } catch (Exception e) {
        }
        return "";
    }

    public synchronized void updateKeyValue(String[] keyValue) {
        // Find the key
        String key = keyValue[0];
        String[] kv = null;
        int index = -1;
        boolean found = false;
        boolean addedRow = false;

        int i = 0;

        while (!found && (i < nextEmptyRow)) {
            kv = (String[]) data.elementAt(i);
            if ((kv != null) && (key.compareTo(kv[0]) == 0)) {
                found = true;
                index = i;
            } else {
                i++;
            }
        }

        // Update old row
        if (found) {
            data.setElementAt(keyValue, index);
        }
        // Add new row
        else {
            if (numRows <= nextEmptyRow) {
                // Add a row
                numRows++;
                addedRow = true;
            }
            index = nextEmptyRow;
            data.addElement(keyValue);
        }

        nextEmptyRow++;

        // Notify listeners that the data changed.
        if (addedRow) {
            fireTableRowsInserted(index, index);
        } else {
            fireTableRowsUpdated(index, index);
        }
    }

    public synchronized void clear() {
        int oldNumRows = numRows;

        numRows = 0;
        data.removeAllElements();
        nextEmptyRow = 0;

        if (oldNumRows > START_NUM_ROWS) {
            fireTableRowsDeleted(START_NUM_ROWS, oldNumRows - 1);
        }
        fireTableRowsUpdated(0, START_NUM_ROWS - 1);
    }
}
