/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;

import java.time.ZonedDateTime;

import javax.json.JsonObject;

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
     * The string representing the command identification of this macro.
     */
    private String commandId;
    
    /**
     * A content assist event related to this macro.
     */
    private ContentAssistEvent event;
    
    /**
     * Creates an object storing information about a code completion macro.
     * @param action the action of this macro
     * @param path the path of a file or a package this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param commandId the command information about this macro
     * @param event a content assist event related to this macro
     */
    protected CodeCompletionMacro(String action, String path, String branch, String commandId, ContentAssistEvent event) {
        super(action, path, branch);
        this.commandId = commandId;
        this.event = event;
    }
    
    /**
     * Creates an object storing information about a code completion macro.
     * @param action the action of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param commandId the command information about this macro
     * @param event a content assist event related to this macro
     */
    public CodeCompletionMacro(Action action, String path, String branch, String commandId, ContentAssistEvent event) {
        this(action.toString(), path, branch, commandId, event);
    }
    
    /**
     * Creates an object storing information about a code completion macro.
     * @param time the time when this macro was performed
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param commandId the command information about this macro
     * @param event a content assist event related to this macro
     */
    protected CodeCompletionMacro(ZonedDateTime time, String action, MacroPath mpath, String commandId, ContentAssistEvent event) {
        super(time, action, mpath);
        this.commandId = commandId;
        this.event = event;
    }
    
    /**
     * Creates a clone of this macro.
     */
    @Override
    public CodeCompletionMacro clone() {
        return new CodeCompletionMacro(time, action, macroPath, commandId, event);
    }
    
    /**
     * Returns command information about this macro.
     * @return the string representing the command identification of this macro
     */
    public String getCommandId() {
        return commandId;
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
     * Returns the textual description of this macro.
     * @return the textual description
     */
    @Override
    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.getDescription());
        
        buf.append(" command=[" + commandId + "]");
        return buf.toString();
    }
    
    /**
     * Returns a string that represents a JSON object for a macro.
     * @return the JSON string representation
     */
    @Override
    public String getJSON() {
        JsonObject json = MacroJSON.getJSONObjectBuikder(this)
          .add(MacroJSON.JSON_ATTR_COMMAND, commandId)
          .build();
        return MacroJSON.stringify(json);
    }
    
    /**
     * Returns the string for printing.
     * @return the string for printing
     */
    @Override
    public String toString() {
        return getDescription();
    }
}
