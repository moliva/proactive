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
package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.objectweb.proactive.core.body.ProActiveMetaObjectFactory;
import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.ProActiveSecurityDescriptorHandler;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.security.SecurityConstants.EntityType;
import org.objectweb.proactive.core.security.exceptions.InvalidPolicyFile;
import org.objectweb.proactive.extensions.dataspaces.core.InputOutputSpaceConfiguration;
import org.objectweb.proactive.extensions.dataspaces.core.SpaceType;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extensions.gcmdeployment.PathElement;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationParser;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.TechnicalServicesProperties;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ApplicationParserProactive extends AbstractApplicationParser {
    private static final String XPATH_JAVA = "app:java";
    private static final String XPATH_JVMARG = "app:jvmarg";
    private static final String XPATH_CONFIGURATION = "app:configuration";
    private static final String XPATH_PROACTIVE_CLASSPATH = "app:proactiveClasspath";
    private static final String XPATH_APPLICATION_CLASSPATH = "app:applicationClasspath";
    private static final String XPATH_SECURITY_POLICY = "app:securityPolicy";
    private static final String XPATH_PROACTIVE_SECURITY = "app:proactiveSecurity";
    private static final String XPATH_APPLICATION_POLICY = "app:applicationPolicy";
    private static final String XPATH_RUNTIME_POLICY = "app:runtimePolicy";
    private static final String XPATH_LOG4J_PROPERTIES = "app:log4jProperties";
    private static final String XPATH_USER_PROPERTIES = "app:userProperties";
    private static final String XPATH_DEBUG_PROPERTIES = "app:debug";
    private static final String XPATH_DATA = "app:data";
    private static final String XPATH_NAMING_SERVICE = "app:namingService";
    private static final String XPATH_INPUT_DEFAULT = "app:inputDefault";
    private static final String XPATH_INPUT = "app:input";
    private static final String XPATH_OUTPUT_DEFAULT = "app:outputDefault";
    private static final String XPATH_OUTPUT = "app:output";
    protected static final String NODE_NAME = "proactive";
    protected TechnicalServicesProperties appTechnicalServicesProperties;
    protected ProActiveSecurityManager proactiveApplicationSecurityManager;
    protected Set<InputOutputSpaceConfiguration> spacesConfigurations;
    protected String namingServiceURL;

    @Override
    protected CommandBuilder createCommandBuilder() {
        return new CommandBuilderProActive();
    }

    public String getNodeName() {
        return NODE_NAME;
    }

    @Override
    public void parseApplicationNode(Node paNode, GCMApplicationParser applicationParser, XPath xpath)
            throws Exception {
        super.parseApplicationNode(paNode, applicationParser, xpath);

        CommandBuilderProActive commandBuilderProActive = (CommandBuilderProActive) commandBuilder;

        String relPath = GCMParserHelper.getAttributeValue(paNode, "relpath");
        String base = GCMParserHelper.getAttributeValue(paNode, "base");
        commandBuilderProActive.setProActivePath(relPath, base);

        try {

            Node techServicesNode = (Node) xpath.evaluate(XPATH_TECHNICAL_SERVICES, paNode,
                    XPathConstants.NODE);
            if (techServicesNode != null) {
                appTechnicalServicesProperties = GCMParserHelper.parseTechnicalServicesNode(xpath,
                        techServicesNode);
            } else {
                appTechnicalServicesProperties = new TechnicalServicesProperties();
            }

            // parse configuration
            //
            Node configNode = (Node) xpath.evaluate(XPATH_CONFIGURATION, paNode, XPathConstants.NODE);

            if (configNode != null) {
                parseProActiveConfiguration(xpath, commandBuilderProActive, configNode);
                applicationParser.setProactiveApplicationSecurityManager(proactiveApplicationSecurityManager);

            }

            // Optional: data (Data Spaces) node
            final Node dataNode = (Node) xpath.evaluate(XPATH_DATA, paNode, XPathConstants.NODE);
            if (dataNode != null) {
                parseDataSpaces(xpath, dataNode);
                // See FIXME in GCMA - we cannot safely add DataSpacesTechnicalService as an app-wide TS.
                // In current implementation local nodes may be shared, so we cannot safely apply it locally.
                // addDataSpacesTechnicalService();
                applicationParser.setDataSpacesEnabled(true);
                applicationParser.setDataSpacesNamingServiceURL(namingServiceURL);
                applicationParser.setInputOutputSpacesConfigurations(spacesConfigurations);
            }

            commandBuilderProActive.setVirtualNodes(applicationParser.getVirtualNodes());

        } catch (XPathExpressionException e) {
            GCMDeploymentLoggers.GCMA_LOGGER.fatal(e.getMessage(), e);
        }
    }

    protected void parseProActiveConfiguration(XPath xpath, CommandBuilderProActive commandBuilderProActive,
            Node configNode) throws XPathExpressionException {
        // Optional: java
        Node javaNode = (Node) xpath.evaluate(XPATH_JAVA, configNode, XPathConstants.NODE);
        if (javaNode != null) {
            PathElement pe = GCMParserHelper.parsePathElementNode(javaNode);
            commandBuilderProActive.setJavaPath(pe);
        }

        Node classPathNode;
        // Optional: proactiveClasspath
        classPathNode = (Node) xpath.evaluate(XPATH_PROACTIVE_CLASSPATH, configNode, XPathConstants.NODE);
        if (classPathNode != null) {
            String type = GCMParserHelper.getAttributeValue(classPathNode, "type");
            List<PathElement> proactiveClassPath = GCMParserHelper.parseClasspath(xpath, classPathNode);
            commandBuilderProActive.setProActiveClasspath(proactiveClassPath);
            if ("overwrite".equals(type)) {
                commandBuilderProActive.setOverwriteClasspath(true);
            } else {
                commandBuilderProActive.setOverwriteClasspath(false);
            }
        }

        // Optional: applicationClasspath
        classPathNode = (Node) xpath.evaluate(XPATH_APPLICATION_CLASSPATH, configNode, XPathConstants.NODE);
        if (classPathNode != null) {
            List<PathElement> applicationClassPath = GCMParserHelper.parseClasspath(xpath, classPathNode);
            commandBuilderProActive.setApplicationClasspath(applicationClassPath);
        }

        // Optional: security policy
        Node securityPolicyNode = (Node) xpath.evaluate(XPATH_SECURITY_POLICY, configNode,
                XPathConstants.NODE);
        if (securityPolicyNode != null) {
            PathElement pathElement = GCMParserHelper.parsePathElementNode(securityPolicyNode);
            commandBuilderProActive.setSecurityPolicy(pathElement);
        }

        Node applicationSecurityPolicyNode = (Node) xpath.evaluate(XPATH_PROACTIVE_SECURITY + "/" +
            XPATH_APPLICATION_POLICY, configNode, XPathConstants.NODE);
        if (applicationSecurityPolicyNode != null) {
            PathElement pathElement = GCMParserHelper.parsePathElementNode(applicationSecurityPolicyNode);
            commandBuilderProActive.setApplicationPolicy(pathElement);

            /** security rules */

            PolicyServer policyServer;

            try {

                System.out.println("CommandBuilderProActive.setApplicationPolicy()" +
                    pathElement.getRelPath());

                policyServer = ProActiveSecurityDescriptorHandler
                        .createPolicyServer(pathElement.getRelPath());

                proactiveApplicationSecurityManager = new ProActiveSecurityManager(EntityType.APPLICATION,
                    policyServer);

                System.out.println("ApplicationParserProactive.parseProActiveConfiguration()" +
                    proactiveApplicationSecurityManager);

                // set the security policyserver to the default proactive meta object
                // by the way, the HalfBody will be associated to a security manager
                // derivated from this one.
                ProActiveSecurityManager psm = proactiveApplicationSecurityManager
                        .generateSiblingCertificate(EntityType.OBJECT, "HalfBody");
                ProActiveMetaObjectFactory.newInstance().setProActiveSecurityManager(psm);

            } catch (InvalidPolicyFile e) {
                e.printStackTrace();
            }

        }

        Node runtimeSecurityPolicyNode = (Node) xpath.evaluate(XPATH_PROACTIVE_SECURITY + "/" +
            XPATH_RUNTIME_POLICY, configNode, XPathConstants.NODE);
        if (runtimeSecurityPolicyNode != null) {
            PathElement pathElement = GCMParserHelper.parsePathElementNode(runtimeSecurityPolicyNode);
            commandBuilderProActive.setRuntimePolicy(pathElement);
        }

        // Optional: log4j properties
        Node log4jPropertiesNode = (Node) xpath.evaluate(XPATH_LOG4J_PROPERTIES, configNode,
                XPathConstants.NODE);
        if (log4jPropertiesNode != null) {
            PathElement pathElement = GCMParserHelper.parsePathElementNode(log4jPropertiesNode);
            commandBuilderProActive.setLog4jProperties(pathElement);
        }

        // Optional: user properties
        Node userPropertiesNode = (Node) xpath.evaluate(XPATH_USER_PROPERTIES, configNode,
                XPathConstants.NODE);
        if (userPropertiesNode != null) {
            PathElement pathElement = GCMParserHelper.parsePathElementNode(userPropertiesNode);
            commandBuilderProActive.setUserProperties(pathElement);
        }

        // Optional: jvmarg
        NodeList jvmargNodes = (NodeList) xpath.evaluate(XPATH_JVMARG, configNode, XPathConstants.NODESET);
        for (int i = 0; i < jvmargNodes.getLength(); i++) {
            String jvmarg = GCMParserHelper.getAttributeValue(jvmargNodes.item(i), "value");
            commandBuilderProActive.addJVMArg(jvmarg);
        }

        // Optional: debug mode
        Node debugNode = (Node) xpath.evaluate(XPATH_DEBUG_PROPERTIES, configNode, XPathConstants.NODE);
        if (debugNode != null) {
            String attr = GCMParserHelper.getAttributeValue(debugNode, "command");
            commandBuilderProActive.setDebugCommand(attr);
            commandBuilderProActive.enableDebug(true);
        }

    }

    protected void parseDataSpaces(XPath xpath, Node dataNode) throws XPathExpressionException {
        // Optional: Naming Service URL
        final Node namingServiceNode = (Node) xpath.evaluate(XPATH_NAMING_SERVICE, dataNode,
                XPathConstants.NODE);
        if (namingServiceNode != null) {
            namingServiceURL = GCMParserHelper.getAttributeValue(namingServiceNode, "url");
        }

        spacesConfigurations = new HashSet<InputOutputSpaceConfiguration>();
        // Optional: default input
        final Node defaultInputNode = (Node) xpath.evaluate(XPATH_INPUT_DEFAULT, dataNode,
                XPathConstants.NODE);
        parseSpaceConfigurationNode(xpath, defaultInputNode, SpaceType.INPUT);

        // Optional: inputs
        final NodeList inputsNodes = (NodeList) xpath.evaluate(XPATH_INPUT, dataNode, XPathConstants.NODESET);
        for (int i = 0; i < inputsNodes.getLength(); i++) {
            parseSpaceConfigurationNode(xpath, inputsNodes.item(i), SpaceType.INPUT);
        }

        // Optional: default output
        final Node defaultOutputNode = (Node) xpath.evaluate(XPATH_OUTPUT_DEFAULT, dataNode,
                XPathConstants.NODE);
        parseSpaceConfigurationNode(xpath, defaultOutputNode, SpaceType.OUTPUT);

        // Optional: outputs
        final NodeList outputsNodes = (NodeList) xpath.evaluate(XPATH_OUTPUT, dataNode,
                XPathConstants.NODESET);
        for (int i = 0; i < outputsNodes.getLength(); i++) {
            parseSpaceConfigurationNode(xpath, outputsNodes.item(i), SpaceType.OUTPUT);
        }
    }

    protected void parseSpaceConfigurationNode(final XPath xpath, final Node node, final SpaceType type)
            throws XPathExpressionException {
        if (node != null) {
            final InputOutputSpaceConfiguration config = GCMParserHelper.parseInputOuputSpaceConfiguration(
                    xpath, node, type);
            if (spacesConfigurations.contains(config)) {
                throw new IllegalStateException("Duplicate data space definition: " + config);
            }
            spacesConfigurations.add(config);
        }
    }

    public TechnicalServicesProperties getTechnicalServicesProperties() {
        return appTechnicalServicesProperties;
    }

}
