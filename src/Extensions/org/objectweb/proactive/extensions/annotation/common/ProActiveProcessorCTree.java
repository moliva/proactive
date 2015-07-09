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
package org.objectweb.proactive.extensions.annotation.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extensions.annotation.Migratable;
import org.objectweb.proactive.extensions.annotation.MigrationSignal;
import org.objectweb.proactive.extensions.annotation.NodeAttachmentCallback;
import org.objectweb.proactive.extensions.annotation.OnArrival;
import org.objectweb.proactive.extensions.annotation.OnDeparture;
import org.objectweb.proactive.extensions.annotation.RemoteObject;
import org.objectweb.proactive.extensions.annotation.VirtualNodeIsReadyCallback;
import org.objectweb.proactive.extensions.annotation.activeobject.ActiveObjectVisitorCTree;
import org.objectweb.proactive.extensions.annotation.callbacks.isready.VirtualNodeIsReadyCallbackVisitorCTree;
import org.objectweb.proactive.extensions.annotation.callbacks.nodeattachment.NodeAttachmentCallbackVisitorCTree;
import org.objectweb.proactive.extensions.annotation.migratable.MigratableVisitorCTree;
import org.objectweb.proactive.extensions.annotation.migration.signal.MigrationSignalVisitorCTree;
import org.objectweb.proactive.extensions.annotation.migration.strategy.OnArrivalVisitorCTree;
import org.objectweb.proactive.extensions.annotation.migration.strategy.OnDepartureVisitorCTree;
import org.objectweb.proactive.extensions.annotation.remoteobject.RemoteObjectVisitorCTree;

import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;


/**
 * This class implements a Processor for annotations, according to the
 * Pluggable Annotation Processing API(jsr269) specification.
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
//cannot use ${Annotation}.class.getName() the value must be a constant expression BLEAH!
@SupportedAnnotationTypes( { "org.objectweb.proactive.extensions.annotation.*"
/*"org.objectweb.proactive.extensions.annotation.activeobject.ActiveObject",
 "org.objectweb.proactive.extensions.annotation.remoteobject.RemoteObject",
 "org.objectweb.proactive.extensions.annotation.migration.signal.MigrationSignal",
 "org.objectweb.proactive.extensions.annotation.migration.strategy.OnDeparture",
 "org.objectweb.proactive.extensions.annotation.migration.strategy.OnArrival",
 "org.objectweb.proactive.extensions.annotation.callbacks.isready.VirtualNodeIsReadyCallback",
 "org.objectweb.proactive.extensions.annotation.callbacks.nodeattachment.NodeAttachmentCallback"*/
})
@SupportedOptions("enableTypeGenerationInEditor")
public class ProActiveProcessorCTree extends AbstractProcessor {

    private Trees trees;
    private Messager messager;
    private Map<String, TreePathScanner<Void, Trees>> scanners = new HashMap<String, TreePathScanner<Void, Trees>>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
        messager = processingEnv.getMessager();

        // populate the map of visitors
        populateAVMap(processingEnv);

    }

    private void populateAVMap(ProcessingEnvironment processingEnv) {
        scanners.put(ActiveObject.class.getName(), new ActiveObjectVisitorCTree(processingEnv));
        scanners.put(RemoteObject.class.getName(), new RemoteObjectVisitorCTree(processingEnv));
        scanners.put(MigrationSignal.class.getName(), new MigrationSignalVisitorCTree(processingEnv));
        scanners.put(OnDeparture.class.getName(), new OnDepartureVisitorCTree(processingEnv));
        scanners.put(OnArrival.class.getName(), new OnArrivalVisitorCTree(processingEnv));
        scanners.put(VirtualNodeIsReadyCallback.class.getName(), new VirtualNodeIsReadyCallbackVisitorCTree(
            processingEnv));
        scanners.put(NodeAttachmentCallback.class.getName(), new NodeAttachmentCallbackVisitorCTree(
            processingEnv));
        scanners.put(Migratable.class.getName(), new MigratableVisitorCTree(processingEnv));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.isEmpty()) {
            // called with no annotations
            return true;
        }

        for (TypeElement annotation : annotations) {

            TreePathScanner<Void, Trees> scanner = scanners.get(annotation.getQualifiedName().toString());
            if (scanner == null) {
                // annotation is not intended to be used for code checking
                continue;
            }

            // check whether the annotation is used in correct place and perform the verification
            // of target element using appropriate scanner
            Target target = annotation.getAnnotation(Target.class);
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : annotatedElements) {

                if (target == null) {
                    // annotation can be used everywhere
                } else {

                    boolean usedInCorrectPlace = false;

                    for (ElementType type : target.value()) {
                        if (UtilsCTree.convertToElementType(element.getKind()).equals(type)) {
                            usedInCorrectPlace = true;
                        }
                    }

                    if (!usedInCorrectPlace) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "The @" + annotation.getSimpleName() +
                            " annotation is declared to be used with " + target.toString());
                        continue;
                    }
                }

                scanner.scan(trees.getPath(element), trees);
            }
        }

        return true;
    }

}
