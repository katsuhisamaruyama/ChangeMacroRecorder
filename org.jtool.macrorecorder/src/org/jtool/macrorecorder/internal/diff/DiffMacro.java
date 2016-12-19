/*
 *  Copyright 2016
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.diff;

import org.jtool.macrorecorder.macro.DocumentMacro;

/**
 * Stores a macro that represents difference between two versions of source code.
 * @author Katsuhisa Maruyama
 */
public class DiffMacro extends DocumentMacro{
    
    /**
     * Creates an object storing information on a difference macro.
     * @param action the action of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param start the leftmost offset of the text changed by this macro
     * @param itext the contents of the text inserted by the macro
     * @param dtext the contents of the text deleted by the macro
     */
    public DiffMacro(Action action, String path, String branch, int start, String itext, String dtext) {
        super(action.toString(), path, branch, start, itext, dtext);
    }
    
    /**
     * Sets the contents of the text inserted by the macro
     * @param text the contents of the text inserted by the macro
     */
    void setInsertedText(String text) {
        insertedText = text;
    }
    
    /**
     * Sets the contents of the text deleted by the macro
     * @param text the contents of the text deleted by the macro
     */
    void setDeletedText(String text) {
        deletedText = text;
    }
    
    /**
     * Creates a document macro that represents an expected diff and returns it.
     * @param macro a diff macro
     * @return the created document macro
     */
    public static DocumentMacro getExpectedDiff(DiffMacro macro) {
        return new DocumentMacro(macro.getTime(), DocumentMacro.Action.AUTO_DIFF.toString(),
                   macro.getPath(), macro.getBranch(), macro.getStart(), macro.getInsertedText(), macro.getDeletedText());
    }
    
    /**
     * Creates a document macro that represents an unexpected diff and returns it.
     * @param macro a diff macro
     * @return the created document macro
     */
    public static DocumentMacro getUnexpectedDiff(DiffMacro macro) {
        return new DocumentMacro(macro.getTime(),DocumentMacro.Action.IRREGULAR_DIFF.toString(),
                   macro.getPath(), macro.getBranch(), macro.getStart(), macro.getInsertedText(), macro.getDeletedText());
    }
}
