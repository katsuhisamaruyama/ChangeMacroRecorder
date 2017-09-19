/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

/**
 * Defines a listener interface for receiving a change macro event.
 * @author Katsuhisa Maruyama
 */
public interface IMacroListener {
    
    /**
     * Test if the macro recording is allowed.
     * This handler will be registered if <code>true</code> is returned, otherwise the handler will not be registered.
     */
    public boolean recordingAllowed();
    
    /**
     * Invoked to initialize this handler immediately before starting the macro recording.
     */
    public void initialize();
    
    /**
     * Invoked to terminate this handler immediately after stopping the macro recording.
     */
    public void terminate();
    
    /**
     * Receives an event when a new change macro is added.
     * @param evt the macro event
     */
    public void macroAdded(MacroEvent evt);
    
    /**
     * Receives an event when a new raw change macro is added.
     * @param evt the raw macro event
     */
    public void rawMacroAdded(MacroEvent evt);
}
