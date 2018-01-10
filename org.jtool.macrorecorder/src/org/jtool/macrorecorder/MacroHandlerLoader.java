/*
 *  Copyright 2017-2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.jtool.macrorecorder.recorder.IMacroHandler;

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
    static final String ELEMENT_NAME = "handler";
    
    /**
     * The attribute name that specifies a macro handler class to be loaded.
     */
    static final String ATTRIBUTE_CALSS = "class";
    
    /**
     * The attribute name that specifies commandId of a macro handler to be loaded.
     */
    static final String ATTRIBUTE_COMMAND_ID = "commandId";
    
    /**
     * Loads macro handlers that are specified in the extension point.
     */
    public static Set<IMacroHandler> load() {
        Set<IMacroHandler> handlers = new HashSet<IMacroHandler>();
        
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(EXTENSION_POINT_ID);
        if (point == null) {
            return handlers;
        }
        
        IExtension[] extensions = point.getExtensions();
        for (IExtension extension : extensions) {
            IConfigurationElement[] elems = extension.getConfigurationElements();
            for (IConfigurationElement elem : elems) {
                if (elem.getName().equals(ELEMENT_NAME)) {
                    try {
                        Object obj = elem.createExecutableExtension(ATTRIBUTE_CALSS);
                        if (obj instanceof IMacroHandler) {
                            IMacroHandler handler = (IMacroHandler)obj;
                            if (handler.recordingAllowed()) {
                                handlers.add(handler);
                            }
                        }
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return handlers;
    }
}
