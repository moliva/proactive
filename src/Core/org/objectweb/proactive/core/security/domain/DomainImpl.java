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
package org.objectweb.proactive.core.security.domain;

import java.io.IOException;
import java.security.AccessControlException;
import java.security.PublicKey;

import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.ProActiveSecurityDescriptorHandler;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.security.SecurityContext;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.core.security.crypto.KeyExchangeException;
import org.objectweb.proactive.core.security.exceptions.InvalidPolicyFile;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.security.exceptions.SecurityNotAvailableException;
import org.objectweb.proactive.core.security.securityentity.Entities;
import org.objectweb.proactive.core.security.securityentity.Entity;


public class DomainImpl implements SecurityDomain {
    private PolicyServer policyServer;
    private String name;

    // empty constructor
    public DomainImpl() {
    }

    // create policy Server
    public DomainImpl(String securityFile, String name) {
        try {
            this.policyServer = ProActiveSecurityDescriptorHandler.createPolicyServer(securityFile);
        } catch (InvalidPolicyFile e) {
            e.printStackTrace();
        }
    }

    public SecurityContext getPolicy(Entities local, Entities distant) {
        try {
            return this.policyServer.getPolicy(local, distant);
        } catch (SecurityNotAvailableException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ext.security.domain.Domain#getCertificateEncoded()
     */
    public byte[] getCertificateEncoded() throws SecurityNotAvailableException {
        return null;
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ext.security.domain.Domain#getEntities()
     */
    public Entities getEntities() throws SecurityNotAvailableException {
        Entities entities = new Entities();

        //        entities.add(new Entity(new TypedCertificateList));
        return entities;
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ext.security.domain.Domain#getName()
     */
    public String getName() {
        return null;
    }

    public void terminateSession(long sessionID) throws IOException, SecurityNotAvailableException {
    }

    public TypedCertificate getCertificate() {
        return null;
    }

    public ProActiveSecurityManager getProActiveSecurityManager() {
        return null;
    }

    public long startNewSession(long distantSessionID, SecurityContext policy,
            TypedCertificate distantCertificate) {
        return 0;
    }

    public PublicKey getPublicKey() throws SecurityNotAvailableException {
        return null;
    }

    public byte[] randomValue(long sessionID, byte[] clientRandomValue) throws SecurityNotAvailableException,
            RenegotiateSessionException {
        return null;
    }

    public byte[] publicKeyExchange(long sessionID, byte[] signature) throws SecurityNotAvailableException,
            RenegotiateSessionException, KeyExchangeException {
        return null;
    }

    public byte[][] secretKeyExchange(long sessionID, byte[] encodedAESKey, byte[] encodedIVParameters,
            byte[] encodedClientMacKey, byte[] encodedLockData, byte[] parametersSignature)
            throws SecurityNotAvailableException, RenegotiateSessionException {
        return null;
    }

    public String getVNName() throws SecurityNotAvailableException {
        return null;
    }

    public ProActiveSecurityManager getProActiveSecurityManager(Entity user)
            throws SecurityNotAvailableException, AccessControlException {
        // TODO Auto-generated method stub
        return null;
    }

    public void setProActiveSecurityManager(Entity user, PolicyServer policyServer)
            throws SecurityNotAvailableException, AccessControlException {
        // TODO Auto-generated method stub
    }
}
