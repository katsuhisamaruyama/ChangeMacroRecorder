/*
 *  Copyright 2016
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

/**
 * Records macros that were performed on Eclipse.
 * @author Katsuhisa Maruyama
 */
public interface IMacroRecorder {
    
    /**
     * Returns the compressor that compresses macros.
     * @return the macro compressor
     */
    public IMacroCompressor getMacroCompressor();
    
    /**
     * Sets a compressor that compresses macros.
     * @param compressor the compressor
     */
    public void setMacroCompressor(IMacroCompressor compressor);
    
    /**
     * Starts the recording of document macros performed on an editor.
     */
    public void start();
    
    /**
     * Stops the recording of menu and document macros.
     */
    public void stop();
    
    /**
     * Adds a listener that receives a macro event.
     * @param listener the event listener to be added
     */
    public void addMacroListener(IMacroListener listener);
    
    /**
     * Removes a listener that receives a macro event.
     * @param listener the event listener to be removed
     */
    public void removeMacroListener(IMacroListener listener);
}
