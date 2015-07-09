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
package org.objectweb.proactive.extensions.timitspmd.util;

/**
 * This class represent parameters for one benchmark test. These parameters are
 * set in the configuration file.
 *
 * @author The ProActive Team
 */
public class Parameter {
    public String className;
    public String[] args;
    public int run;
    public String outputFile;
    public String note;
    public String name;
    public XMLHelper xhp;
    public String proActiveDescriptor;
    public long timeout;
    public String[] jvmArgs;
    public int warmUpRuns;

    /**
     * Create an instance of Paramater for a benchmark test
     *
     * @param xhp
     *            a reference to the XMLHelper instance
     * @param name
     *            the name of the benchmark test
     * @param className
     *            the full class name of the application to benchmark
     * @param args
     *            the arguments list to pass to application
     * @param run
     *            how many time you want to run the application
     * @param outputFile
     *            the output filename for XML results
     * @param note
     *            specific information to add for this benchmark test
     * @param proActiveDescriptor
     *            the ProActive deployement descriptor filename
     * @param timeout
     *            timeout in seconds for this test
     * @param jvmArgs
     *            Arguments passed to create a jvm
     */
    public Parameter(XMLHelper xhp, String name, String className, String[] args, int run, String outputFile,
            String note, String proActiveDescriptor, long timeout, String[] jvmArgs, int warmUpRuns) {
        this.xhp = xhp;
        this.name = name;
        this.className = className;
        this.args = args.clone();
        this.run = run;
        this.outputFile = outputFile;
        this.note = note;
        this.proActiveDescriptor = proActiveDescriptor;
        this.timeout = timeout;
        this.jvmArgs = jvmArgs.clone();
        this.warmUpRuns = warmUpRuns;
    }
}
