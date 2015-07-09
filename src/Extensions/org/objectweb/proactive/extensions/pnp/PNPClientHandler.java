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

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.pnp.PNPAgent.PNPClientChannel;
import org.objectweb.proactive.extensions.pnp.exception.PNPException;


/** The client side handler of the PNP protocol
 *
 * @since ProActive 4.3.0
 */
@ChannelPipelineCoverage("one")
class PNPClientHandler extends IdleStateAwareChannelHandler {
    final private static Logger logger = ProActiveLogger.getLogger(PNPConfig.Loggers.PNP_HANDLER_CLIENT);

    /** The name of this handler */
    final static String NAME = "PNPClientHandler";

    /** The {@link PNPClientChannel}
     *
     * Unfortunately there is no way to set this field from the constructor since
     * we don't have a reference on the {@link PNPClientHandler} in the
     * pipeline factory. This field must be set by using
     * {@link PNPClientHandler#setPnpClientChannel(PNPClientChannel)}
     */
    volatile private PNPClientChannel pnpClientChannel = null;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        // Must be called ASAP to avoid late heartbeat
        pnpClientChannel.signalInputMessage();

        if (!(e.getMessage() instanceof PNPFrame)) {
            throw new PNPException("Invalid message type " + e.getMessage().getClass().getName());
        }

        PNPFrame msg = (PNPFrame) e.getMessage();
        switch (msg.getType()) {
            case CALL_RESPONSE:
                pnpClientChannel.receiveResponse((PNPFrameCallResponse) msg);
                break;
            case HEARTBEAT:
                // OK we already notified the client channel
                break;
            default:
                throw new PNPException("Unexpected message type: " + msg);
        }
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
        this.pnpClientChannel.signalIdle();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if (this.pnpClientChannel != null) {
            this.pnpClientChannel.close("exception caught", e.getCause());
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Connection failed (an exception will be throw to the user code by " +
                    PNPAgent.class.getName() + " ) ", e.getCause());
            }
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.pnpClientChannel.close("channel disconnected", null);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Channel closed " + e.getChannel());
        }
    }

    /** Set the {@link PNPClientChannel} associated to this Channel
     *
     * @param cChanel the PNP Client Channel
     */
    protected void setPnpClientChannel(PNPClientChannel cChanel) {
        if (this.pnpClientChannel != null) {
            logger.error("PPN Client channel already set", new PNPException());
            return;
        }

        this.pnpClientChannel = cChanel;
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        boolean ok = false;

        if (e.getMessage() instanceof PNPFrameCall) {
            ok = true;
            if (logger.isTraceEnabled()) {
                PNPFrameCall msg = (PNPFrameCall) e.getMessage();
                logger.trace("Written  request #" + msg.getCallId() + " on " + e.getChannel());
            }
        } else if (e.getMessage() instanceof PNPFrameHeartbeatAdvertisement) {
            ok = true;
            if (logger.isTraceEnabled()) {
                PNPFrameHeartbeatAdvertisement msg = (PNPFrameHeartbeatAdvertisement) e.getMessage();
                logger.trace("Written  hbadv  #" + msg.getHeartbeatPeriod() + " on " + e.getChannel());
            }
        }

        if (ok) {
            ctx.sendDownstream(e);
        } else {
            throw new PNPException("Invalid message type " + e.getMessage().getClass().getName());
        }
    }
}
