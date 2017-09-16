/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.sample;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Sample: Plug-in that prints and HTTP post macros
 * @author Katsuhisa Maruyama
 */
public class Activator extends AbstractUIPlugin {
    
    public static final String PLUGIN_ID = "org.jtool.macrorecorder.sample";
    
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
