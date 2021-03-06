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
package org.objectweb.proactive.core.process;

/**
 * A class implementing this interface is able to start an embedded ExternalProcess.
 */
public interface ExternalProcessDecorator extends ExternalProcess {

    /**
     * Composition in which the command of the target process is appended to the command of this process.
     */
    public static final int APPEND_TO_COMMAND_COMPOSITION = 1;

    /**
     * Composition in which the command of the target process is sent into the
     * output stream of this process.
     */
    public static final int SEND_TO_OUTPUT_STREAM_COMPOSITION = 2;

    /**
     * Composition in which the command of the target process is given as parameter
     * of the command of this process.
     */
    public static final int GIVE_COMMAND_AS_PARAMETER = 3;

    /**
     * Composition in which a File transfer takes place in the upper process and
     * the command of this target process is appended to the upper process command
     */
    public static final int COPY_FILE_AND_APPEND_COMMAND = 4;

    /**
     * Returns the process target of this process. The target process is embedded
     * inside the current process.
     * @return the process target of this process
     */
    public ExternalProcess getTargetProcess();

    /**
     * Sets the process target of this process. The target process is embedded
     * inside the current process.
     * @param targetProcess the process target of this process
     */
    public void setTargetProcess(ExternalProcess targetProcess);

    /**
     * Sets the type of composition that occurs between the commands of two processes.
     * Type are APPEND_TO_COMMAND_COMPOSITION or SEND_TO_OUTPUT_STREAM_COMPOSITION.
     * @param compositionType the type of composition that occurs between the commands of two processes
     */
    public void setCompositionType(int compositionType);
}
