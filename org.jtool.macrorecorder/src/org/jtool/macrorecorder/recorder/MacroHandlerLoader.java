/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import java.util.Set;
import java.util.HashSet;

/**
 * Loads extensions for a macro hander.
 * @author Katsuhisa Maruyama
 */
public class MacroHandlerLoader {
    
    /**
     * The ID of the extension point.
     */
    public static final String EXTENSION_POINT_ID = "org.jtool.macrorecorder.handlers";
    
    /**
     * The element name that specifies the collection of macro handlers.
     */
    private static final String ELEMENT_NAME = "handler";
    
    /**
     * The attribute name that specifies the a macro handler to be loaded.
     */
    private static final String ATTRIBUTE_CALSS = "class";
    
    /**
     * Loads macro handlers that are specified in the extension point.
     */
    static Set<IMacroListener> load() {
        Set<IMacroListener> handlers = new HashSet<IMacroListener>();
        
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(EXTENSION_POINT_ID);
        if (point == null) {
            return handlers;
        }
        
        IExtension[] extensions = point.getExtensions();
        if (extensions.length > 0) {
            IExtension ex = extensions[0];
            IConfigurationElement[] elems = ex.getConfigurationElements();
            for (IConfigurationElement elem : elems) {
                if (elem.getName().equals(ELEMENT_NAME)) {
                    try {
                        Object obj = elem.createExecutableExtension(ATTRIBUTE_CALSS);
                        if (obj instanceof IMacroListener) {
                            IMacroListener handler = (IMacroListener)obj;
                            if (handler.recordingAllowed()) {
                                handlers.add(handler);
                            }
                        }
                    } catch (CoreException e) { /* empty */ }
                }
            }
        }
        return handlers;
    }
}
