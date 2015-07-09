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
package org.objectweb.proactive.core.xml.io;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


/**
 *
 * Implement an XLMReader based on SAX reading from a stream
 *
 * @author The ProActive Team
 * @version      0.91
 *
 */
public class StreamReader implements XMLReader {
    static Logger logger = ProActiveLogger.getLogger(Loggers.XML);
    private org.xml.sax.XMLReader parser;
    private org.xml.sax.InputSource inputSource;

    public StreamReader(java.io.InputStream in, XMLHandler xmlHandler) throws java.io.IOException {
        this(new org.xml.sax.InputSource(in), xmlHandler);
    }

    public StreamReader(java.io.Reader reader, XMLHandler xmlHandler) throws java.io.IOException {
        this(new org.xml.sax.InputSource(reader), xmlHandler);
    }

    public StreamReader(org.xml.sax.InputSource inputSource, XMLHandler xmlHandler)
            throws java.io.IOException {
        this(inputSource, xmlHandler, null, null);
    }

    public StreamReader(org.xml.sax.InputSource inputSource, XMLHandler xmlHandler, String[] schemas,
            org.xml.sax.ErrorHandler errorHandler) throws java.io.IOException {
        this.inputSource = inputSource;

        DefaultHandlerAdapter adaptor = new DefaultHandlerAdapter(xmlHandler);

        //    	javax.xml.parsers.SAXParserFactory factory = javax.xml.parsers.SAXParserFactory.newInstance();
        //    	parser = factory.newSAXParser().getXMLReader();
        parser = new SAXParser();
        //parser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
        parser.setContentHandler(adaptor);

        if ((schemas != null) || (errorHandler != null)) {
            try {
                parser.setErrorHandler((errorHandler == null) ? new SAXParserErrorHandler() : errorHandler);

                if (schemas != null) {
                    parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", schemas);

                    ///    File f = new FileInputStream( ProActive.class.getResourceAsStream("/DescriptorSchema.xsd"));
                }

                parser.setFeature("http://xml.org/sax/features/validation", true);
                parser.setFeature("http://apache.org/xml/features/validation/schema", true);

                //            parser.parse(inputSource);
            } catch (SAXNotRecognizedException e) {
                logger.error("unrecognised feature: ");
                logger.error("http://xml.org/sax/features/validation");
            } catch (SAXNotSupportedException e) {
                logger.error("unrecognised feature: ");
                logger.error("http://apache.org/xml/features/validation/schema");
            }
        }
    }

    // -- implements XMLReader ------------------------------------------------------
    public void read() throws SAXException, IOException {
        //parser.setFeature("http://xml.org/sax/features/validation",true);
        //parser.setFeature("http://apache.org/xml/features/validation/schema",true);
        parser.parse(inputSource);
    }
}
