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
package org.objectweb.proactive.core.gc;

/**
 * This is probably the smallest class in ProActive. So, let's compensate with
 * comments. The DGC has to keep track of every Proxy pointing to a given
 * Active Object, as the only way to declare that an AO does not reference
 * another AO anymore is to observe that all these Proxy have disappeared.
 * Instead of doing this by keeping all Proxy in a list, we give a reference to
 * the same instance of a GCTag to all the Proxy pointing to the same AO. The
 * DGC keeps a weak reference to this GCTag. When this weak reference is
 * cleared, it means all the Proxy pointing to the AO have been garbage
 * collected.
 *
 * This comment is there only to participate in the race to the commit 5000.
 *
 * This code is:
 * Copyright (C) 2007 Guillaume Chazarain
 * All rights reserved
 */
public class GCTag {
}
