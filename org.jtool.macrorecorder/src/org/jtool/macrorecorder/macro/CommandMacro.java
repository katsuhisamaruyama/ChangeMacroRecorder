/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import org.eclipse.core.commands.ExecutionEvent;

/**
 * Stores an execution macro.
 * @author Katsuhisa Maruyama
 */
public class CommandMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        EXECUTION, REFACTORING;
    }
    
    /**
     * The string representing the contents of this macro.
     */
    private String commandId;
    
    /**
     * An execution event related to this macro.
     */
    private ExecutionEvent event;
    
    /**
     * Creates an object storing information about an execution macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this macro was performed
     * @param branch the branch name of a file or a package on which this macro was performed
     * @param commandId the detailed information about this macro
     * @param event an execution event related to this macro
     */
    public CommandMacro(Action action, String path, String branch, String commandId, ExecutionEvent event) {
        super(action.toString(), path, branch);
        this.commandId = commandId;
        this.event = event;
    }
    
    /**
     * Returns information about this macro.
     * @return the string representing the contents of this macro
     */
    public String getCommandId() {
        return commandId;
    }
    
    /**
     * Returns the execution event related to this macro.
     * @return the execution event
     */
    public ExecutionEvent getExecutionEvent() {
        return event;
    }
    
    /**
     * Tests if this macro represents refacttoring.
     * @return <code>true</code> if this macro represents refacttoring, otherwise <code>false</code>
     */
    public boolean isRefactoring() {
        return action.equals(Action.REFACTORING.toString());
    }
    
    /**
     * Returns the string for printing, which does not contain a new line character at its end.
     * @return the string for printing
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        
        buf.append(" command=[" + commandId + "]");
        return buf.toString();
    }
}
