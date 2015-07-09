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
package functionalTests.component;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.control.AttributeController;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * @author The ProActive Team
 */
public class ParameterizedPrimitiveComponentA extends PrimitiveComponentA implements AttributeController {
    public String message;
    public final static String I2_ITF_NAME = "i2";
    private static Logger logger = ProActiveLogger.getLogger("functionalTests");
    I2 i2;

    /**
     *
     */
    public ParameterizedPrimitiveComponentA() {
    }

    // attribute for use by AttributeController 
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Message processInputMessage(Message message) {
        //      /logger.info("transferring message :" + message.toString());
        if (i2 != null) {
            return (i2.processOutputMessage(message.append(message))).append(message);
        } else {
            logger.error("cannot forward message (binding missing)");
            message.setInvalid();
            return message;
        }
    }
}
