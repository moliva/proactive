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
package org.objectweb.proactive.core.debug.tunneling;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.objectweb.proactive.annotation.ImmediateService;
import org.objectweb.proactive.api.PAActiveObject;


public abstract class AbstractDebuggerSocket {

    /** The target */
    protected AbstractDebuggerSocket target;
    /** The connection */
    protected Connection connection = null;
    /** The connection will be activated on creation */
    private boolean toActivate = false;
    /** Control of the reading */
    private volatile boolean keepReading;
    /** Port of the socket */
    protected volatile int port = 0;

    /**
     * Start to read all activated connections one by one
     */
    public void startRead() {
        keepReading = true;
        new Thread() {
            public void run() {
                try {
                    while (keepReading) {
                        if (connection != null && connection.isActive()) {
                            Data data = read();
                            if (data != null && !data.isEmpty()) {
                                target.write(data);
                            }
                        }
                    }
                } catch (IOException e) {
                    stopRead();
                    closeConnection();
                }
            }
        }.start();
    }

    /**
     * Stop reading connections
     */
    public void stopRead() {
        keepReading = false;
    }

    /**
     * Performs the handshake
     *
     * @param socket
     *            The socket
     * @throws IOException
     */
    abstract public void handshake(Socket socket) throws IOException;

    /**
     * Create and add a new connection. The connection is enabled if the target
     * connection (with same id) exists. The target connection will be enabled.
     *
     * @param socket
     *            The socket of the connection
     * @param id
     *            The id of the connection
     * @throws IOException
     */
    public void addConnection(Socket socket) throws IOException {
        handshake(socket);
        connection = new Connection(socket);
        System.out.println("connected: " + this);
        if (toActivate) {
            connection.activate();
            toActivate = false;
        }
        target.activate();
        startRead();
    }

    /**
     * Read the content of a connection
     *
     * @param id
     *            The id of the connection
     * @return The data readed
     * @throws IOException
     */
    public Data read() throws IOException {
        try {
            Data data = null;
            if (connection != null) {
                data = connection.read();
            }
            return data;
        } catch (SocketException e) {
            sendError(e);
            throw e;
        }
    }

    /**
     * Write some data in a connection
     *
     * @param data
     *            The data to write
     * @param id
     *            The id of the connection
     * @throws IOException
     */
    public void write(Data data) throws IOException {
        try {
            connection.write(data);
        } catch (SocketException e) {
            sendError(e);
            throw e;
        }
    }

    /**
     * Close a connection
     *
     * @param id
     *            The id of the connection
     */
    public void closeConnection() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
        stopRead();
    }

    /**
     * Activate a connection. For internal use only.
     *
     * @param id
     *            The id of the connection.
     */
    @ImmediateService
    public void activate() {
        if (connection != null) {
            connection.activate();
        } else {
            toActivate = true;
        }
    }

    /**
     * Get target
     *
     * @return The target
     */
    public AbstractDebuggerSocket getTarget() {
        return target;
    }

    /**
     * Set the target
     *
     * @param target
     *            The target
     */
    public void setTarget(AbstractDebuggerSocket target) {
        this.target = target;
    }

    /**
     * Get port
     *
     * @return The port
     */
    @ImmediateService
    public int getPort() {
        return port;
    }

    /**
     * Set the port
     *
     * @param port
     *            The port
     */
    @ImmediateService
    public void setPort(int port) {
        this.port = port;
    }

    public String toString() {
        return "#<" + this.getClass() + " port=" + port + ">";
    }

    /**
     * Close the connection
     */
    public void stopConnection() {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Terminate the active object
     */
    public void terminate() {
        terminate(false);
    }

    /**
     * Terminate the active object
     * @param immediate Kill the ActiveObject immediatly
     */
    public void terminate(boolean immediate) {
        PAActiveObject.terminateActiveObject(immediate);
    }

    /**
     * For internal use only
     *
     * @param exception
     *            An IOException
     */
    private void sendError(IOException exception) {
        target.closeConnection();
    }

}
