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
