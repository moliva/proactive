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
package org.objectweb.proactive.core.component.body;

import java.io.Serializable;

import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.proactive.Active;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.EndActive;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.control.PAMembraneController;


/**
 * The class for the component activity, having the membrane controller inside the membrane
 *
 * @author The ProActive Team
 */
public class ComponentMembraneActivity extends ComponentActivity implements RunActive, InitActive, EndActive,
        Serializable {

    public ComponentMembraneActivity() {
        super();
    }

    public ComponentMembraneActivity(Active activity, Object reifiedObject) {
        super(activity, reifiedObject);

    }

    /**
     * <p>
     * Runs the activity as defined in @see ComponentRunActive. The default behaviour is to
     * serve non-functional requests in FIFO order, until the component is started. Then the functional
     * activity (as defined in @see InitActive, @see RunActive and @see EndActive) begins.</p><p>
     * When redefining the @see RunActive#runActivity(Body) method, the @see Body#isActive() returns true
     * as long as the lifecycle of the component is @see LifeCycleController#STARTED. When the lifecycle of
     * the component is @see LifeCycleController#STOPPED, @see Body#isActive() returns false.</p>
     *
     */
    public void runActivity(Body body) {
        if ((componentRunActive != null) && (componentRunActive != this)) {
            componentRunActive.runActivity(body);
        } else {
            // this is the default activity of the active object
            // the activity of the component has been initialized and started, now
            // what we have to do is to manage the life cycle, i.e. start and stop the
            // activity
            // that can be redefined on the reified object.
            try {
                Service componentService = new Service(body);
                NFRequestFilterImpl nfRequestFilter = new NFRequestFilterImpl();
                MembraneControllerRequestFilter memRequestFilter = new MembraneControllerRequestFilter();
                while (body.isActive()) {
                    ComponentBody componentBody = (ComponentBody) body;
                    /*
                     * While the membrane is stopped, serve calls only on the Membrane Controller
                     */
                    while (Utils.getPAMembraneController(componentBody.getPAComponentImpl())
                            .getMembraneState().equals(PAMembraneController.MEMBRANE_STOPPED)) {
                        componentService.blockingServeOldest(memRequestFilter);
                    }

                    while (LifeCycleController.STOPPED.equals(GCM.getGCMLifeCycleController(
                            componentBody.getPAComponentImpl()).getFcState())) {
                        componentService.blockingServeOldest(nfRequestFilter);
                        if (!body.isActive()) {
                            // in case of a migration 
                            break;
                        }
                    }
                    if (!body.isActive()) {
                        // in case of a migration 
                        break;
                    }

                    // 3.1. init object Activity
                    // life cycle started : starting activity of the object
                    if (functionalInitActive != null) {
                        functionalInitActive.initActivity(body);
                        //functionalInitActive = null; // we won't do it again
                    }

                    ((ComponentBody) body).startingFunctionalActivity();
                    // 3.2 while object activity
                    // componentServe (includes filter on priority)
                    functionalRunActive.runActivity(body);
                    ((ComponentBody) body).finishedFunctionalActivity();
                    if (functionalEndActive != null) {
                        functionalEndActive.endActivity(body);
                    }

                    /* While membrane is started
                     * serve all non functional calls
                     * */
                    while (Utils.getPAMembraneController(componentBody.getPAComponentImpl())
                            .getMembraneState().equals(PAMembraneController.MEMBRANE_STARTED)) {
                        componentService.blockingServeOldest(nfRequestFilter);
                        if (!body.isActive()) {//Don't know if this is OK
                            // in case of a migration 
                            break;
                        }
                    }

                }
            } catch (NoSuchInterfaceException e) {
                logger
                        .error("could not retreive an interface, probably the life cycle controller of this component; terminating the component. Error message is : " +
                            e.getMessage());
            }
        }
    }

}
