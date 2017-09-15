/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import javax.json.JsonObject;

/**
 * Stores a macro occurring copy.
 * @author Katsuhisa Maruyama
 */
public class CopyMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        COPY;
    }
    
    /**
     * The leftmost offset of the document copied by this macro.
     */
    private int start;
    
    /**
     * The contents of the document copied by this macro.
     */
    private String copiedText;
    
    /**
     * Creates an object storing information about a copy macro.
     * @param action the action of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param start the leftmost offset of the document changed by this macro
     * @param text the contents of the document copied by the macro
     */
    public CopyMacro(Action action, String path, String branch, int start, String text) {
        super(action.toString(), path, branch);
        this.start = start;
        this.copiedText = text;
    }
    
    /**
     * Returns the leftmost offset of the document changed by this macro.
     * @return the leftmost offset of the document
     */
    public int getStart() {
        return start;
    }
    
    /**
     * Returns the contents of the document copied by this macro.
     * @return the copied contents of the document
     */
    public String getCopiedText() {
        return copiedText;
    }
    
    /**
     * Returns the textual description of this macro.
     * @return the textual description
     */
    @Override
    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.getDescription());
        
        buf.append(" offset=" + start);
        buf.append(" copy=[" + getShortText(copiedText) + "]");
        return buf.toString();
    }
    
    /**
     * Returns the JSON object of this macro.
     * @return the JSON object
     */
    @Override
    public JsonObject getJSON() {
        JsonObject json = MacroJSON.getJSONObjectBuikder(this)
          .add(MacroJSON.JSON_ATTR_OFFSET, start)
          .add(MacroJSON.JSON_ATTR_COPYED_TEXT, copiedText)
          .build();
        return json;
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
