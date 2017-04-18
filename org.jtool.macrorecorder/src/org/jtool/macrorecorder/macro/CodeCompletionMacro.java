/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;

/**
 * Stores a code completion macro.
 * @author Katsuhisa Maruyama
 */
public class CodeCompletionMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        QUICK_ASSIST_BEGIN, QUICK_ASSIST_END, CONTENT_ASSIST_BEGIN, CONTENT_ASSIST_END, NONE;
    }
    
    /**
     * The name of a code assistance class.
     */
    private String name;
    
    /**
     * A content assist event related to this macro.
     */
    private ContentAssistEvent event;
    
    /**
     * Creates an object storing information about a code completion macro.
     * @param action the action of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param name the name of a code assistance class
     * @param event content assist event related to this macro
     */
    public CodeCompletionMacro(Action action, String path, String branch, String name, ContentAssistEvent event) {
        super(action.toString(), path, branch);
        this.name = name;
        this.event = event;
    }
    
    /**
     * Returns the name of a code assistance class.
     * @return the code assistance class name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the content assist event related to this macro.
     * @return the content assist event
     */
    public ContentAssistEvent getContentAssistEvent() {
        return event;
    }
    
    /**
     * Tests if this macro represents the beginning of quick assist.
     * @return <code>true</code> if this macro represents the beginning of quick assist, otherwise <code>false</code>
     */
    public boolean isQuickAssistBegin() {
        return action.equals(Action.QUICK_ASSIST_BEGIN.toString());
    }
    
    /**
     * Tests if this macro represents the ending of quick assist.
     * @return <code>true</code> if this macro represents the ending of quick assist, otherwise <code>false</code>
     */
    public boolean isQuickAssistEnd() {
        return action.equals(Action.QUICK_ASSIST_END.toString());
    }
    
    /**
     * Tests if this macro represents the beginning of content assist.
     * @return <code>true</code> if this macro represents the beginning of content assist, otherwise <code>false</code>
     */
    public boolean isContentAssistBegin() {
        return action.equals(Action.CONTENT_ASSIST_BEGIN.toString());
    }
    
    /**
     * Tests if this macro represents the ending of content assist.
     * @return <code>true</code> if this macro represents the ending of content assist, otherwise <code>false</code>
     */
    public boolean isContentAssistEnd() {
        return action.equals(Action.CONTENT_ASSIST_END.toString());
    }
    
    /**
     * Returns the string for printing, which does not contain a new line character at its end.
     * @return the string for printing
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        
        buf.append(" name=[" + name + "]");
        return buf.toString();
    }
}
