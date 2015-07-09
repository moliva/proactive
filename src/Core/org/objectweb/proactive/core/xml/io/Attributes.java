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
package org.objectweb.proactive.core.xml.io;

import org.xml.sax.SAXException;


/**
 *
 * A class implementing this interface is a wrapper of the attribute of an XML element.
 * It is used to wrap both SAX and DOM attributes in an independant manner.
 *
 * @author The ProActive Team
 * @version      0.91
 *
 */
public interface Attributes {

    /**
     * Looks up an attribute's value by index.
     * @param index The attribute index (zero-based).
     * @return The attribute's value as a string, or null if the index is out of range.
     */
    public String getValue(int index);

    /**
     * Looks up an attribute's value by XML 1.0 qualified name.
     * @param qName The qualified (prefixed) name.
     * @return The attribute value as a string, or null if the attribute is not in the list or if
     *         qualified names are not available.
     * @throws SAXException
     */
    public String getValue(String qName) throws SAXException;

    /**
     * Looks up the index of an attribute by Namespace name.
     * @param uri The Namespace URI, or the empty string if the name has no Namespace URI.
     * @param localPart The attribute's local name.
     * @return The attribute value as a string, or null if the attribute is not in the list.
     */
    public String getValue(String uri, String localPart) throws SAXException;

    /**
     * Returns the number of attributes in the list.
     */
    public int getLength();
}
