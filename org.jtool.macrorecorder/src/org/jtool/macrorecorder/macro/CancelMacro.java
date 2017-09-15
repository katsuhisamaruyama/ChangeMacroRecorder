/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;

/**
 * Stores a cancel macro that cancels another macro.
 * @author Katsuhisa Maruyama
 */
public class CancelMacro extends DocumentMacro {
    
    /**
     * Creates an object storing information about a cancel macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this macro was performed
     * @param branch the branch name of a file or a package on which this macro was performed
     * @param start the leftmost offset of the document changed by this macro
     * @param itext the contents of the document inserted by the macro
     * @param dtext the contents of the document deleted by the macro
     */
    public CancelMacro(String action, String path, String branch, int start, String itext, String dtext) {
        super(action, path, branch, start, itext, dtext);
    }
    
    /**
     * Creates an object storing information about a cancel macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this macro was performed
     * @param branch the branch name of a file or a package on which this macro was performed
     * @param start the leftmost offset of the document changed by this macro
     * @param itext the contents of the document inserted by the macro
     * @param dtext the contents of the document deleted by the macro
     */
    public CancelMacro(Action action, String path, String branch, int start, String itext, String dtext) {
       this(action.toString(), path, branch, start, itext, dtext);
    }
    
    /**
     * Creates an object storing information about a cancel macro.
     * @param time the time when this macro was performed
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param start the leftmost offset of the document changed by this macro
     * @param itext the contents of the document inserted by the macro
     * @param dtext the contents of the document deleted by the macro
     */
    protected CancelMacro(ZonedDateTime time, String action, MacroPath mpath, int start, String itext, String dtext) {
        super(time, action, mpath, start, itext, dtext);
    }
    
    /**
     * Creates a clone of this macro.
     */
    @Override
    public CancelMacro clone() {
        return new CancelMacro(time, action, macroPath, start, insertedText, deletedText);
    }
}
