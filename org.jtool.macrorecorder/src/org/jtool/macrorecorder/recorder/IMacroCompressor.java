/*
 *  Copyright 2016
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.jtool.macrorecorder.macro.DocumentMacro;

/**
 * An interface for compressor of macros.
 * @author Katsuhisa Maruyama
 */
public interface IMacroCompressor {
    
    /**
     * Sets characters that delimit recorded macros.
     * @param chars characters representing delimiters
     */
    public void setDelimiter(char[] chars);
    
    /**
     * Tests if a document macros can be combined with its previous macro.
     * @param macro the document macro
     * @return <code>true</code> if the macros can be combined, otherwise <code>false</code>
     */
    public boolean canCombine(DocumentMacro macro);
    
    /**
     * Combines successive two document macros.
     * @param last the former document macro 
     * @param next the latter document macro
     * @return the combined macro, or <code>null</code> if the macro cannot be combined
     */
    public DocumentMacro combine(DocumentMacro last, DocumentMacro next);
}
