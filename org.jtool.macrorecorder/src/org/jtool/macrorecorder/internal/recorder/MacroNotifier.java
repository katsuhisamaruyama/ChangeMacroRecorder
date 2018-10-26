/*
 *  Copyright 2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.DocumentMacro;
import org.jtool.macrorecorder.recorder.IDocMacroCombinator;
import org.jtool.macrorecorder.recorder.IMacroListener;

/**
 * A notifier that sends change macros to each listener.
 * @author Katsuhisa Maruyama
 */
public class MacroNotifier {
    
    /**
     * A listener that receives macro events.
     */
    private IMacroListener macroListener;
    
    /**
     * A combinator that combines document macros.
     */
    private IDocMacroCombinator docMacroCombinator;
    
    /**
     * The last document macro stored for char concatenation.
     */
    private DocumentMacro lastDocumentMacro;
    
    /**
     * Creates an agent that records macros for each listener.
     * @param listener a listener that receives macro events
     * @param combinator a processor that processes document macros
     */
    public MacroNotifier(IMacroListener listener, IDocMacroCombinator combinator) {
        macroListener = listener;
        docMacroCombinator = combinator;
    }
    
    /**
     * Returns the listener that receives macro events.
     * @return the listener
     */
    public IMacroListener getMacroListener() {
        return macroListener;
    }
    
    /**
     * Sets a combinator that combines document macros.
     * @param combinator the combinator
     */
    public void setDocMacroCombinator(IDocMacroCombinator combinator) {
        docMacroCombinator = combinator;
    }
    
    /**
     * Returns the combinator that combines document macros.
     * @return the combinator
     */
    public IDocMacroCombinator getDocMacroCombinator() {
        return docMacroCombinator;
    }
    
    /**
     * Sets the last document macro for char concatenation.
     * @param macro the last document macro
     */
    void setLastDocumentMacro(DocumentMacro macro) {
        lastDocumentMacro = macro;
    }
    
    /**
     * Returns the last document macro for char concatenation.
     * @return the last document macro
     */
    DocumentMacro getLastDocumentMacro() {
        return lastDocumentMacro;
    }
}
