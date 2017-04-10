/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;

/**
 * Stores a macro occurring a document change.
 * @author Katsuhisa Maruyama
 */
public class DocumentMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        EDIT, CUT, PASTE, UNDO, REDO, AUTO_DIFF, IRREGULAR_DIFF;
    }
    
    /**
     * The leftmost offset of the text changed by this macro.
     */
    protected int start;
    
    /**
     * The contents of the text inserted by this macro.
     */
    protected String insertedText;
    
    /**
     * The contents of the text deleted by this macro.
     */
    protected String deletedText;
    
    /**
     * Creates an object storing information about a document macro.
     * @param action the action of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch of a file on which this macro was performed
     * @param start the leftmost offset of the text changed by this macro
     * @param itext the contents of the text inserted by the macro
     * @param dtext the contents of the text deleted by the macro
     */
    public DocumentMacro(Action action, MacroPath path, String branch, int start, String itext, String dtext) {
        super(action.toString(), path, branch);
        this.start = start;
        this.insertedText = itext;
        this.deletedText = dtext;
    }
    
    /**
     * Creates an object storing information about a document macro.
     * @param type the type of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param start the leftmost offset of the text changed by this macro
     * @param itext the contents of the text inserted by the macro
     * @param dtext the contents of the text deleted by the macro
     */
    public DocumentMacro(String type, MacroPath path, String branch, int start, String itext, String dtext) {
        super(type, path, branch);
        this.start = start;
        this.insertedText = itext;
        this.deletedText = dtext;
    }
    
    /**
     * Creates an object storing information about a document macro.
     * @param type the type of this macro
     * @param path the path of a file or a package this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param start the leftmost offset of the text changed by this macro
     * @param itext the contents of the text inserted by the macro
     * @param dtext the contents of the text deleted by the macro
     */
    public DocumentMacro(ZonedDateTime time, String type, MacroPath path, String branch, int start, String itext, String dtext) {
        super(time, type, path, branch);
        this.start = start;
        this.insertedText = itext;
        this.deletedText = dtext;
    }
    
    /**
     * Returns the leftmost offset of the text changed by this macro.
     * @return the leftmost offset of the text
     */
    public int getStart() {
        return start;
    }
    
    /**
     * Returns the contents of the text inserted by this macro.
     * @return the inserted contents of the text
     */
    public String getInsertedText() {
        return insertedText;
    }
    
    /**
     *  Returns the contents of the text deleted by this macro.
     * @return the deleted contents of the text
     */
    public String getDeletedText() {
        return deletedText;
    }
    
    /**
     * Tests if this macro inserts any text and deletes no text.
     * @return <code>true</code> if this macro performs insertion, otherwise <code>false>
     */
    public boolean inserted() {
        return insertedText.length() != 0 && deletedText.length() == 0;
    }
    
    /**
     * Tests if this macro inserts no text and deletes any text.
     * @return <code>true</code> if this macro performs deletion, otherwise <code>false>
     */
    public boolean deleted() {
        return insertedText.length() == 0 && deletedText.length() != 0;
    }
    
    /**
     * Tests if this macro inserts any text and deletes any text.
     * @return <code>true</code> if this macro performs replacement, otherwise <code>false>
     */
    public boolean replaced() {
        return insertedText.length() != 0 && deletedText.length() != 0;
    }
    
    /**
     * Tests if this macro represents a normal edit operation.
     * @return <code>true</code> if this macro represents a normal edit operation, otherwise <code>false</code>
     */
    public boolean isNormalEdit() {
        return action.equals(Action.EDIT.toString());
    }
    
    /**
     * Tests if this macro represents a cut operation.
     * @return <code>true</code> if this macro represents a cut operation, otherwise <code>false</code>
     */
    public boolean isCut() {
        return action.equals(Action.CUT.toString());
    }
    
    /**
     * Tests if this macro represents a paste operation.
     * @return <code>true</code> if this macro represents a paste operation, otherwise <code>false</code>
     */
    public boolean isPaste() {
        return action.equals(Action.PASTE.toString());
    }
    
    /**
     * Tests if this macro represents an undo operation.
     * @return <code>true</code> if this macro represents an undo operation, otherwise <code>false</code>
     */
    public boolean isUndo() {
        return action.equals(Action.UNDO.toString());
    }
    
    /**
     * Tests if this macro represents a redo operation.
     * @return <code>true</code> if this macro represents a redo operation, otherwise <code>false</code>
     */
    public boolean isRedo() {
        return action.equals(Action.REDO.toString());
    }
    
    /**
     * Tests if this macro represents a diff operation that was automatically performed.
     * @return <code>true</code> if this macro represents a diff operation, otherwise <code>false</code>
     */
    public boolean isDiff() {
        return action.equals(Action.AUTO_DIFF.toString()) || action.equals(Action.IRREGULAR_DIFF.toString());
    }
    
    /**
     * Returns the string for printing, which does not contain a new line character at its end.
     * @return the string for printing
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        
        buf.append(" offset=" + start);
        buf.append(" ins=[" + getShortText(insertedText) + "]");
        buf.append(" del=[" + getShortText(deletedText) + "]");
        return buf.toString();
    }
}
