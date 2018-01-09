/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

/**
 * An interface for recording change macros that were performed on Eclipse.
 * @author Katsuhisa Maruyama
 */
public interface IMacroRecorder {
    
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
    
    /**
     * Sets characters that delimit recorded document change macros.
     * @param delimiters string that contains delimiter characters
     */
    public void setDelimiters(String delimiters);
    
    /**
     * Sets a compressor that compresses change macros.
     * @param compressor the compressor
     */
    public void setMacroCompressor(IMacroCompressor compressor);
}
