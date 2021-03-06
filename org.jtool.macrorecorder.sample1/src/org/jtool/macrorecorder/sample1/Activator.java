/*
 *  Copyright 2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.sample1;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Sample1: Plug-in that prints change macros.
 * @author Katsuhisa Maruyama
 */
public class Activator extends AbstractUIPlugin {
    
    private static Activator plugin;
    
    public Activator() {
    }
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }
    
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }
    
    public static Activator getPlugin() {
        return plugin;
    }
}
