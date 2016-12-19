/*
 *  Copyright 2016
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

/**
 * The activator class controls the plug-in life cycle.
 * @author Katsuhisa Maruyama
 */
public class Activator extends AbstractUIPlugin {
    
    /**
     * The plug-in ID.
     */
    public static final String PLUGIN_ID = "MacroRecorder";
    
    /**
     * The plug-in instance.
     */
    private static Activator plugin;
    
    /**
     * Creates a plug-in instance.
     */
    public Activator() {
    }
    
    /**
     * Performs actions when the plug-in is activated.
     * @param context the bundle context for this plug-in
     * @throws Exception if this plug-in did not start up properly
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        System.out.println(PLUGIN_ID + " activated.");
    }
    
    /**
     * Performs actions when when the plug-in is shut down.
     * @param context the bundle context for this plug-in
     * @throws Exception if this this plug-in fails to stop
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
        
        System.out.println(PLUGIN_ID + " deactivated.");
    }
    
    /**
     * Returns the default plug-in instance.
     * @return the default plug-in instance
     */
    public static Activator getPlugin() {
        return plugin;
    }
    
    /**
     * Obtains a progress monitor dialog for the workbench window.
     * @return the progress monitor dialog
     */
    public static ProgressMonitorDialog getProgressMonitorDialog() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        return new ProgressMonitorDialog(window.getShell());
    }
}
