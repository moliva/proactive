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
package org.objectweb.proactive.extensions.annotation.callbacks.isready;

import java.util.Collection;
import java.util.Iterator;

import org.objectweb.proactive.extensions.annotation.common.ErrorMessages;

import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.type.VoidType;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import com.sun.mirror.util.SourcePosition;


public class VirtualNodeIsReadyCallbackVisitorAPT extends SimpleDeclarationVisitor {

    private final Messager _compilerOutput;

    public VirtualNodeIsReadyCallbackVisitorAPT(final Messager messager) {
        super();
        _compilerOutput = messager;
    }

    @Override
    public void visitMethodDeclaration(MethodDeclaration methodDeclaration) {

        boolean correctSignature = false;
        Collection<ParameterDeclaration> methodParams = methodDeclaration.getParameters();
        // return type must be void
        if (methodDeclaration.getReturnType() instanceof VoidType && methodParams.size() == 1) {
            Iterator<ParameterDeclaration> it = methodParams.iterator();
            ParameterDeclaration param = it.next();

            if (param.getType().toString().equals(String.class.getName())) {
                correctSignature = true;
            }
        }

        if (!correctSignature) {
            reportError(methodDeclaration, ErrorMessages.INCORRECT_METHOD_SIGNATURE_FOR_ISREADY_CALLBACK);
        }
    }

    protected void reportError(Declaration declaration, String msg) {
        SourcePosition sourceCodePos = declaration.getPosition();
        _compilerOutput.printError(sourceCodePos, "[ERROR] " + msg);
    }

}