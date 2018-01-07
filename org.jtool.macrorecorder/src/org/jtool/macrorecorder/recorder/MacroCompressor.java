/*
 *  Copyright 2016-2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.jtool.macrorecorder.macro.DocumentMacro;

/**
 * Compresses macros.
 * @author Katsuhisa Maruyama
 */
public class MacroCompressor implements IMacroCompressor {
    
    /**
     * Defines the default delimiters
     */
    protected final String DEFAULT_DELIMITERS = "\n\r ,.;()[]{}";
    
    /**
     * The collection of characters that represent delimiters.
     */
    protected char[] delimiterChars;
    
    /**
     * Creates an object compressing macros.
     */
    protected MacroCompressor() {
        setDelimiter(DEFAULT_DELIMITERS);
    }
    
    /**
     * Sets characters that delimit recorded document change macros.
     * @param chars characters representing delimiters
     */
    protected void setDelimiter(char[] chars) {
        delimiterChars = chars;
    }
    
    /**
     * Sets characters that delimit recorded document change macros.
     * @param delimiters string that contains delimiter characters
     */
    protected void setDelimiter(String delimiters) {
        delimiterChars = new char[delimiters.length()];
        for (int i = 0; i < delimiters.length(); i++) {
            delimiterChars[i] = delimiters.charAt(i);
        }
    }
    
    /**
     * Tests if a document change macros can be combined with its previous document change macro.
     * @param macro the document macro
     * @return <code>true</code> if the macros can be combined, otherwise <code>false</code>
     */
    @Override
    public boolean canCombine(DocumentMacro macro) {
        if (macro == null) {
            return false;
        }
        
        if (macro.inserted()) {
            return combineWith(macro.getInsertedText());
        }
        
        if (macro.deleted()) {
            return combineWith(macro.getDeletedText());
        }
        
        if (macro.replaced()) {
            return combineWith(macro.getInsertedText()) && combineWith(macro.getDeletedText());
        }
        
        return false;
    }
    
    /**
     * Combines successive two document change macros.
     * @param last the former document change macro 
     * @param next the latter document change macro
     * @return the combined document change macro, or <code>null</code> if the macro cannot be combined
     */
    @Override
    public DocumentMacro combine(DocumentMacro last, DocumentMacro next) {
        if (next == null) {
            return null;
        }
        
        if (next.inserted()) {
            return combineInsertMacro(last, next);
        }
        
        if (next.deleted()) {
            return combineDeleteMacro(last, next);
        }
        
        if (next.replaced()) {
            return compressReplaceMacro(last, next);
        }
        
        return null;
    }
    
    /**
     * Combines successive two document change macro.
     * @param last the former document change macro
     * @param next the latter document change macro that represents the insertion
     * @return the combined document change macro, or <code>null</code> if the macros cannot be combined
     */
    protected DocumentMacro combineInsertMacro(DocumentMacro last, DocumentMacro next) {
        if (last == null) {
            return next;
        }
        
        if (!last.inserted() || !combineWith(last.getInsertedText())) {
            return null;
        }
        
        if (last.getStart() + last.getInsertedText().length() == next.getStart()) {
            String text = last.getInsertedText() + next.getInsertedText();
            return new DocumentMacro(last.getTime(), last.getAction(),
                       last.getMacroPath(), last.getStart(), text, ""); 
        }
        
        return null;
    }
    
    /**
     * Combines successive two document change macros.
     * @param last the former document change macro
     * @param next the latter document change macro that represents deletion
     * @return the combined document change macro, or <code>null</code> if the macros cannot be combined
     */
    protected DocumentMacro combineDeleteMacro(DocumentMacro last, DocumentMacro next) {
        if (last == null) {
            return next;
        }
        
        if (!last.deleted() || !combineWith(last.getDeletedText())) {
            return null;
        }
        
        if (last.getStart() > next.getStart()) {
            if (last.getStart() == next.getStart() + next.getDeletedText().length()) {
                String text = next.getDeletedText() + last.getDeletedText();
                
                return new DocumentMacro(last.getTime(), last.getAction(),
                           last.getMacroPath(), next.getStart(), "", text); 
            }
            
        } else {
            if (last.getStart() + last.getInsertedText().length() == next.getStart()) {
                String text = last.getDeletedText() + next.getDeletedText();
                
                return new DocumentMacro(last.getTime(), last.getAction(),
                           last.getMacroPath(), last.getStart(), "", text); 
            }
        }
        
        return null;
    }
    
    /**
     * Compresses successive two document change macros.
     * @param last the former document change macro
     * @param next the latter document change macro that represents replacement
     * @return the combined document change macro, or <code>null</code> if the macros cannot be combined
     */
    protected DocumentMacro compressReplaceMacro(DocumentMacro last, DocumentMacro next) {
        if (last == null) {
            return next;
        }
        
        if (!(last.inserted() || last.replaced()) ||
            !combineWith(last.getInsertedText()) || !combineWith(last.getDeletedText())) {
            return null;
        }
        
        if (last.getStart() == next.getStart() &&
            last.getInsertedText().equals(next.getDeletedText())) {
            String itext = next.getInsertedText();
            String dtext = last.getDeletedText();
            return new DocumentMacro(last.getTime(), last.getAction(),
                       last.getMacroPath(), last.getStart(), itext, dtext); 
        }
        
        return null;
    }
    
    /**
     * Tests if a given text can be combined with another one.
     * @param text the text to be combined
     * @return <code>true</code> if the can be combined or no delimiter is specified, otherwise <code>false</code>
     */
    private boolean combineWith(String text) {
        if (delimiterChars == null) {
            return false;
        }
        
        if (delimiterChars.length == 0) {
            return true;
        }
        
        for (int i = 0; i < delimiterChars.length; i++) {
            if (text.indexOf(delimiterChars[i]) >= 0) {
                return false;
            }
        }
        return true;
    }
}
