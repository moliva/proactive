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

public class ShutdownThread extends Thread {
    //    //singleton pattern because of java bug #4533
    //    private static ShutdownThread sh;
    //
    //    static {
    //        sh = new ShutdownThread();
    //        Runtime.getRuntime().addShutdownHook(sh);
    //    }
    //
    //    public static synchronized void addStream(BenchStream b) {
    //        ShutdownThread.sh.streamList.add(b);
    //        System.out.println("Adding current size is " +
    //            ShutdownThread.sh.streamList.size());
    //    }
    //
    //    public static synchronized boolean removeStream(BenchStream b) {
    //        boolean result = ShutdownThread.sh.streamList.remove(b);
    //        System.out.println("Removing current size is " +
    //            ShutdownThread.sh.streamList.size());
    //        return result;
    //    }
    //
    //    private LinkedList streamList;
    //
    //    private ShutdownThread() {
    //        this.streamList = new LinkedList();
    //    }
    //
    //    public void run() {
    //        ////        this.bos.displayTotal();
    //        synchronized (sh.streamList) {
    //        	System.out.println("Run on list with " + this.streamList.size() +
    //        	" elements");
    //            Iterator it = this.streamList.iterator();
    //            while (it.hasNext()) {
    //                ((BenchStream) it.next()).displayTotal();
    //            }
    //        }
    //    }
    protected boolean fakeRun;
    protected BenchStream stream;

    public ShutdownThread() {
    }

    public ShutdownThread(BenchStream s) {
        this.stream = s;
    }

    protected void fakeRun() {
        this.fakeRun = true;
        this.start();
    }

    @Override
    public void run() {
        ////        this.bos.displayTotal();
        //System.out.println("XXXfakerun " + fakeRun);
        if (!this.fakeRun) {
            this.stream.displayTotal();
        }
    }
}
