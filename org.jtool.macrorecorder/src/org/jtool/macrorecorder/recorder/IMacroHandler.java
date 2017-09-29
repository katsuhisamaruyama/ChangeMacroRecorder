/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

/**
 * An interface for handling received change macro event.
 * @author Katsuhisa Maruyama
 */
public interface IMacroHandler extends IMacroListener {
    
    /**
     * Tests if the macro recording is allowed.
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
}
