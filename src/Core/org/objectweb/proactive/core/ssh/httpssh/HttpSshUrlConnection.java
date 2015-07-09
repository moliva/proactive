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
package org.objectweb.proactive.core.ssh.httpssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ssh.SshConfig;
import org.objectweb.proactive.core.ssh.SshTunnelPool;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * @author The ProActive Team
 */
public class HttpSshUrlConnection extends java.net.HttpURLConnection {
    static Logger logger = ProActiveLogger.getLogger(Loggers.SSH);
    private HttpURLConnection _httpConnection;
    private java.util.Hashtable<String, List<String>> _properties;
    private SshTunnelPool tunnelPool;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public HttpSshUrlConnection(java.net.URL u) throws IOException {
        super(u);
        this.tunnelPool = new SshTunnelPool(new SshConfig());
        _properties = new Hashtable<String, List<String>>();
    }

    private void checkNotConnected() throws IllegalStateException {
        if (connected) {
            throw new IllegalStateException("already connected");
        }
    }

    private void checkNullKey(String str) throws NullPointerException {
        if (str == null) {
            throw new NullPointerException("null key");
        }
    }

    @Override
    public Map<String, List<String>> getRequestProperties() {
        checkNotConnected();
        return _properties;
    }

    @Override
    public String getRequestProperty(String key) {
        checkNotConnected();
        if (key == null) {
            return null;
        }
        List<String> list = _properties.get(key);
        String retval = null;
        if (list != null) {
            retval = list.get(0);
        }
        return retval;
    }

    @Override
    public void setRequestProperty(String key, String value) {
        checkNotConnected();
        checkNullKey(key);
        ArrayList<String> list = new ArrayList<String>();
        list.add(value);
        _properties.put(key, list);
    }

    @Override
    public void addRequestProperty(String key, String value) {
        checkNotConnected();
        checkNullKey(key);
        List<String> list = _properties.get(key);
        if (list == null) {
            list = new ArrayList<String>();
        }
        list.add(value);
        _properties.put(key, list);
    }

    @Override
    public void setInstanceFollowRedirects(boolean followRedirects) {
        super.setInstanceFollowRedirects(followRedirects);
        if (_httpConnection != null) {
            _httpConnection.setInstanceFollowRedirects(followRedirects);
        }
    }

    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        super.setRequestMethod(method);
        if (_httpConnection != null) {
            _httpConnection.setRequestMethod(method);
        }
    }

    @Override
    public int getResponseCode() throws IOException {
        ensureTunnel();
        return _httpConnection.getResponseCode();
    }

    @Override
    public void connect() throws IOException {
        ensureTunnel();
        _httpConnection.connect();
        connected = true;
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public boolean usingProxy() {
        if (_httpConnection != null) {
            return _httpConnection.usingProxy();
        } else {
            return false;
        }
    }

    @Override
    public InputStream getErrorStream() {
        if (!connected) {
            return null;
        } else {
            // if we are connected, _httpConnection is a valid field.
            return _httpConnection.getErrorStream();
        }
    }

    @Override
    public String getHeaderField(String name) {
        try {
            ensureTunnel();
            return _httpConnection.getHeaderField(name);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getHeaderField(int n) {
        try {
            ensureTunnel();
            return _httpConnection.getHeaderField(n);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getHeaderFieldKey(int n) {
        try {
            ensureTunnel();
            return _httpConnection.getHeaderFieldKey(n);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        try {
            ensureTunnel();
            return _httpConnection.getHeaderFields();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        ensureTunnel();
        return _httpConnection.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        ensureTunnel();
        return _httpConnection.getOutputStream();
    }

    @Override
    public String toString() {
        return HttpSshUrlConnection.class.getName() + ":" + url.toString();
    }

    private void ensureSetup(HttpURLConnection connection) {
        connection.setDoInput(getDoInput());
        connection.setDoOutput(getDoOutput());
        connection.setAllowUserInteraction(getAllowUserInteraction());
        connection.setIfModifiedSince(getIfModifiedSince());
        connection.setUseCaches(getUseCaches());

        connection.setInstanceFollowRedirects(getInstanceFollowRedirects());
        try {
            connection.setRequestMethod(getRequestMethod());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<Map.Entry<String, List<String>>> set = _properties.entrySet();
        for (java.util.Iterator<Map.Entry<String, List<String>>> i = set.iterator(); i.hasNext();) {
            Map.Entry<String, List<String>> entry = i.next();
            String key = entry.getKey();
            List<String> values = entry.getValue();
            for (Iterator<String> j = values.iterator(); j.hasNext();) {
                String val = j.next();
                try {
                    connection.addRequestProperty(key, val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void ensureTunnel() throws IOException {
        if (_httpConnection != null) {
            return;
        }
        java.net.URL u = getURL();
        logger.debug("create http " + url.toString());
        String host = u.getHost();
        int port = u.getPort();
        Socket socket = tunnelPool.getSocket(host, port);
        URL httpURL = new URL("http://" + socket.getInetAddress().getHostName() + ":" + socket.getPort() +
            u.getPath());
        _httpConnection = (HttpURLConnection) httpURL.openConnection();
        ensureSetup(_httpConnection);
    }
}
