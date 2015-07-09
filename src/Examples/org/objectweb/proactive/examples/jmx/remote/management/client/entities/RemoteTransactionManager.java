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
package org.objectweb.proactive.examples.jmx.remote.management.client.entities;

import java.util.HashMap;


public class RemoteTransactionManager {
    private static RemoteTransactionManager instance;
    private RemoteTransactionGroup transactionGroup;
    private HashMap<Long, RemoteTransaction> idTransactions = new HashMap<Long, RemoteTransaction>();

    private RemoteTransactionManager() {
        this.transactionGroup = new RemoteTransactionGroup("Transactions");
    }

    public static RemoteTransactionManager getInstance() {
        if (instance == null) {
            instance = new RemoteTransactionManager();
        }
        return instance;
    }

    public RemoteTransactionGroup getTransactionsGroup() {
        return this.transactionGroup;
    }

    public void addTransaction(RemoteTransaction transaction) {
        this.transactionGroup.addEntity(transaction);
        this.idTransactions.put(transaction.getId(), transaction);
    }

    public void commitTransaction(long id) {
        RemoteTransaction transaction = this.idTransactions.get(id);
        transaction.setState(RemoteTransaction.COMMITED);
    }

    public void cancelTransaction(long id) {
        RemoteTransaction transaction = this.idTransactions.get(id);
        transaction.setState(RemoteTransaction.CANCELLED);
    }

    public RemoteTransaction getTransaction(long id) {
        return this.idTransactions.get(id);
    }
}
