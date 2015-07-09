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
package org.objectweb.proactive.core.xml.handler;

import java.util.List;

import org.objectweb.proactive.core.xml.io.Attributes;


/**
 *
 * Receives SAX event and pass them on
 *
 * @author The ProActive Team
 * @version      0.91
 *
 */
public class CollectionUnmarshaller extends AbstractUnmarshallerDecorator {
    protected List<Object> resultList;
    protected Class<?> targetClass;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //  
    public CollectionUnmarshaller(boolean lenient) {
        this(null, lenient);
    }

    public CollectionUnmarshaller() {
        this(null);
    }

    public CollectionUnmarshaller(Class<?> targetClass, boolean lenient) {
        super(lenient);
        this.targetClass = targetClass;
    }

    public CollectionUnmarshaller(Class<?> targetClass) {
        super();
        this.targetClass = targetClass;
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //
    //
    // -- implements UnmarshallerHandler ------------------------------------------------------
    //  
    public Object getResultObject() throws org.xml.sax.SAXException {
        int size = 0;
        if (resultList != null) {
            size = resultList.size();
        }
        Object[] resultArray = null;
        if (targetClass == null) {
            resultArray = new Object[size];
        } else {
            resultArray = (Object[]) java.lang.reflect.Array.newInstance(targetClass, size);
        }
        if (size > 0) {
            resultList.toArray(resultArray);
        }

        // clean-up
        resultList = null;
        //targetClass = null;
        return resultArray;
    }

    public void startContextElement(String name, Attributes attributes) throws org.xml.sax.SAXException {
        resultList = new java.util.ArrayList<Object>();
    }

    //
    // -- PROTECTED METHODS ------------------------------------------------------
    //
    @Override
    protected void notifyEndActiveHandler(String name, UnmarshallerHandler activeHandler)
            throws org.xml.sax.SAXException {
        Object o = activeHandler.getResultObject();
        if (o != null) {
            resultList.add(o);
        }
    }

    //
    // -- PRIVATE METHODS ------------------------------------------------------
    //
    //
    // -- INNER CLASSES ------------------------------------------------------
    //
}
