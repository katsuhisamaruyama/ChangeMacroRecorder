/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

/**
 * Records change macros that were performed on Eclipse.
 * @author Katsuhisa Maruyama
 */
public interface IMacroRecorder {
    
    /**
     * Returns the compressor that compresses change macros.
     * @return the macro compressor
     */
    public IMacroCompressor getMacroCompressor();
    
    /**
     * Sets a compressor that compresses change macros.
     * @param compressor the compressor
     */
    public void setMacroCompressor(MacroCompressor compressor);
    
    /**
     * Starts the recording of change macros.
     */
    public void start();
    
    /**
     * Stops the recording of change macros.
     */
    public void stop();
    
    /**
     * Tests if this macro recorder is running.
     * @return <code>true</code> if this macro recorder is running, otherwise <code>false</code>
     */
    public boolean isRunning();
    
    /**
     * Adds a listener that receives a change macro event.
     * @param listener the event listener to be added
     */
    public void addMacroListener(IMacroListener listener);
    
    /**
     * Removes a listener that receives a change macro event.
     * @param listener the event listener to be removed
     */
    public void removeMacroListener(IMacroListener listener);
}
