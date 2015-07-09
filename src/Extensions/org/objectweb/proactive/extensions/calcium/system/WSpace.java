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
//@snippet-start calcium_WSpace
package org.objectweb.proactive.extensions.calcium.system;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * This class is the interface for creating files on the computation node.
 *
 * This class is not Serializable on purpose, since it is environment dependent.
 *
 * @author The ProActive Team
 */
@PublicAPI
public interface WSpace {
    static Logger logger = ProActiveLogger.getLogger(Loggers.SKELETONS_SYSTEM);

    /**
     * Copies a File into the workspace, and returns
     * a reference on the new File.
     *
     * @param src  The original location of the file.
     * @return A reference to the file inside the workspace.
     * @throws IOException
     */
    public File copyInto(File src) throws IOException;

    /**
     * Downloads the file specified by the URL and places a copy inside
     * the workspace.
     *
     * @param src The location of the original file
     * @return A reference to the file inside the workspace.
     * @throws IOException
     */
    public File copyInto(URL src) throws IOException;

    /**
     * This method is used to get a reference on a file inside the workspace.
     *
     * Note that this is only a reference, and can point to an unexistent File.
     * ie. no File is actually created by invoking this method.
     *
     * @param path The path to look inside the workspace.
     * @return A reference to the path inside the workspace.
     */
    public File newFile(String path);

    /**
     * This method is used to get a reference on a file inside the workspace.
     *
     * Note that this is only a reference, and can point to an unexistent File.
     * ie. no File is actually created by invoking this method.
     *
     * @param path The path to look inside the workspace.
     * @return A reference to the path inside the workspace.
     */
    public File newFile(File path);

    /**
     * This method returns true if a file with this name exists in the work space.
     *
     * @return true if the file exists, false otherwise.
     */
    public boolean exists(File path);

    /**
     * This method returns a list of the files currently available on the root
     * of the workspace.
     *
     * @return The list of files that are currently held in the workspace.
     */
    public File[] listFiles();

    /**
     * This method returns a list of the files currently available on the root
     * of the workspace that match the specified filter.
     *
     * @param filter The filter to use for listing the files.
     * @return The list of files thar are currently held in the workspace and match the specified filter.
     */
    public File[] listFiles(FileFilter filter);
}
//@snippet-end calcium_WSpace