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
package org.objectweb.proactive.examples.userguide.distributedprimes;

import java.io.Serializable;
import java.util.Vector;

import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


@ActiveObject
public class PrimeManager implements Serializable {
    private Vector<PrimeWorker> workers = new Vector<PrimeWorker>();

    public PrimeManager() {
    } ////empty no-arg constructor needed by ProActive

    //1. send number to all workers and if all of them say the
    //number is prime then it is
    //2. send the number randomly to one worker if prime
    //3. try the next number
    public void startComputation(long maxNumber) {
        boolean prime;//true after checking if a number is prime 
        int futureIndex;//updated future index;
        long primeCheck = 2; //start number
        long primeCounter = 1;
        int k = 0;
        Vector<BooleanWrapper> answers = new Vector<BooleanWrapper>();
        while (primeCounter < maxNumber) {
            //1. send request to all workers
            for (PrimeWorker worker : workers)
                // Non blocking (asynchronous method call)
                // adds the futures to the vector
                answers.add(worker.isPrime(primeCheck));
            //2. wait for all the answers, or an answer that says NO 
            prime = true;
            while (!answers.isEmpty() && prime) {//repeat until a worker says no or all the workers responded (i.e. vector is emptied)
                // Will block until a new response is available
                futureIndex = PAFuture.waitForAny(answers); //blocks until a future is actualized 
                prime = answers.get(futureIndex).getBooleanValue(); //check the answer
                answers.remove(futureIndex); //remove the actualized future
            }// end while check for primes
            if (prime) { //print if prime
                sendPrime(primeCheck);
                System.out.print(primeCheck + ", ");
                primeCounter++;//prime number found 
                //flush print buffer every 20 numbers
                if (k % 20 == 0)
                    System.out.println("\n");
                k++;
            }
            //flush the answers vector
            answers.clear();
            primeCheck++;
        }//end while number loop
    }// end StartComputation

    //add a workers to the worker Vector
    public void addWorker(PrimeWorker worker) {
        workers.add(worker);
    }

    //sends the prime numbers found  to one worker randomly
    public void sendPrime(long number) {
        int destination = (int) Math.round(Math.random() * (workers.size() - 1));
        workers.get(destination).addPrime(new Long(number));
    }

}
