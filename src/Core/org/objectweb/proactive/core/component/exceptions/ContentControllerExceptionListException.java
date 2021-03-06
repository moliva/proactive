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
package org.objectweb.proactive.core.component.exceptions;

import java.util.Hashtable;
import java.util.Map;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.annotation.PublicAPI;


/**
 * An exception that is also a container for a list of exceptions generated by uses of a content controller.
 * This exception may be thrown when handling several components simultaneously, for example with a ProActive group.
 *
 * @author The ProActive Team
 *
 */
@PublicAPI
public class ContentControllerExceptionListException extends Exception {
    Map<Component, IllegalLifeCycleException> lifeCycleExceptions = null;
    Map<Component, IllegalContentException> contentExceptions = null;

    public ContentControllerExceptionListException() {
    }

    public ContentControllerExceptionListException(
            Map<Component, IllegalLifeCycleException> lifeCycleExceptions,
            Map<Component, IllegalContentException> contentExceptions) {
        this.lifeCycleExceptions = lifeCycleExceptions;
        this.contentExceptions = contentExceptions;
    }

    public Map<Component, IllegalContentException> getContentExceptions() {
        return contentExceptions;
    }

    public Map<Component, IllegalLifeCycleException> getLifeCycleExceptions() {
        return lifeCycleExceptions;
    }

    public boolean isEmpty() {
        return (((lifeCycleExceptions == null) || (lifeCycleExceptions.isEmpty())) && ((contentExceptions == null) || (contentExceptions
                .isEmpty())));
    }

    public void addIllegalLifeCycleException(Component c, IllegalLifeCycleException e) {
        if (lifeCycleExceptions == null) {
            lifeCycleExceptions = new Hashtable<Component, IllegalLifeCycleException>();
        }
        lifeCycleExceptions.put(c, e);
    }

    public void addIllegalContentException(Component c, IllegalContentException e) {
        if (contentExceptions == null) {
            contentExceptions = new Hashtable<Component, IllegalContentException>();
        }
        contentExceptions.put(c, e);
    }
}
