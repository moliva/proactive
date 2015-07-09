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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.pnpssl;

import java.security.KeyStore;

import javax.net.ssl.TrustManager;

import org.objectweb.proactive.extensions.ssl.PASslEngine;
import org.objectweb.proactive.extensions.ssl.SecureMode;


/**
 * An helper class wrapping {@link PASslEngine} creation
 *
 */
public class PNPSslEngineFactory {
    final SecureMode sm;
    final KeyStore ks;
    final TrustManager tm;

    /**
     * Store default value for creating {@link PASslEngine}
     *
     * @param sm Default secure mode to use
     * @param ks Default key store to use
     * @param tm Default trust manage to use
     */
    public PNPSslEngineFactory(SecureMode sm, KeyStore ks, TrustManager tm) {
        this.sm = sm;
        this.ks = ks;
        this.tm = tm;
    }

    /**
     * Create a {@link PASslEngine} in client mode using the cached default values
     *
     * @return An already configured {@link PASslEngine}
     */
    public PASslEngine getClientSSLEngine() {
        return this.getClientSSLEngine(this.sm, this.ks, this.tm);
    }

    /**
     * Create a {@link PASslEngine} in client mode. Default values are not used
     *
     * @param sm the secure mode
     * @param ks the key store
     * @param tm the trust manager
     * @return
     */
    public PASslEngine getClientSSLEngine(SecureMode sm, KeyStore ks, TrustManager tm) {
        return new PASslEngine(true, sm, ks, tm);
    }

    /**
     * Create a {@link PASslEngine} in server mode using the cached default values
     *
     * @return An already configured {@link PASslEngine}
     */
    public PASslEngine getServerSSLEngine() {
        return this.getServerSSLEngine(this.sm, this.ks, this.tm);
    }

    /**
     * Create a {@link PASslEngine} in server mode. Default values are not used
     *
     * @param sm the secure mode
     * @param ks the key store
     * @param tm the trust manager
     * @return
     */
    public PASslEngine getServerSSLEngine(SecureMode sm, KeyStore ks, TrustManager tm) {
        return new PASslEngine(false, sm, ks, tm);
    }
}
