/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

/**
 * Stores a macro of a trigger related to an event.
 * @author Katsuhisa Maruyama
 */
public class TriggerMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        REFACTORING, UNDO, REDO, CODE_ASSIST, DIFF, CURSOR_CHANGE;
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
     * The string representing the contents of this macro.
     */
    private String commandId;
    
    /**
     * Creates an object storing information about a trigger macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this trigger macro was performed
     * @param branch the branch name of a file or a package on which this trigger macro was performed
     * @param timing the timing of a trigger of this trigger macro
     * @param commandId the string representing this contents of this trigger macro
     */
    public TriggerMacro(Action action, MacroPath path, String branch, Timing timing, String commandId) {
        super(action.toString(), path, branch);
        this.timing = timing;
        this.commandId = commandId;
    }
    
    /**
     * Creates an object storing information about a trigger macro.
     * @param action the action of this trigger macro
     * @param path the path of a file or a package on which this trigger macro was performed
     * @param branch the branch name of a file or a package on which this trigger macro was performed
     * @param timing the timing of a trigger of this trigger macro
     */
    public TriggerMacro(Action action, MacroPath path, String branch, Timing timing) {
        this(action, path, branch, timing, "");
    }
    
    /**
     * Returns the timing of a trigger of the macro
     * @return the timing of the trigger
     */
    public Timing getTiming() {
        return timing;
    }
    
    /**
     * Returns the string representing the contents of this macro.
     * @return the information on this macro
     */
    public String getCommandId() {
        return commandId;
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
     * Returns the string for printing, which does not contain a new line character at its end.
     * @return the string for printing
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        
        buf.append(" timing=[" + timing.toString() + "]");
        return buf.toString();
    }
}
