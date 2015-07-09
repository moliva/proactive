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
package org.objectweb.proactive.ext.hpc.exchange;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.RequestImpl;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.ext.hpc.DummySender;
import org.objectweb.proactive.ext.hpc.MethodCallDummy;


public class RequestExchange extends RequestImpl {

    // Variables related to source and destination ranks
    transient private int tagID;
    transient private int destinationUID;

    // Variables related to source and destination arrays
    transient protected int dataType;
    transient protected byte[] byteArray;
    transient protected double[] doubleArray;
    transient protected int[] intArray;
    transient protected int offsetArray;
    transient protected int lenArray;
    transient protected ExchangeableArrayPointer exchangeableArrayPointer;

    //
    // -- CONSTRUCTORS ---------------------------------------------------------
    //
    private RequestExchange(int tagID, int offset, int len, int dstUID) {
        super(MethodCallDummy.getImmediateMethodCallDummy(), true /* OneWay */);
        this.tagID = tagID;
        this.offsetArray = offset;
        this.lenArray = len;
        this.destinationUID = dstUID;
        this.senderNodeURI = DummySender.getDummySender().getNodeURL();
    }

    private RequestExchange(int tagID, byte[] array, int offset, int len, int dstUID) {
        this(tagID, offset, len, dstUID);
        this.byteArray = array;
        this.dataType = ExchangeableArrayPointer.BYTE_ARRAY;
    }

    private RequestExchange(int tagID, double[] array, int offset, int len, int dstUID) {
        this(tagID, offset, len, dstUID);
        this.doubleArray = array;
        this.dataType = ExchangeableArrayPointer.DOUBLE_ARRAY;
    }

    private RequestExchange(int tagID, int[] array, int offset, int len, int dstUID) {
        this(tagID, offset, len, dstUID);
        this.intArray = array;
        this.dataType = ExchangeableArrayPointer.INT_ARRAY;
    }

    private RequestExchange(int tagID, ExchangeableArrayPointer exchangeableArrayPointer, int dstUID) {
        this(tagID, 0, 0, dstUID);
        this.exchangeableArrayPointer = exchangeableArrayPointer;
        this.dataType = ExchangeableArrayPointer.EXCHANGEABLE_DOUBLE;
    }

    //
    // --- FACTORY -------------------------------------------------------------
    //  
    public static RequestExchange getRequestExchange(int tagID, byte[] array, int offset, int len, int dstUID) {
        return getRequestExchange(tagID, offset, len, dstUID).setByteArray(array);
    }

    public static RequestExchange getRequestExchange(int tagID, double[] array, int offset, int len,
            int dstUID) {
        return getRequestExchange(tagID, offset, len, dstUID).setDoubleArray(array);
    }

    public static RequestExchange getRequestExchange(int tagID, int[] array, int offset, int len, int dstUID) {
        return getRequestExchange(tagID, offset, len, dstUID).setIntArray(array);
    }

    public static RequestExchange getRequestExchange(int tagID,
            ExchangeableArrayPointer exchangeableArrayPointer, int dstUID) {
        return new RequestExchange(tagID, exchangeableArrayPointer, dstUID);
    }

    private static RequestExchange getRequestExchange(int tagID, int offset, int len, int dstUID) {
        return new RequestExchange(tagID, offset, len, dstUID);
    }

    private RequestExchange setByteArray(byte[] array) {
        this.byteArray = array;
        this.dataType = ExchangeableArrayPointer.BYTE_ARRAY;

        return this;
    }

    private RequestExchange setDoubleArray(double[] array) {
        this.doubleArray = array;
        this.dataType = ExchangeableArrayPointer.DOUBLE_ARRAY;

        return this;
    }

    private RequestExchange setIntArray(int[] array) {
        this.intArray = array;
        this.dataType = ExchangeableArrayPointer.INT_ARRAY;

        return this;
    }

    //
    // --- PUBLIC METHODS OVERRIDING -------------------------------------------
    //  
    @Override
    public Reply serve(Body targetBody) {
        return null;
    }

    @Override
    public MethodCall getMethodCall() {
        return MethodCallDummy.getImmediateMethodCallDummy();
    }

    @Override
    public UniversalBody getSender() {
        return (UniversalBody) DummySender.getDummySender();
    }

    //
    // --- PROTECTED METHODS FOR SERIALIZATION ---------------------------------
    //
    @Override
    protected void writeTheObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.writeInt(this.destinationUID);
        out.writeInt(this.tagID);
        int endArray;
        switch (this.dataType) {
            case ExchangeableArrayPointer.BYTE_ARRAY:
                out.write(this.byteArray, this.offsetArray, this.lenArray);

                break;

            case ExchangeableArrayPointer.DOUBLE_ARRAY:
                endArray = this.offsetArray + this.lenArray;
                for (int i = this.offsetArray; i < endArray; i++) {
                    out.writeDouble(this.doubleArray[i]);
                }

                break;

            case ExchangeableArrayPointer.INT_ARRAY:
                endArray = this.offsetArray + this.lenArray;
                for (int i = this.offsetArray; i < endArray; i++) {
                    out.writeInt(this.intArray[i]);
                }

                break;

            case ExchangeableArrayPointer.EXCHANGEABLE_DOUBLE:
                ExchangeableDouble src = this.exchangeableArrayPointer.getExchangeDouble();
                while (src.hasNextGet()) {
                    out.writeDouble(src.get());
                }
                break;
        }
    }

    @Override
    protected void readTheObject(java.io.ObjectInputStream in) throws java.io.IOException,
            ClassNotFoundException {
        int dstUID = in.readInt();
        int tagID = in.readInt();

        ExchangeManager manager = ExchangeManager.getExchangeManager(dstUID);
        ExchangeableArrayPointer exchangeableArrayPointer = manager.getExchangeableArrayPointer(tagID);

        int len = exchangeableArrayPointer.getLenArray();
        int dataType = exchangeableArrayPointer.getDataType();
        int endArray = exchangeableArrayPointer.getOffset() + len;

        switch (dataType) {
            case ExchangeableArrayPointer.BYTE_ARRAY:
                in.readFully(exchangeableArrayPointer.getByteArray(), exchangeableArrayPointer.getOffset(),
                        len);
                break;

            case ExchangeableArrayPointer.DOUBLE_ARRAY:

                for (int i = exchangeableArrayPointer.getOffset(); i < endArray; i++) {
                    exchangeableArrayPointer.getDoubleArray()[i] = in.readDouble();
                }
                break;

            case ExchangeableArrayPointer.INT_ARRAY:

                for (int i = exchangeableArrayPointer.getOffset(); i < endArray; i++) {
                    exchangeableArrayPointer.getIntArray()[i] = in.readInt();
                }
                break;

            case ExchangeableArrayPointer.EXCHANGEABLE_DOUBLE:
                ExchangeableDouble dst = exchangeableArrayPointer.getExchangeDouble();
                while (dst.hasNextPut()) {
                    dst.put(in.readDouble());
                }
                break;
        }
        manager.setReady(tagID);
        senderNodeURI = DummySender.getDummySender().getNodeURL();
    }
}
