/*
 *  Copyright 2016-2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.jtool.macrorecorder.macro.DocumentMacro;

/**
 * An interface for combining document macros before they are sent to listeners.
 * @author Katsuhisa Maruyama
 */
public interface IDocMacroCombinator {
    
    /**
     * Tests if a document macros can be combined with its previous document macro.
     * @param macro the document macro
     * @return <code>true</code> if the macros can be combined, otherwise <code>false</code>
     */
    public boolean canCombine(DocumentMacro macro);
    
    /**
     * Combines successive two document macros.
     * @param former the former document macro 
     * @param latter the latter document macro
     * @return the combined document macro, or <code>null</code> if the macro cannot be combined
     */
    public DocumentMacro combine(DocumentMacro former, DocumentMacro latter);
}
