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
package org.objectweb.proactive.core;

import java.io.File;


/**
 * Defines many constants useful across ProActive
 *
 * @author The ProActive Team
 * @version 1.0,  2002/03/21
 * @since   ProActive 0.9
 *
 */
public interface Constants {

    /** The explicit local body default class */
    public static final Class<?> DEFAULT_BODY_CLASS = org.objectweb.proactive.core.body.ActiveBody.class;

    /** The name of the explicit local body default class */
    public static final String DEFAULT_BODY_CLASS_NAME = DEFAULT_BODY_CLASS.getName();

    /** The explicit local body default class */
    public static final Class<?> DEFAULT_BODY_INTERFACE = org.objectweb.proactive.Body.class;

    /** The name of the explicit local body default class */
    public static final String DEFAULT_BODY_INTERFACE_NAME = DEFAULT_BODY_INTERFACE.getName();

    /** The explicit local body default class */
    public static final Class<?> DEFAULT_BODY_PROXY_CLASS = org.objectweb.proactive.core.body.proxy.UniversalBodyProxy.class;

    /** The name of the explicit local body default class */
    public static final String DEFAULT_BODY_PROXY_CLASS_NAME = DEFAULT_BODY_PROXY_CLASS.getName();

    /** The explicit local body default class */
    public static final Class<?> DEFAULT_FUTURE_PROXY_CLASS = org.objectweb.proactive.core.body.future.FutureProxy.class;

    /** The name of the explicit local body default class */
    public static final String DEFAULT_FUTURE_PROXY_CLASS_NAME = DEFAULT_FUTURE_PROXY_CLASS.getName();

    /**
     * The interface implemented by all proxies featuring 'future' semantics,
     * depending on whether they are remoteBodyly-accessible or not
     */
    public static final Class<?> FUTURE_PROXY_INTERFACE = org.objectweb.proactive.core.body.future.Future.class;

    /** rmi protocol identifier */
    public static final String RMI_PROTOCOL_IDENTIFIER = "rmi";

    /** rmi tunneling over ssh protocol identifier */
    public static final String RMISSH_PROTOCOL_IDENTIFIER = "rmissh";

    /** ibis protocol identifier */
    public static final String IBIS_PROTOCOL_IDENTIFIER = "ibis";

    /**xml-http protocol identifier */
    public static final String XMLHTTP_PROTOCOL_IDENTIFIER = "http";

    /** this property identifies the https protocol */
    public static final String HTTPSSH_PROTOCOL_IDENTIFIER = "httpssh";

    /** default protocol identifier */
    public static final String DEFAULT_PROTOCOL_IDENTIFIER = RMI_PROTOCOL_IDENTIFIER;

    // list of system properties used within proactive
    public static final String SSH_TUNNELING_DEFAULT_KNOW_HOSTS = "/.ssh/known_hosts";

    /** User configuration directory */
    public static final String USER_CONFIG_DIR = System.getProperty("user.home") + File.separator +
        ".proactive";

    /** The name of nodes created by a GCM Deployment
     *
     * User are not allowed to create node with this name.
     * @TODO enforce this rule, currently the user node is destroyed
     */
    public static final String GCM_NODE_NAME = "GCMNode-";
}
