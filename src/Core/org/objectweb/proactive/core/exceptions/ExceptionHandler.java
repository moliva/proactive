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
package org.objectweb.proactive.core.exceptions;

import java.lang.reflect.Method;
import java.util.Collection;

import org.objectweb.proactive.core.body.future.FutureProxy;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.MethodCallExceptionContext;


public class ExceptionHandler {

    /* Called by the user */
    public static void tryWithCatch(Class<?>[] exceptions) {
        ExceptionMaskStack stack = ExceptionMaskStack.get();
        synchronized (stack) {
            stack.waitForIntersection(exceptions);
            stack.push(exceptions);
        }
    }

    public static void throwArrivedException() {
        ExceptionMaskStack stack = ExceptionMaskStack.get();
        synchronized (stack) {
            stack.throwArrivedException();
        }
    }

    public static void waitForPotentialException() {
        ExceptionMaskStack stack = ExceptionMaskStack.get();
        synchronized (stack) {
            stack.waitForPotentialException(true);
        }
    }

    public static void endTryWithCatch() {
        ExceptionMaskStack stack = ExceptionMaskStack.get();
        synchronized (stack) {
            stack.waitForPotentialException(false);
        }
    }

    public static void removeTryWithCatch() {
        ExceptionMaskStack stack = ExceptionMaskStack.get();
        synchronized (stack) {
            stack.pop();
        }
    }

    public static Collection<Throwable> getAllExceptions() {
        ExceptionMaskStack stack = ExceptionMaskStack.get();
        synchronized (stack) {
            return stack.getAllExceptions();
        }
    }

    /* Called by ProActive on the client side */
    public static void addRequest(MethodCall methodCall, FutureProxy future) {
        MethodCallExceptionContext context = methodCall.getExceptionContext();
        if (context.isExceptionAsynchronously() || context.isRuntimeExceptionHandled()) {
            ExceptionMaskStack stack = ExceptionMaskStack.get();
            synchronized (stack) {
                Method m = methodCall.getReifiedMethod();
                ExceptionMaskLevel level = stack.findBestLevel(m.getExceptionTypes());
                level.addFuture(future);
            }
        }
    }

    public static void addResult(FutureProxy future) {
        ExceptionMaskLevel level = future.getExceptionLevel();
        if (level != null) {
            level.removeFuture(future);
        }
    }

    public static MethodCallExceptionContext getContextForCall(Method m) {
        ExceptionMaskStack stack = ExceptionMaskStack.get();
        synchronized (stack) {
            boolean runtime = stack.isRuntimeExceptionHandled();
            boolean async = stack.areExceptionTypesCaught(m.getExceptionTypes());
            MethodCallExceptionContext res = new MethodCallExceptionContext(runtime, async);

            //            System.out.println(m + " => " + res);
            return res;
        }
    }

    public static void throwException(Throwable exception) {
        if (exception instanceof RuntimeException) {
            throw (RuntimeException) exception;
        }

        if (exception instanceof Error) {
            throw (Error) exception;
        }

        ExceptionMaskStack stack = ExceptionMaskStack.get();
        synchronized (stack) {
            if (!stack.isExceptionTypeCaught(exception.getClass())) {
                RuntimeException re = new IllegalStateException("Invalid Future Usage");
                re.initCause(exception);
                throw re;
            }

            ExceptionThrower.throwException(exception);
        }
    }
}
