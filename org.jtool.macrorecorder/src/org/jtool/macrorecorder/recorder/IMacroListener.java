/*
 *  Copyright 2016
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

/**
 * Defines a listener interface for receiving a macro event.
 * @author Katsuhisa Maruyama
 */
public interface IMacroListener {
    
    /**
     * Receives a macro event when a new macro is added.
     * @param evt the macro event
     */
    public void macroAdded(MacroEvent evt);
    
    /**
     * Receives a macro event when a new raw macro is added.
     * @param evt the raw macro event
     */
    public void rawMacroAdded(MacroEvent evt);
}
