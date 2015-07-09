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

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.objectweb.proactive.core.body.future.MethodCallResult;
import org.objectweb.proactive.core.remoteobject.SynchronousReplyImpl;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.util.converter.ObjectToByteConverter;
import org.objectweb.proactive.core.util.converter.remote.ProActiveMarshaller;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.pnp.exception.PNPException;


/** The server side handler of the PNP protocol*/
@ChannelPipelineCoverage("one")
class PNPServerHandler extends SimpleChannelHandler {
    static final private Logger logger = ProActiveLogger.getLogger(PNPConfig.Loggers.PNP_HANDLER_SERVER);

    /** The name of this handler */
    final static String NAME = "PNPServerHandler";
    /** The executor to be used to run the {@link PNPROMessage} */
    // Requests must be handled in separate threads to avoid deadlock
    final private Executor executor;
    /** The object in charge of sending heartbeats to the client */
    private Heartbeater hearthbeater;
    /** Serialization */
    final private ProActiveMarshaller marshaller;

    public PNPServerHandler(Executor executor) {
        this.executor = executor;
        String runtimeUrl = ProActiveRuntimeImpl.getProActiveRuntime().getURL();
        this.marshaller = new ProActiveMarshaller(runtimeUrl);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (!(message instanceof PNPFrameCall)) {
            throw new PNPException("Unexpected message type " + message.getClass().getName());
        }

        PNPFrameCall msgReq = (PNPFrameCall) message;
        executor.execute(new RequestExecutor(msgReq, e.getChannel(), hearthbeater, this.marshaller));
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
        if (logger.isDebugEnabled()) {
            if (e.getMessage() instanceof PNPFrameCallResponse) {
                PNPFrameCallResponse res = (PNPFrameCallResponse) e.getMessage();
                logger.debug("Written  response #" + res.getCallId() + " on " + e.getChannel());
            } else if (e.getMessage() instanceof PNPFrameHeartbeat) {
                PNPFrameHeartbeat hb = (PNPFrameHeartbeat) e.getMessage();
                logger.debug("Written  hearthbt #" + hb.hearthbeatId + " on " + e.getChannel());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.debug("Exception caught in PNP server handler on: " + e.getChannel() + ". Closing connection",
                e.getCause());
        e.getChannel().close();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Channel connected " + e.getChannel());
        }
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (this.hearthbeater != null) {
            this.hearthbeater.cancel();
        }

        super.channelClosed(ctx, e);
        if (logger.isDebugEnabled()) {
            logger.debug("Channel closed " + e.getChannel());
        }
    }

    /** Send heartbeat to the client when needed
     *
     * TODO: Explain the server side heartbeat algorithm
     */
    static class Heartbeater implements TimerTask {
        static final private long DEFAULT_EXTRA_TIME = 4; // See Parking#DEFAULT_EXTRA_TIME

        /** The channel to heartbeat */
        final private Channel channel;
        /** The heartbeat period of this channel*/
        final long heartbeatPeriod;

        // Cache coherence is ensured by method synchronization (see JMM)
        /** The number of {@link PNPROMessage} currently processed (in the channel) */
        int cClientCount;
        /** The timer thread */
        Timer timer;
        /** The current heartbeat ID*/
        long cHeartbeatId;
        /** the current extra time */
        int cGraceTime;
        /** Signal that the channel is closed*/
        volatile boolean canceled;
        /** Is the timer currently armed ? */
        boolean scheduled;

        public Heartbeater(Channel channel, Timer timer, long heartbeatPeriod) {
            this.channel = channel;
            this.heartbeatPeriod = heartbeatPeriod;
            this.timer = timer;

            this.cHeartbeatId = 0;
            this.cClientCount = 0;
            this.cGraceTime = 0;

            this.canceled = false;
        }

        synchronized public void clientEnter() {
            if (this.heartbeatPeriod == 0)
                return;

            this.cGraceTime = 0;
            this.cClientCount++;

            if (!this.scheduled) {
                this.timer.newTimeout(this, heartbeatPeriod / 2, TimeUnit.MILLISECONDS);
                this.scheduled = true;
            }
        }

        synchronized public void clientLeave() {
            if (this.heartbeatPeriod == 0)
                return;

            this.cClientCount--;
        }

        synchronized public void run(Timeout timeout) {
            this.scheduled = false;

            if (((this.cClientCount == 0) && (this.cGraceTime++ > DEFAULT_EXTRA_TIME)) || this.canceled) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Canceled the heartbeater timer task for " + this.channel);
                }
                return;
            }

            this.timer.newTimeout(this, heartbeatPeriod / 2, TimeUnit.MILLISECONDS);
            this.scheduled = true;

            final long heartbeatId = this.cHeartbeatId++;
            PNPFrameHeartbeat hearthbeat = new PNPFrameHeartbeat(heartbeatId);
            if (logger.isDebugEnabled()) {
                logger.debug("Sending eartbeat " + heartbeatId + " on " + channel);
            }
            ChannelFuture cf = channel.write(hearthbeat); // FIXME check return value
            cf.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess() && !canceled) {
                        logger.info("Failed to send heartbeat " + heartbeatId + " on " + channel, future
                                .getCause());
                    }
                }
            });
        }

        public void cancel() {
            this.canceled = true;
        }
    }

    /** Execute a {@link PNPROMessage} */
    static class RequestExecutor implements Runnable {
        /** The message request*/
        final private PNPFrameCall req;
        /** The channel to be used to send the response*/
        final private Channel channel;
        /** The heartbeater to notify when the handling is finished */
        final private Heartbeater hearthbeater;
        /** Serialization */
        final private ProActiveMarshaller marshaller;

        public RequestExecutor(PNPFrameCall req, Channel channel, Heartbeater hearthbeater,
                ProActiveMarshaller marshaller) {
            this.req = req;
            this.channel = channel;
            this.hearthbeater = hearthbeater;
            this.marshaller = marshaller;
        }

        public void run() {
            if (logger.isTraceEnabled()) {
                logger.trace("Received request #" + req.getCallId() + " on " + channel);
            }

            ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

                // Handle the call

                // Unmarshall the data
                PNPROMessage pnpMessage = null;
                try {
                    pnpMessage = (PNPROMessage) marshaller.unmarshallObject(req.getPayload());
                } catch (Throwable t) {
                    // Sends a response call
                    PNPException e = new PNPException("Failed to unmarshall incoming message", t);
                    SynchronousReplyImpl sr = new SynchronousReplyImpl(new MethodCallResult(null, e));
                    byte[] b = ObjectToByteConverter.ProActiveObjectStream.convert(sr);
                    PNPFrameCallResponse msgResp = new PNPFrameCallResponse(req.getCallId(), b);
                    ChannelFuture cf = this.channel.write(msgResp);
                    cf.addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                logger.warn("Failed to send response to call  #" + req.callId + " on " +
                                    channel, future.getCause());
                            }
                        }
                    });
                    return;
                }

                Object result = pnpMessage.processMessage();

                byte[] resultBytes = null;
                try {
                    resultBytes = this.marshaller.marshallObject(result);
                } catch (Throwable t) {
                    // Sends a response call
                    PNPException e = new PNPException("Failed to marshall the result bytes", t);
                    SynchronousReplyImpl sr = new SynchronousReplyImpl(new MethodCallResult(null, e));
                    byte[] b = ObjectToByteConverter.ProActiveObjectStream.convert(sr);
                    PNPFrameCallResponse msgResp = new PNPFrameCallResponse(req.getCallId(), b);
                    ChannelFuture cf = this.channel.write(msgResp);
                    cf.addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                logger.info("Failed to send response to call  #" + req.callId + " on " +
                                    channel, future.getCause());
                            }
                        }
                    });
                    return;
                }

                PNPFrameCallResponse msgResp = new PNPFrameCallResponse(req.getCallId(), resultBytes);
                ChannelFuture cf = this.channel.write(msgResp);
                cf.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            logger.info("Failed to send response to call  #" + req.callId + " on " + channel,
                                    future.getCause());
                        }
                    }
                });
            } catch (Exception ex) {
                logger.info("BPN call handling failed", ex);
            } finally {
                Thread.currentThread().setContextClassLoader(savedClassLoader);
                hearthbeater.clientLeave();
            }
        }
    }

    public void setHeartBeater(Heartbeater heartbeater) {
        this.hearthbeater = heartbeater;
    }

    /** Used by the server side frame decoder to signal that heartbeats must be send*/
    protected void bytesAvailable() {
        this.hearthbeater.clientEnter();
    }

    /** Used by the server side frame decoder to signal that the decoding of the message failed.
     *
     * Thus it is no longer needed to send heartbeats
     */
    protected void clientLeave() {
        this.hearthbeater.clientLeave();
    }
}
