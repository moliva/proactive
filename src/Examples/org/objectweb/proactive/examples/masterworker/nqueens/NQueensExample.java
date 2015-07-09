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
package org.objectweb.proactive.examples.masterworker.nqueens;

import java.util.Vector;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.examples.masterworker.AbstractExample;
import org.objectweb.proactive.examples.masterworker.nqueens.query.Query;
import org.objectweb.proactive.examples.masterworker.nqueens.query.QueryExtern;
import org.objectweb.proactive.examples.masterworker.nqueens.query.QueryGenerator;
import org.objectweb.proactive.examples.masterworker.util.Pair;
import org.objectweb.proactive.extensions.masterworker.ProActiveMaster;
import org.objectweb.proactive.extensions.masterworker.TaskException;


/**
 * This examples calculates the Nqueen
 * @author The ProActive Team
 *
 */
public class NQueensExample extends AbstractExample {
    private static final int DEFAULT_BOARD_SIZE = 17;
    private static final int DEFAULT_ALGORITHM_DEPTH = 1;
    public static int nqueen_board_size;
    public static int nqueen_algorithm_depth;
    private static ProActiveMaster<QueryExtern, Pair<Long, Long>> master;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        //   Getting command line parameters and creating the master (see AbstractExample)
        init(args);

        if (master_vn_name == null) {
            master = new ProActiveMaster<QueryExtern, Pair<Long, Long>>();
        } else {
            master = new ProActiveMaster<QueryExtern, Pair<Long, Long>>(descriptor_url, master_vn_name);
        }

        // handling termination even if something fails
        registerShutdownHook(new Runnable() {
            public void run() {
                master.terminate(true);
            }
        });

        // NQueens tasks are small, therefore, workers should have a big pool of tasks to solve
        master.setInitialTaskFlooding(20);

        // Adding ressources
        if (schedulerURL != null) {
            master.addResources(schedulerURL, login, password, classpath);
        } else if (vn_name == null) {
            master.addResources(descriptor_url);
        } else {
            master.addResources(descriptor_url, vn_name);
        }

        System.out.println("Launching NQUEENS solutions finder for n = " + nqueen_board_size +
            " with a depth of " + nqueen_algorithm_depth);

        long sumResults = 0;
        long sumTime = 0;
        long begin = System.currentTimeMillis();

        // Generating the queries for the NQueens
        Vector<Query> unresolvedqueries = QueryGenerator.generateQueries(nqueen_board_size,
                nqueen_algorithm_depth);

        // Splitting Queries
        Vector<QueryExtern> toSolve = new Vector<QueryExtern>();
        while (!unresolvedqueries.isEmpty()) {
            Query query = unresolvedqueries.remove(0);
            Vector<Query> splitted = QueryGenerator.splitAQuery(query);
            if (!splitted.isEmpty()) {
                for (Query splitquery : splitted) {
                    toSolve.add(new QueryExtern(splitquery));
                }
            } else {
                toSolve.add(new QueryExtern(query));
            }
        }
        master.solve(toSolve);

        // Print results on the fly
        while (!master.isEmpty()) {
            try {
                Pair<Long, Long> res = master.waitOneResult();
                sumResults += res.getFirst();
                sumTime += res.getSecond();
                System.out.println("Current nb of results : " + sumResults);
            } catch (TaskException e) {
                // Exception in the algorithm
                e.printStackTrace();
            }
        }

        // Calculation finished, printing summary and total number of solutions
        long end = System.currentTimeMillis();
        int nbworkers = master.workerpoolSize();

        System.out.println("Total number of configurations found for n = " + nqueen_board_size +
            " and with " + nbworkers + " workers : " + sumResults);
        System.out.println("Time needed with " + nbworkers + " workers : " + ((end - begin) / 3600000) +
            String.format("h %1$tMm %1$tSs %1$tLms", end - begin));
        System.out.println("Total workers calculation time : " + (sumTime / 3600000) +
            String.format("h %1$tMm %1$tSs %1$tLms", sumTime));

        PALifeCycle.exitSuccess();
    }

    protected static void init(String[] args) throws Exception {
        command_options.addOption(OptionBuilder.withArgName("value").hasArg().withDescription(
                "nqueen board size").create("size"));
        command_options.addOption(OptionBuilder.withArgName("value").hasArg().withDescription(
                "nqueen algorithm depth").create("depth"));

        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("NQueensExample", command_options);

        // Initialisation of common arguments
        AbstractExample.init(args);

        String board_sizeString = cmd.getOptionValue("size");
        if (board_sizeString == null) {
            nqueen_board_size = DEFAULT_BOARD_SIZE;
        } else {
            nqueen_board_size = Integer.parseInt(board_sizeString);
        }

        String algodepthString = cmd.getOptionValue("depth");
        if (algodepthString == null) {
            nqueen_algorithm_depth = DEFAULT_ALGORITHM_DEPTH;
        } else {
            nqueen_algorithm_depth = Integer.parseInt(algodepthString);
        }
    }
}
