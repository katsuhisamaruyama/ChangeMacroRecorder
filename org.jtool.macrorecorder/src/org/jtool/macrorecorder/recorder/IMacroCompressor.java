/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.jtool.macrorecorder.macro.DocumentMacro;

/**
 * An interface for compressor of change macros.
 * @author Katsuhisa Maruyama
 */
public interface IMacroCompressor {
    
    /**
     * Sets characters that delimit recorded document change macros.
     * @param chars characters representing delimiters
     */
    public void setDelimiter(char[] chars);
    
    /**
     * Tests if a document change macros can be combined with its previous document change macro.
     * @param macro the document macro
     * @return <code>true</code> if the macros can be combined, otherwise <code>false</code>
     */
    public boolean canCombine(DocumentMacro macro);
    
    /**
     * Combines successive two document change macros.
     * @param last the former document change macro 
     * @param next the latter document change macro
     * @return the combined document change macro, or <code>null</code> if the macro cannot be combined
     */
    public DocumentMacro combine(DocumentMacro last, DocumentMacro next);
}
