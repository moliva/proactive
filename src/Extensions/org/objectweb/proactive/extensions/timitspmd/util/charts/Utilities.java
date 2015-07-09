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
package org.objectweb.proactive.extensions.timitspmd.util.charts;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jdom.Element;
import org.jfree.chart.JFreeChart;
import org.objectweb.proactive.extensions.timitspmd.TimIt;
import org.objectweb.proactive.extensions.timitspmd.config.ConfigChart;
import org.objectweb.proactive.extensions.timitspmd.util.BenchmarkStatistics;
import org.w3c.dom.DOMImplementation;


/**
 * Give some utilities to manage charts
 *
 * @author The ProActive Team
 */
public class Utilities {

    /**
     * For each tag <chart> in configuration file, try to generate a chart
     * according to its parameters
     *
     * @param serieResults
     *            merged values
     * @param bstats
     *            full statistics (used for non-mergable values like commevents)
     * @param charts
     *            array from <chart> tags in configuration file
     */
    public static void generatingCharts(Element eTimitResult, BenchmarkStatistics bstats, ConfigChart[] charts) {
        if (charts == null) {
            return;
        }
        TimIt.message(2, "Generating charts...");
        String className = null;

        for (ConfigChart cChart : charts) {
            TimIt.message(4, "Generating " + cChart.get("title") + " [" + cChart.get("subtitle") + "]" +
                "...");
            try {
                className = Utilities.class.getPackage().getName() + "." + cChart.get("type");
                Class<?> chartClass = Class.forName(className);
                Chart chart = (Chart) chartClass.newInstance();
                chart.generateChart(eTimitResult, bstats, cChart);
            } catch (ClassNotFoundException e) {
                System.err.println("  Fail: Unknown chart type: " + className);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Exports a JFreeChart to a SVG file.
     *
     * @param chart
     *            JFreeChart to export
     * @param bounds
     *            the dimensions of the viewport
     * @param svgFile
     *            the output file.
     * @throws IOException
     *             if writing the svgFile fails.
     */
    public static void saveChartAsSVG(JFreeChart chart, Rectangle bounds, File svgFile) throws IOException {
        try {
            Class<?> GDI = Class.forName("org.apache.batik.dom.GenericDOMImplementation");

            // Get a DOMImplementation and create an XML document
            Method getDOMImplementation = GDI.getMethod("getDOMImplementation", new Class<?>[0]);
            DOMImplementation domImpl = (DOMImplementation) getDOMImplementation.invoke(null, new Object[0]);

            org.w3c.dom.Document document = domImpl.createDocument(null, "svg", null);

            // Create an instance of the SVG Generator
            Class<?> SG2D = Class.forName("org.apache.batik.svggen.SVGGraphics2D");
            Method streamMethod = SG2D.getMethod("stream", new Class<?>[] { Writer.class, boolean.class });
            Constructor<?> SG2DConstr = SG2D.getConstructor(new Class<?>[] { org.w3c.dom.Document.class });
            Object svgGenerator = SG2DConstr.newInstance(document);

            // draw the chart in the SVG generator
            chart.draw((Graphics2D) svgGenerator, bounds);

            // Write svg file
            OutputStream outputStream = new FileOutputStream(svgFile);
            Writer out = new OutputStreamWriter(outputStream, "UTF-8");
            streamMethod.invoke(svgGenerator, new Object[] { out, true /* use css */});
            outputStream.flush();
            outputStream.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
