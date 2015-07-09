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
package benchsocket;

import java.io.IOException;
import java.io.InputStream;


public class BenchInputStream extends InputStream implements BenchStream {
    private InputStream realInputStream;
    private int number;
    private int total;
    private BenchClientSocket parent;
    private ShutdownThread shThread;

    public BenchInputStream(InputStream real, int number) {
        this.realInputStream = real;
        this.number = number;
        // ShutdownThread.addStream(this);
        //when the JVM is killed
        try {
            shThread = new ShutdownThread(this);
            Runtime.getRuntime().addShutdownHook(shThread);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public BenchInputStream(InputStream stream, int number, BenchClientSocket parent) {
        this(stream, number);
        this.parent = parent;
    }

    public synchronized void displayTotal() {
        display("=== Total Input for socket ");
        total = 0;
    }

    public synchronized void dumpIntermediateResults() {
        display("---- Intermediate input for socket ");
    }

    protected void display(String s) {
        if (parent != null) {
            System.out.println(s + "" + number + " = " + total + " real " + parent);
        } else {
            System.out.println(s + "" + number + " = " + total);
        }
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException {
        return this.realInputStream.available();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        //    		if (ShutdownThread.removeStream(this)){
        //    			this.realInputStream.close();
        //    			//System.out.println("BenchOutputStream.close() on " + this.number);
        //    			this.displayTotal();
        //    		}
        //	if (ShutdownThread.removeStream(this)){
        if (this.realInputStream != null) {
            this.realInputStream.close();
        }

        //System.out.println("BenchOutputStream.close() on " + this.number);
        this.displayTotal();
        //	}
        //no only we remove the thread, but we also fire it
        //because of java bug #4533
        try {
            Runtime.getRuntime().removeShutdownHook(shThread);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        if (shThread != null) {
            shThread.fakeRun();
        }
        shThread = null;
        this.parent = null;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#mark(int)
     */
    @Override
    public synchronized void mark(int readlimit) {
        this.realInputStream.mark(readlimit);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported() {
        return this.realInputStream.markSupported();
    }

    @Override
    public int read() throws IOException {
        int tmp = this.realInputStream.read();

        //  System.out.println("BenchInputStream.read() on " + this.number +" " + tmp);
        if (BenchSocketFactory.measure) {
            total += 1;
        }

        // total += tmp;
        return tmp;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int tmp = this.realInputStream.read(b, off, len);

        //   System.out.println("BenchInputStream.read(byte[] b, int off, int len) on " + this.number +" " + tmp);
        if (BenchSocketFactory.measure) {
            total += tmp;
        }
        return tmp;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(byte[] b) throws IOException {
        int tmp = this.realInputStream.read(b);

        // System.out.println("BenchInputStream.read(byte[] b) on " + this.number +" " + tmp);
        if (BenchSocketFactory.measure) {
            total += tmp;
        }
        return tmp;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#reset()
     */
    @Override
    public synchronized void reset() throws IOException {
        this.realInputStream.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.realInputStream.skip(n);
    }
}
