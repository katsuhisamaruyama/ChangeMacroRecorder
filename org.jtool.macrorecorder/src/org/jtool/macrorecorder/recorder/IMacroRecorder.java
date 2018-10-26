/*
 *  Copyright 2016-2018
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
     * @param a listener that receives a change macro event
     * @param delimiters string that contains delimiter characters
     * @return <code>true</code> if the delimiters are attached to a combinator corresponding to the listener, otherwise <code>false</code>
     */
    public boolean setDelimiters(IMacroListener listener, String delimiters);
    
    /**
     * Sets a combinator that combines document macros.
     * @param a listener that receives a change macro event
     * @param combinator the combinator
     * @return <code>true</code> if the combinator is attached to the listener, otherwise <code>false</code>
     */
    public boolean setDocMacroCombinator(IMacroListener listener, IDocMacroCombinator combinator);
}
