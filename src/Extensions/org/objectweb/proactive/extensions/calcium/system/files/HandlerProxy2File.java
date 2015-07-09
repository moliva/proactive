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
package org.objectweb.proactive.extensions.calcium.system.files;

import java.io.File;
import java.util.IdentityHashMap;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.calcium.environment.FileServerClient;
import org.objectweb.proactive.extensions.calcium.stateness.Handler;
import org.objectweb.proactive.extensions.calcium.system.ProxyFile;


class HandlerProxy2File implements Handler<File> {
    static Logger logger = ProActiveLogger.getLogger(Loggers.SKELETONS_SYSTEM);
    FileServerClient fserver;
    File outDir;
    IdentityHashMap<ProxyFile, ProxyFile> files;

    HandlerProxy2File(FileServerClient fserver, IdentityHashMap<ProxyFile, ProxyFile> files, File outDir) {
        this.fserver = fserver;
        this.outDir = outDir;
        this.files = files;
    }

    public File transform(File file) throws Exception {
        if (!ProxyFile.class.isAssignableFrom(file.getClass())) {
            throw new Exception("Transforming wrong type class:" + file.getClass());
        }

        ProxyFile pfile = (ProxyFile) file;

        pfile.setWSpace(fserver, outDir);
        pfile.saveRemoteDataInWSpace();

        files.put(pfile, pfile);

        File f = pfile.getCurrent();

        return f;
    }

    public boolean matches(Object o) {
        return ProxyFile.class.isAssignableFrom(o.getClass());
    }
}
