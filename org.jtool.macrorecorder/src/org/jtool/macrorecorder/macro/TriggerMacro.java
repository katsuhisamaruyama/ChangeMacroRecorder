/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;

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
     * @param macro a command macro that causes this trigger macro
     */
    public TriggerMacro(String action, String path, String branch, Timing timing, CommandMacro macro) {
        super(action, path, branch);
        this.timing = timing;
        this.commandMacro = macro;
    }
    
    /**
     * Creates an object storing information about a trigger macro.
     * @param action the action of this trigger macro
     * @param path the path of a file or a package on which this trigger macro was performed
     * @param branch the branch name of a file or a package on which this trigger macro was performed
     * @param timing the timing of a trigger
     * @param macro a command macro that causes this macro
     */
    public TriggerMacro(Action action, String path, String branch, Timing timing, CommandMacro macro) {
        this(action.toString(), path, branch, timing, macro);
    }
    
    /**
     * Creates an object storing information about a trigger macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this trigger macro was performed
     * @param branch the branch name of a file or a package on which this trigger macro was performed
     * @param timing the timing of a trigger
     */
    public TriggerMacro(Action action, String path, String branch, Timing timing) {
        this(action.toString(), path, branch, timing, null);
    }
    
    /**
     * Creates an object storing information about a trigger macro.
     * @param time the time when this macro was performed
     * @param action the action of this trigger macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param timing the timing of a trigger
     * @param macro a command macro that causes this macro
     */
    protected TriggerMacro(ZonedDateTime time, String action, MacroPath mpath, Timing timing, CommandMacro macro) {
        super(time, action, mpath);
        this.timing = timing;
        this.commandMacro = macro;
    }
    
    /**
     * Creates a clone of this macro.
     */
    @Override
    public TriggerMacro clone() {
        return new TriggerMacro(time, action, macroPath, timing, commandMacro);
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
     * Returns a string that represents a JSON object for a macro.
     * @return the JSON string representation
     */
    @Override
    public String getJSON() {
        JsonObject json = MacroJSON.getJSONObjectBuikder(this)
          .add(MacroJSON.JSON_ATTR_TIMING, timing.toString())
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
