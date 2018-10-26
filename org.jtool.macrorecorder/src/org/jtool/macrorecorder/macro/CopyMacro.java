/*
 *  Copyright 2016-2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;
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
     * @param mpath the information about the path a resource on which this macro was performed
     * @param start the leftmost offset of the document changed by this macro
     * @param text the contents of the document copied by the macro
     */
    public CopyMacro(String action, MacroPath mpath, int start, String text) {
        super(action, mpath);
        this.start = start;
        this.copiedText = text;
    }
    
    /**
     * Creates an object storing information about a copy macro.
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param start the leftmost offset of the document changed by this macro
     * @param text the contents of the document copied by the macro
     */
    public CopyMacro(Action action, MacroPath mpath, int start, String text) {
        this(action.toString(), mpath, start, text);
    }
    
    /**
     * Creates an object storing information about a copy macro.
     * @param time the time when this macro was performed
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param start the leftmost offset of the document changed by this macro
     * @param text the contents of the document copied by the macro
     */
    protected CopyMacro(ZonedDateTime time, String action, MacroPath mpath, int start, String text) {
        super(time, action.toString(), mpath);
        this.start = start;
        this.copiedText = text;
    }
    
    /**
     * Creates a clone of this macro.
     */
    @Override
    public CopyMacro clone() {
        return new CopyMacro(time, action, macroPath, start, copiedText);
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
     * Obtains a JSON object that stores information on this macro.
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
