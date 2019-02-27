/*
 *  Copyright 2016-2019
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
        REFACTORING, COMMAND, UNDO, REDO, GIT, CODE_COMPLETION, CURSOR_CHANGE;
    }
    
    /**
     * The timing of a trigger.
     */
    public enum Timing {
        BEGIN, END, CANCEL, INSTANT;
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
     * @param mpath the information about the path a resource on which this macro was performed
     * @param timing the timing of a trigger of this trigger macro
     * @param macro a command macro that causes this trigger macro
     */
    public TriggerMacro(String action, MacroPath mpath, Timing timing, CommandMacro macro) {
        super(action, mpath);
        this.timing = timing;
        this.commandMacro = macro;
    }
    
    /**
     * Creates an object storing information about a trigger macro.
     * @param action the action of this trigger macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param timing the timing of a trigger
     * @param macro a command macro that causes this macro
     */
    public TriggerMacro(Action action, MacroPath mpath, Timing timing, CommandMacro macro) {
        this(action.toString(), mpath, timing, macro);
    }
    
    /**
     * Creates an object storing information about a trigger macro.
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param timing the timing of a trigger
     */
    public TriggerMacro(Action action, MacroPath mpath, Timing timing) {
        this(action.toString(), mpath, timing, null);
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
     * Tests this macro indicates a trigger of the cancellation of the event.
     * @return <code>true</code> if this macro indicates the cancellation, otherwise <code>false</code>
     */
    public boolean isCancel() {
        return timing == Timing.CANCEL;
    }
    
    /**
     * Tests this macro indicates the instant event.
     * @return <code>true</code> if this macro indicates the instant event, otherwise <code>false</code>
     */
    public boolean isInstant() {
        return timing == Timing.INSTANT;
    }
    
    /**
     * Tests this macro indicates the cursor change event.
     * @return <code>true</code> if this macro indicates the cursor change event, otherwise <code>false</code>
     */
    public boolean cursorChanged() {
        return action.equals(Action.CURSOR_CHANGE.toString());
    }
    
    /**
     * Tests this macro indicates completion of the event.
     * @return <code>true</code> if this macro indicates completion of the event, otherwise <code>false</code>
     */
    public boolean isComplete() {
        return isEnd() || cursorChanged();
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
     * Obtains a JSON object that stores information on this macro.
     * @return the JSON object
     */
    @Override
    public JsonObject getJSON() {
        JsonObject json = MacroJSON.getJSONObjectBuilder(this)
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
