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
package org.objectweb.proactive.api;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.group.ProxyForGroup;
import org.objectweb.proactive.core.group.spmd.MethodCallBarrier;
import org.objectweb.proactive.core.group.spmd.MethodCallBarrierWithMethodName;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.ext.hpc.exchange.ExchangeManager;
import org.objectweb.proactive.ext.hpc.exchange.ExchangeableDouble;


/**
 * <p>
 * This class provides a static method to build (an deploy) an 'SPMD' group of active objects with
 * all references between them to communicate.
 * </p>
 * <p>
 * For instance, the following code builds objects of type <code>A</code> on nodes
 * <code>node1,node2,...</code>, with parameters <code>param1,param2,...</code> and build for
 * each object created its diffusion group to communicate with the others.
 * </p>
 * 
 * <pre>
 * Object[] params = {param1,param2,...};
 * Node[] nodes = {node1,node2,...};
 * A group  =  (A) PASPMD.newSPMDGroup(&quot;A&quot;, params, nodes);
 * </pre>
 * 
 * @version 1.0, 2003/10/09
 * @since ProActive 1.0.3
 * @author The ProActive Team
 */
@PublicAPI
public class PASPMD {

    /** The class has only static methods so it should not be instantiated. */
    private PASPMD() {
    }

    /**
     * Creates an object representing a spmd group (a typed group) and creates all members with
     * params on the node.
     * 
     * @param className -
     *            the name of the (upper) class of the group's member.
     * @param params -
     *            the array that contain the parameters used to build the group's member.
     * @param nodeName -
     *            the name (String) of the node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException
     *             if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException
     *             if the Class corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException
     *             if the Class corresponding to <code>className</code> can't be reify.
     * @throws NodeException
     *             if the node was null and that the DefaultNode cannot be created
     */
    public static Object newSPMDGroup(String className, Object[][] params, String nodeName)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException,
            NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = NodeFactory.getNode(nodeName);
        return PASPMD.newSPMDGroup(className, params, nodeList);
    }

    /**
     * Creates an object representing a spmd group (a typed group) and creates members with params
     * cycling on nodeList.
     * 
     * @param className -
     *            the name of the (upper) class of the group's member.
     * @param params -
     *            the array that contain the parameters used to build the group's member.
     * @param nodeListString -
     *            the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException
     *             if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException
     *             if the Class corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException
     *             if the Class corresponding to <code>className</code> can't be reify.
     * @throws NodeException
     *             if the node was null and that the DefaultNode cannot be created
     */
    public static Object newSPMDGroup(String className, Object[][] params, String[] nodeListString)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException,
            NodeException {
        Node[] nodeList = new Node[nodeListString.length];
        for (int i = 0; i < nodeListString.length; i++)
            nodeList[i] = NodeFactory.getNode(nodeListString[i]);
        return PASPMD.newSPMDGroup(className, params, nodeList);
    }

    /**
     * Creates an object representing a spmd group (a typed group) and creates all members with
     * params on the node.
     * 
     * @param className -
     *            the name of the (upper) class of the group's member.
     * @param params -
     *            the array that contain the parameters used to build the group's member.
     * @param node -
     *            the node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException
     *             if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException
     *             if the Class corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException
     *             if the Class corresponding to <code>className</code> can't be reify.
     * @throws NodeException
     *             if the node was null and that the DefaultNode cannot be created
     */
    public static Object newSPMDGroup(String className, Object[][] params, Node node)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException,
            NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = node;
        return PASPMD.newSPMDGroup(className, params, nodeList);
    }

    /**
     * Creates an object representing a spmd group (a typed group) and creates members with params
     * cycling on nodeList.
     * 
     * @param className -
     *            the name of the (upper) class of the group's member.
     * @param params
     *            the array that contain the parameters used to build the group's member. If
     *            <code>params</code> is <code>null</code>, builds an empty group.
     * @param nodeList -
     *            the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException
     *             if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException
     *             if the Class corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException
     *             if the Class corresponding to <code>className</code> can't be reify.
     * @throws NodeException
     *             if the node was null and that the DefaultNode cannot be created
     */
    public static Object newSPMDGroup(String className, Object[][] params, Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException,
            NodeException {
        Object result = PAGroup.newGroup(className);
        Group<Object> g = PAGroup.getGroup(result);

        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                g.add(PAActiveObject.newActive(className, params[i], nodeList[i % nodeList.length]));
            }
        }
        ((ProxyForGroup<Object>) g).setSPMDGroup(result);
        return result;
    }

    /**
     * Creates an object representing a spmd group (a typed group) and creates members with params
     * cycling on nodeList.
     *
     * @param className -
     *            the name of the (upper) class of the group's member.
     * @param params
     *            the array that contain the parameters used to build the group's member. If
     *            <code>params</code> is <code>null</code>, builds an empty group.
     * @param nodeList -
     *            the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException
     *             if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException
     *             if the Class corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException
     *             if the Class corresponding to <code>className</code> can't be reify.
     * @throws NodeException
     *             if the node was null and that the DefaultNode cannot be created
     */
    public static Object newSPMDGroup(String className, Object[][] params, List<Node> nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException,
            NodeException {
        Object result = PAGroup.newGroup(className);
        Group<Object> g = PAGroup.getGroup(result);

        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                g.add(PAActiveObject.newActive(className, params[i], nodeList.get(i % nodeList.size())));
            }
        }
        ((ProxyForGroup<Object>) g).setSPMDGroup(result);
        return result;
    }

    // -------------------------------------------------------------------------
    // InParallel OOSPMD constructors
    // -------------------------------------------------------------------------

    /**
     * Creates an object representing a spmd group (a typed group) and creates all members with
     * params on the node.
     * 
     * @param className -
     *            the name of the (upper) class of the group's member.
     * @param params -
     *            the array that contain the parameters used to build the group's member.
     * @param nodeName -
     *            the name (String) of the node where the members are created.
     * @return a typed group with its members.
     * @throws ClassNotFoundException
     *             if the Class corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException
     *             if the Class corresponding to <code>className</code> can't be reify.
     * @throws NodeException
     *             if the node was null and that the DefaultNode cannot be created
     */
    public static Object newSPMDGroupInParallel(String className, Object[][] params, String nodeName)
            throws ClassNotFoundException, ClassNotReifiableException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = NodeFactory.getNode(nodeName);
        return PASPMD.newSPMDGroupInParallel(className, params, nodeList);
    }

    /**
     * Creates an object representing a spmd group (a typed group) and creates members with params
     * cycling on nodeList.
     * 
     * @param className -
     *            the name of the (upper) class of the group's member.
     * @param params -
     *            the array that contain the parameters used to build the group's member.
     * @param nodeListString -
     *            the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ClassNotFoundException
     *             if the Class corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException
     *             if the Class corresponding to <code>className</code> can't be reify.
     * @throws NodeException
     *             if the node was null and that the DefaultNode cannot be created
     */
    public static Object newSPMDGroupInParallel(String className, Object[][] params, String[] nodeListString)
            throws ClassNotFoundException, ClassNotReifiableException, NodeException {
        Node[] nodeList = new Node[nodeListString.length];
        for (int i = 0; i < nodeListString.length; i++)
            nodeList[i] = NodeFactory.getNode(nodeListString[i]);
        return PASPMD.newSPMDGroupInParallel(className, params, nodeList);
    }

    /**
     * Creates an object representing a spmd group (a typed group) and creates all members with
     * params on the node.
     * 
     * @param className -
     *            the name of the (upper) class of the group's member.
     * @param params -
     *            the array that contain the parameters used to build the group's member.
     * @param node -
     *            the node where the members are created.
     * @return a typed group with its members.
     * @throws ClassNotFoundException
     *             if the Class corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException
     *             if the Class corresponding to <code>className</code> can't be reify.
     */
    public static Object newSPMDGroupInParallel(String className, Object[][] params, Node node)
            throws ClassNotFoundException, ClassNotReifiableException {
        Node[] nodeList = new Node[1];
        nodeList[0] = node;
        return PASPMD.newSPMDGroupInParallel(className, params, nodeList);
    }

    /**
     * Creates an object representing a spmd group (a typed group) and creates members with params
     * cycling on nodeList.
     * 
     * @param className -
     *            the name of the (upper) class of the group's member.
     * @param params
     *            the array that contain the parameters used to build the group's member. If
     *            <code>params</code> is <code>null</code>, builds an empty group.
     * @param nodeList -
     *            the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ClassNotFoundException
     *             if the Class corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException
     *             if the Class corresponding to <code>className</code> can't be reify.
     */
    public static Object newSPMDGroupInParallel(String className, Object[][] params, Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException {
        Object result = PAGroup.newGroup(className);
        ProxyForGroup<Object> proxy = (org.objectweb.proactive.core.group.ProxyForGroup<Object>) PAGroup
                .getGroup(result);

        proxy.createMemberWithMultithread(className, null, params, nodeList);

        proxy.setSPMDGroup(result);

        return result;
    }

    // -------------------------------------------------------------------------
    // End InParallel
    // -------------------------------------------------------------------------
    // /**
    // * Set the SPMD group for this
    // * @param o - the new SPMD group
    // */
    // public static void setSPMDGroupOnThis(Object o) {
    // AbstractBody body = (AbstractBody) ProActive.getBodyOnThis();
    // body.setSPMDGroup(o);
    // }
    /**
     * Returns the SPMD group of this
     * 
     * @return the SPMD group of this
     */
    // @snippet-start spmd_mygroup
    public static Object getSPMDGroup()
    // @snippet-end spmd_mygroup
    {
        AbstractBody body = (AbstractBody) PAActiveObject.getBodyOnThis();
        return body.getSPMDGroup();
    }

    /**
     * Returns the size of the SPMD group of this
     * 
     * @return a size (int)
     */
    // @snippet-start spmd_group_size
    public static int getMySPMDGroupSize()
    // @snippet-end spmd_group_size
    {
        return PAGroup.getGroup(PASPMD.getSPMDGroup()).size();
    }

    /**
     * Returns the rank (position) of the object in the Group
     * 
     * @return the index of the object
     */
    // @snippet-start spmd_rank
    public static int getMyRank()
    // @snippet-end spmd_rank
    {
        return PAGroup.getGroup(PASPMD.getSPMDGroup()).indexOf(PAActiveObject.getStubOnThis());
    }

    /**
     * Strongly synchronizes all the members of the spmd group
     * 
     * @param barrierName
     *            the name of the barrier (used as unique identifier)
     */
    // @snippet-start spmd_total_barrier
    public static void totalBarrier(String barrierName)
    // @snippet-end spmd_total_barrier
    {
        PASPMD.neighbourBarrier(barrierName, PASPMD.getSPMDGroup());
    }

    /**
     * Strongly synchronizes all the members of the group. Beware ! The caller object HAS TO BE IN
     * THE GROUP <code>group</code>
     * 
     * @param barrierName -
     *            the name of the barrier (used as unique identifier)
     * @param group -
     *            the typed group the barrier is invoked on
     */
    public static void neighbourBarrier(String barrierName, Object group) {
        try {
            // add the barrier into the tag list
            AbstractBody body = (AbstractBody) PAActiveObject.getBodyOnThis();
            body.getProActiveSPMDGroupManager().addToBarrierTags(barrierName);
            // set the number of awaited message barriers
            body.getProActiveSPMDGroupManager().setAwaitedBarrierCalls(barrierName, PAGroup.size(group));
            // send the barrier messages
            ProxyForGroup<Object> proxy = (ProxyForGroup<Object>) PAGroup.getGroup(group);
            proxy.reify(new MethodCallBarrier(barrierName));
        } catch (InvocationTargetException e) {
            System.err.println("Unable to invoke a method call to control groups");
            e.printStackTrace();
        }
    }

    /**
     * Stops the activity and wait for the methods to resume.
     * 
     * @param methodNames -
     *            the name of the methods used to synchronize
     */
    public static void methodBarrier(String[] methodNames) {
        try {
            (PAActiveObject.getStubOnThis()).getProxy().reify(
                    new MethodCallBarrierWithMethodName(methodNames));
        } catch (InvocationTargetException e) {
            System.err.println("Unable to invoke a method call to control groups");
            e.printStackTrace();
        } catch (Throwable e) {
            System.err.println("Unable to invoke a method call to control groups");
            e.printStackTrace();
        }
    }

    /**
     * Performs an exchange on a complex structure of doubles between two Active Objects.
     * 
     * @param tag
     * @param destRank
     * @param src
     * @param dst
     */
    public static void exchange(String tag, int destRank, ExchangeableDouble src, ExchangeableDouble dst) {
        Object destAO = PAGroup.get(PASPMD.getSPMDGroup(), destRank);
        ExchangeManager.getExchangeManager().exchange(tag.hashCode(), destAO, src, dst);
    }

    /**
     * Performs an exchange on an integer array between two Active Objects.
     * 
     * @param tag
     * @param destRank
     * @param srcArray
     * @param srcOffset
     * @param dstArray
     * @param dstOffset
     * @param len
     */
    public static void exchange(String tag, int destRank, int[] srcArray, int srcOffset, int[] dstArray,
            int dstOffset, int len) {
        Object destAO = PAGroup.get(PASPMD.getSPMDGroup(), destRank);
        ExchangeManager.getExchangeManager().exchange(tag.hashCode(), destAO, srcArray, srcOffset, dstArray,
                dstOffset, len);
    }

    /**
     * Performs an exchange on a double array between two Active Objects.
     * 
     * @param tag
     * @param destRank
     * @param srcArray
     * @param srcOffset
     * @param dstArray
     * @param dstOffset
     * @param len
     */
    public static void exchange(String tag, int destRank, double[] srcArray, int srcOffset,
            double[] dstArray, int dstOffset, int len) {
        Object destAO = PAGroup.get(PASPMD.getSPMDGroup(), destRank);
        ExchangeManager.getExchangeManager().exchange(tag.hashCode(), destAO, srcArray, srcOffset, dstArray,
                dstOffset, len);
    }

    /**
     * Performs an exchange on a byte array between two Active Objects.
     * 
     * @param tag
     * @param destRank
     * @param srcArray
     * @param srcOffset
     * @param dstArray
     * @param dstOffset
     * @param len
     */
    public static void exchange(String tag, int destRank, byte[] srcArray, int srcOffset, byte[] dstArray,
            int dstOffset, int len) {
        Object destAO = PAGroup.get(PASPMD.getSPMDGroup(), destRank);
        ExchangeManager.getExchangeManager().exchange(tag.hashCode(), destAO, srcArray, srcOffset, dstArray,
                dstOffset, len);
    }
}
