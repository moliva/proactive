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
package functionalTests.security.sessionkeyexchange;

import java.security.AccessControlException;
import java.security.PublicKey;

import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.security.SecurityContext;
import org.objectweb.proactive.core.security.SecurityEntity;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.core.security.crypto.AuthenticationException;
import org.objectweb.proactive.core.security.crypto.KeyExchangeException;
import org.objectweb.proactive.core.security.crypto.SessionException;
import org.objectweb.proactive.core.security.exceptions.CommunicationForbiddenException;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.security.exceptions.SecurityNotAvailableException;
import org.objectweb.proactive.core.security.securityentity.Entities;
import org.objectweb.proactive.core.security.securityentity.Entity;


public class DummySecurityEntity implements SecurityEntity {

    /**
     *
     */
    private ProActiveSecurityManager securityManager;

    public DummySecurityEntity(ProActiveSecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public void initiateSession(int type, SecurityEntity body) throws CommunicationForbiddenException,
            AuthenticationException, RenegotiateSessionException, SecurityNotAvailableException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        this.securityManager.initiateSession(body);
    }

    public void terminateSession(long sessionID) throws SecurityNotAvailableException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        this.securityManager.terminateSession(sessionID);
    }

    public TypedCertificate getCertificate() throws SecurityNotAvailableException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        return this.securityManager.getCertificate();
    }

    public ProActiveSecurityManager getProActiveSecurityManager(Entity user)
            throws SecurityNotAvailableException, AccessControlException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        return this.securityManager.getProActiveSecurityManager(user);
    }

    public long startNewSession(long distantSessionID, SecurityContext policy,
            TypedCertificate distantCertificate) throws SecurityNotAvailableException, SessionException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        return this.securityManager.startNewSession(distantSessionID, policy, distantCertificate);
    }

    public PublicKey getPublicKey() throws SecurityNotAvailableException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        return this.securityManager.getPublicKey();
    }

    public byte[] randomValue(long sessionID, byte[] clientRandomValue) throws SecurityNotAvailableException,
            RenegotiateSessionException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        return this.securityManager.randomValue(sessionID, clientRandomValue);
    }

    public byte[] publicKeyExchange(long sessionID, byte[] signature) throws SecurityNotAvailableException,
            RenegotiateSessionException, KeyExchangeException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        return this.securityManager.publicKeyExchange(sessionID, signature);
    }

    public byte[][] secretKeyExchange(long sessionID, byte[] encodedAESKey, byte[] encodedIVParameters,
            byte[] encodedClientMacKey, byte[] encodedLockData, byte[] parametersSignature)
            throws SecurityNotAvailableException, RenegotiateSessionException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        return this.securityManager.secretKeyExchange(sessionID, encodedAESKey, encodedIVParameters,
                encodedClientMacKey, encodedLockData, parametersSignature);
    }

    public SecurityContext getPolicy(Entities local, Entities distant) throws SecurityNotAvailableException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        return this.securityManager.getPolicy(local, distant);
    }

    //    public String getVNName() throws SecurityNotAvailableException {
    //    	if (this.securityManager == null) {
    //			throw new SecurityNotAvailableException();
    //		}
    //		return this.securityManager.getVNName();
    //    }

    //    public byte[] getCertificateEncoded() throws SecurityNotAvailableException {
    //    	if (this.securityManager == null) {
    //			throw new SecurityNotAvailableException();
    //		}
    //		return this.securityManager.getCertificateEncoded();
    //    }
    public Entities getEntities() throws SecurityNotAvailableException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        return this.securityManager.getEntities();
    }

    public void setProActiveSecurityManager(Entity user, PolicyServer policyServer)
            throws SecurityNotAvailableException, AccessControlException {
        if (this.securityManager == null) {
            throw new SecurityNotAvailableException();
        }
        this.securityManager.setProActiveSecurityManager(user, policyServer);
    }
}
