/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import javax.json.JsonObject;

/**
 * Stores a macro of a trigger related to an event.
 * @author Katsuhisa Maruyama
 */
public class TriggerMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        REFACTORING, COMMAND, UNDO, REDO, GIT, CURSOR_CHANGE;
    }
    
    /**
     * The timing of a trigger.
     */
    public enum Timing {
        BEGIN, END, INSTANT;
    }
    
    /**
     * The timing of a trigger of this macro.
     */
    private Timing timing = null;
    
    /**
     * The command macro that causes this trigger macro.
     */
    private CommandMacro commandMacro = null;
    
    /**
     * Creates an object storing information about a trigger macro.
     * @param action the action of this trigger macro
     * @param path the path of a file or a package on which this trigger macro was performed
     * @param branch the branch name of a file or a package on which this trigger macro was performed
     * @param timing the timing of a trigger of this trigger macro
     */
    public TriggerMacro(Action action, String path, String branch, Timing timing) {
        super(action.toString(), path, branch);
        this.timing = timing;
    }
    
    /**
     * Creates an object storing information about a trigger macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this trigger macro was performed
     * @param branch the branch name of a file or a package on which this trigger macro was performed
     * @param timing the timing of a trigger of this trigger macro
     * @param macro a command macro that causes this trigger macro
     */
    public TriggerMacro(Action action, String path, String branch, Timing timing, CommandMacro macro) {
        this(action, path, branch, timing);
        this.commandMacro = macro;
    }
    
    /**
     * Returns the timing of a trigger of the macro
     * @return the timing of the trigger
     */
    public Timing getTiming() {
        return timing;
    }
    
    /**
     * Returns the command macro that causes this trigger macro.
     * @return the command macro for the trigger macro
     */
    public CommandMacro getCommandMacro() {
        return commandMacro;
    }
    
    /**
     * Tests this macro indicates a trigger the beginning of the event.
     * @return <code>true</code> if this macro indicates the beginning, otherwise <code>false</code>
     */
    public boolean isBegin() {
        return timing == Timing.BEGIN;
    }
    
    /**
     * Tests this macro indicates a trigger of the ending of the event.
     * @return <code>true</code> if this macro indicates the ending, otherwise <code>false</code>
     */
    public boolean isEnd() {
        return timing == Timing.END;
    }
    
    /**
     * Tests this macro indicates completion of the event.
     * @return <code>true</code> if this macro indicates completion of the event, otherwise <code>false</code>
     */
    public boolean isComplete() {
        return isEnd() || action.equals(Action.CURSOR_CHANGE.toString());
    }
    
    /**
     * Returns the textual description of this macro.
     * @return the textual description
     */
    @Override
    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.getDescription());
        
        buf.append(" timing=[" + timing.toString() + "]");
        return buf.toString();
    }
    
    /**
     * Returns the JSON object of this macro.
     * @return the JSON object
     */
    @Override
    public JsonObject getJSON() {
        JsonObject json = MacroJSON.getJSONObjectBuikder(this)
          .add(MacroJSON.JSON_ATTR_TIMING, timing.toString())
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
