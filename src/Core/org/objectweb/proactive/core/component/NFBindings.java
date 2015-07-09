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
package org.objectweb.proactive.core.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Extended bindings class, containing more information about bindings inside
 * the membrane.
 *
 * @author The ProActive Team
 */
public class NFBindings implements Serializable {

	private final Map<String, NFBinding> serverAliasBindings;
	private final Map<String, NFBinding> clientAliasBindings;
	private final Map<String, NFBinding> normalBindings;

	public NFBindings() {
		serverAliasBindings = new HashMap<String, NFBinding>();
		clientAliasBindings = new HashMap<String, NFBinding>();
		normalBindings = new HashMap<String, NFBinding>();
	}

	public void addServerAliasBinding(final NFBinding b) {
		serverAliasBindings.put(b.getClientInterfaceName(), b);

	}

	public void addClientAliasBinding(final NFBinding b) {
		clientAliasBindings.put(b.getClientInterfaceName(), b);

	}

	public void addNormalBinding(final NFBinding b) {
		normalBindings.put(b.getClientInterfaceName(), b);
	}

	public Object remove(final String clientItfName) {

		if (serverAliasBindings.containsKey(clientItfName)) {
			return serverAliasBindings.remove(clientItfName);
		}
		if (clientAliasBindings.containsKey(clientItfName)) {
			return clientAliasBindings.remove(clientItfName);
		}
		if (normalBindings.containsKey(clientItfName)) {
			return normalBindings.remove(clientItfName);
		}

		return null;
	}

	public Object get(final String clientItfName) {

		if (serverAliasBindings.containsKey(clientItfName)) {
			return serverAliasBindings.get(clientItfName);
		}
		if (clientAliasBindings.containsKey(clientItfName)) {
			return clientAliasBindings.get(clientItfName);
		}
		if (normalBindings.containsKey(clientItfName)) {
			return normalBindings.get(clientItfName);
		}

		return null;
	}

	public boolean containsBindingOn(final String clientItfName) {

		return serverAliasBindings.containsKey(clientItfName) || clientAliasBindings.containsKey(clientItfName) || normalBindings
				.containsKey(clientItfName);
	}

	public boolean hasServerAliasBindingOn(final String component, final String itf) {
		final Vector<NFBinding> v = new Vector<NFBinding>(serverAliasBindings.values());
		for (final NFBinding val : v) {
			if (val.getServerComponentName().equals(component) && val.getServerInterface().equals(itf)) {
				return true;
			}

		}
		return false;
	}

	public boolean hasServerAliasBindingOn(final String component) {// Returns true when
																														// there is an alias
																														// binding on the
																														// component, the
																														// name of which is
																														// passed as an
																														// argument
		final Vector<NFBinding> v = new Vector<NFBinding>(serverAliasBindings.values());
		for (final NFBinding val : v) {
			if (val.getServerComponentName().equals(component)) {
				return true;
			}
		}
		return false;
	}

	public void removeNormalBinding(final String component, final String itf) {
		final Vector<NFBinding> v = new Vector<NFBinding>(normalBindings.values());
		for (final NFBinding val : v) {
			if (val.getServerComponentName().equals(component) && val.getClientInterface().equals(itf)) {
				normalBindings.remove(val.getClientInterfaceName());
			}
		}

	}

	public void removeClientAliasBinding(final String component, final String itf) {// Removes
																																			// a
																																			// client
																																			// alias
																																			// binding.
																																			// The
																																			// component
																																			// name
																																			// and the
																																			// interface
																																			// name
																																			// belong
																																			// to the
																																			// component
																																			// on the
																																			// client
																																			// side.

		final Vector<NFBinding> v = new Vector<NFBinding>(clientAliasBindings.values());
		for (final NFBinding val : v) {
			if (val.getServerComponentName().equals(component) && val.getClientInterface().equals(itf)) {
				clientAliasBindings.remove(val.getClientInterfaceName());
			}
		}

	}

	public void removeServerAliasBindingsOn(final String component) {
		final Vector<NFBinding> v = new Vector<NFBinding>(serverAliasBindings.values());
		for (final NFBinding val : v) {
			if (val.getServerComponentName().equals(component)) {
				serverAliasBindings.remove(val.getClientInterfaceName());
			}
		}

	}

	public boolean hasBinding(final String clientComponent, final String clientItf, final String serverComponent, final String serverItf) {

		final Vector<NFBinding> v = new Vector<NFBinding>(serverAliasBindings.values());

		for (final NFBinding val : v) {
			if (val.getClientComponentName().equals(clientComponent) && val.getClientInterfaceName().equals(clientItf)
					&& val.getServerComponentName().equals(serverComponent) && val.getServerInterface().equals(serverItf)) {
				return true;
			}

		}

		return false;
	}

	public Vector<NFBinding> getServerAliasBindingsOn(final String component, final String itf) {
		final Vector<NFBinding> v = new Vector<NFBinding>(serverAliasBindings.values());
		final Vector<NFBinding> result = new Vector<NFBinding>();
		for (final NFBinding val : v) {
			if (val.getServerComponentName().equals(component) && val.getServerInterface().equals(itf)) {
				result.add(val);
			}

		}
		return result;
	}

}
