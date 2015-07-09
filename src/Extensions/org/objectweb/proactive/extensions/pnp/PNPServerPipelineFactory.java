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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.pnp;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.objectweb.proactive.utils.NamedThreadFactory;


class PNPServerPipelineFactory implements ChannelPipelineFactory {
    /** The thread pool to be used to execute {@link PNPROMessage} */
    final private Executor executor;

    final private Timer timer;

    final private PNPExtraHandlers extraHandlers;

    public PNPServerPipelineFactory(PNPExtraHandlers extraHandlers, Executor executor) {
        this.extraHandlers = extraHandlers;

        this.executor = executor;
        NamedThreadFactory tf = new NamedThreadFactory("PNP server handler timer (shared)", true,
            Thread.MAX_PRIORITY);
        this.timer = new HashedWheelTimer(tf, 10, TimeUnit.MILLISECONDS);
    }

    public ChannelPipeline getPipeline() throws Exception {
        PNPServerHandler pnpServerHandler = new PNPServerHandler(this.executor);
        ChannelPipeline p = Channels.pipeline();

        if (extraHandlers != null) {
            for (final ChannelHandler handler : extraHandlers.getServertHandlers()) {
                p.addLast("" + handler.hashCode(), handler);
            }
        }

        p.addLast("pnpDecoder", new PNPServerFrameDecoder(pnpServerHandler, timer));
        p.addLast("frameEncoder", new LengthFieldPrepender(4));
        p.addLast("pnpEncoder", new PNPEncoder());
        p.addLast(PNPServerHandler.NAME, pnpServerHandler);
        return p;
    }
}
