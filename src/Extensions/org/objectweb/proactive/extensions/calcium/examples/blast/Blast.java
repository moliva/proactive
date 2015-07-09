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
package org.objectweb.proactive.extensions.calcium.examples.blast;

import java.io.File;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extensions.calcium.Calcium;
import org.objectweb.proactive.extensions.calcium.Stream;
import org.objectweb.proactive.extensions.calcium.environment.Environment;
import org.objectweb.proactive.extensions.calcium.environment.EnvironmentFactory;
import org.objectweb.proactive.extensions.calcium.exceptions.PanicException;
import org.objectweb.proactive.extensions.calcium.futures.CalFuture;
import org.objectweb.proactive.extensions.calcium.skeletons.DaC;
import org.objectweb.proactive.extensions.calcium.skeletons.Pipe;
import org.objectweb.proactive.extensions.calcium.skeletons.Seq;
import org.objectweb.proactive.extensions.calcium.skeletons.Skeleton;
import org.objectweb.proactive.extensions.calcium.statistics.StatsGlobal;


public class Blast {
    Skeleton<BlastParams, File> root;

    public Blast() {
        /* Format the query and database files */
        Pipe<BlastParams, BlastParams> formatFork = new Pipe<BlastParams, BlastParams>(new ExecuteFormatDB(),
            new ExecuteFormatQuery());

        /* Blast a database
         * 2.1 Format the database
         * 2.2 Blast the database */
        Pipe<BlastParams, File> blastPipe = new Pipe<BlastParams, File>(formatFork,
            new Seq<BlastParams, File>(new ExecuteBlast()));

        /* 1 Divide the database
         * 2 Blast the database with the query
         * 3 Conquer the query results  */
        root = new DaC<BlastParams, File>(new DivideDB(), new DivideDBCondition(), blastPipe,
            new ConquerResults());
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 5) {
            System.out.println("Wrong number of arguments");
            System.out.println("[Usage] org.objectweb.proactive.extensions.calcium.examples.blast.Blast "
                + "DeploymentDescriptor Query Batabase path/to/formatdb path/to/blastall");
            System.exit(-1);
        }

        String descriptor = args[0];
        BlastParams param = new BlastParams(new File(args[1]), new File(args[2]), new File(args[3]),
            new File(args[4]), true, 2000 * 1024);

        Blast blast = new Blast();
        blast.solve(param, descriptor);
    }

    private void solve(BlastParams parameters, String descriptor) throws InterruptedException,
            PanicException, ProActiveException {

        Environment environment = EnvironmentFactory.newMultiThreadedEnvironment(2);
        //Environment environment = EnvironmentFactory.newProActiveEnvironment(descriptor);
        //Environment environment = ProActiveSchedulerEnvironment.factory("localhost","chri", "chri");

        Calcium calcium = new Calcium(environment);
        // @snippet-start calcium_Blast
        Stream<BlastParams, File> stream = calcium.newStream(root);
        CalFuture<File> future = stream.input(parameters);
        // @snippet-break calcium_Blast
        calcium.boot();
        // @snippet-resume calcium_Blast

        try {
            File res = future.get();
            // @snippet-break calcium_Blast
            System.out.println("Result in:" + res + " " + res.length() + " [bytes]");
            System.out.println(future.getStats());
            // @snippet-resume calcium_Blast
        } catch (Exception e) {
            e.printStackTrace();
        }
        // @snippet-end calcium_Blast

        // @snippet-start calcium_GlobalStat
        StatsGlobal stats = calcium.getStatsGlobal();
        // @snippet-end calcium_GlobalStat
        System.out.println(stats);
        calcium.shutdown();
    }
}
