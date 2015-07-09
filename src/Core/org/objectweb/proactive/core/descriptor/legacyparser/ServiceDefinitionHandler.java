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
package org.objectweb.proactive.core.descriptor.legacyparser;

import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptorInternal;
import org.objectweb.proactive.core.descriptor.services.FaultToleranceService;
import org.objectweb.proactive.core.descriptor.services.RMIRegistryLookupService;
import org.objectweb.proactive.core.descriptor.services.UniversalService;
import org.objectweb.proactive.core.xml.handler.BasicUnmarshaller;
import org.objectweb.proactive.core.xml.handler.PassiveCompositeUnmarshaller;
import org.objectweb.proactive.core.xml.handler.UnmarshallerHandler;
import org.objectweb.proactive.core.xml.io.Attributes;
import org.xml.sax.SAXException;


public class ServiceDefinitionHandler extends PassiveCompositeUnmarshaller implements
        ProActiveDescriptorConstants {
    ProActiveDescriptorInternal pad;
    protected String serviceId;

    public ServiceDefinitionHandler(ProActiveDescriptorInternal pad) {
        super(false);
        this.pad = pad;
        this.addHandler(RMI_LOOKUP_TAG, new RMILookupHandler());
        this.addHandler(FT_CONFIG_TAG, new FaultToleranceHandler());
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.xml.handler.AbstractUnmarshallerDecorator#notifyEndActiveHandler(java.lang.String, org.objectweb.proactive.core.xml.handler.UnmarshallerHandler)
     */
    @Override
    protected void notifyEndActiveHandler(String name, UnmarshallerHandler activeHandler) throws SAXException {
        UniversalService service = (UniversalService) activeHandler.getResultObject();
        pad.addService(serviceId, service);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.xml.handler.UnmarshallerHandler#startContextElement(java.lang.String, org.objectweb.proactive.core.xml.io.Attributes)
     */
    @Override
    public void startContextElement(String name, Attributes attributes) throws SAXException {
        this.serviceId = attributes.getValue("id");
    }

    protected class RMILookupHandler extends BasicUnmarshaller {
        public RMILookupHandler() {
        }

        @Override
        public void startContextElement(String name, Attributes attributes) throws org.xml.sax.SAXException {
            String lookupUrl = attributes.getValue("url");
            RMIRegistryLookupService rmiService = new RMIRegistryLookupService(lookupUrl);
            setResultObject(rmiService);
        }
    } // end of inner class RMILookupHandler

    protected class FaultToleranceHandler extends PassiveCompositeUnmarshaller {
        protected FaultToleranceService ftService;

        public FaultToleranceHandler() {
            FTConfigHandler ftch = new FTConfigHandler();
            this.addHandler(FT_CKPTSERVER_TAG, ftch);
            this.addHandler(FT_RECPROCESS_TAG, ftch);
            this.addHandler(FT_LOCSERVER_TAG, ftch);
            this.addHandler(FT_RESSERVER_TAG, ftch);
            this.addHandler(FT_GLOBALSERVER_TAG, ftch);
            this.addHandler(FT_TTCVALUE_TAG, ftch);
            this.addHandler(FT_PROTO_TAG, ftch);
        }

        @Override
        public void startContextElement(String name, Attributes attributes) throws org.xml.sax.SAXException {
            this.ftService = new FaultToleranceService();
        }

        @Override
        public Object getResultObject() throws org.xml.sax.SAXException {
            return this.ftService;
        }

        protected class FTConfigHandler extends BasicUnmarshaller {
            @Override
            public void startContextElement(String name, Attributes attributes)
                    throws org.xml.sax.SAXException {
                if (FT_RECPROCESS_TAG.equals(name)) {
                    FaultToleranceHandler.this.ftService.setRecoveryProcessURL(attributes.getValue("url"));
                } else if (FT_LOCSERVER_TAG.equals(name)) {
                    FaultToleranceHandler.this.ftService.setLocationServerURL(attributes.getValue("url"));
                } else if (FT_CKPTSERVER_TAG.equals(name)) {
                    FaultToleranceHandler.this.ftService.setCheckpointServerURL(attributes.getValue("url"));
                } else if (FT_RESSERVER_TAG.equals(name)) {
                    FaultToleranceHandler.this.ftService
                            .setAttachedResourceServer(attributes.getValue("url"));
                } else if (FT_TTCVALUE_TAG.equals(name)) {
                    FaultToleranceHandler.this.ftService.setTtcValue(attributes.getValue("value"));
                } else if (FT_GLOBALSERVER_TAG.equals(name)) {
                    FaultToleranceHandler.this.ftService.setGlobalServerURL(attributes.getValue("url"));
                } else if (FT_PROTO_TAG.equals(name)) {
                    FaultToleranceHandler.this.ftService.setProtocolType(attributes.getValue("type"));
                }
            }
        }
    }
}
