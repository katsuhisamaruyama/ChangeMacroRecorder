/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.sample;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Sample: Plug-in that prints and HTTP post macros
 * @author Katsuhisa Maruyama
 */
public class Activator extends AbstractUIPlugin implements IStartup {
    
    public static final String PLUGIN_ID = "org.jtool.macrorecorder.sample";
    
    private static Activator plugin;
    
    private SampleMacroPrint sampleMacroPrint;
    
    private SampleMacroPost sampleMacroPost;
    
    public Activator() {
    }
    
    @Override
    public void earlyStartup() {
    }
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        sampleMacroPrint = new SampleMacroPrint();
        sampleMacroPrint.start();
        
        sampleMacroPost = new SampleMacroPost();
        sampleMacroPost.start();
    }
    
    @Override
    public void stop(BundleContext context) throws Exception {
        if (sampleMacroPrint != null) {
            sampleMacroPrint.stop();
        }
        if (sampleMacroPost != null) {
            sampleMacroPost.stop();
        }
        
        super.stop(context);
        plugin = null;
    }
    
    public static Activator getPlugin() {
        return plugin;
    }
}
