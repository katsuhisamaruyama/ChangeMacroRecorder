/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

/**
 * Stores an execution macro.
 * @author Katsuhisa Maruyama
 */
public class CommandMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        EXECUTION;
    }
    
    /**
     * The string representing the contents of this macro.
     */
    private String commandId;
    
    /**
     * Creates an object storing information about an execution macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this macro was performed
     * @param branch the branch name of a file or a package on which this macro was performed
     * @param commandId the detailed information about this macro
     */
    public CommandMacro(Action action, String path, String branch, String commandId) {
        super(action.toString(), path, branch);
        this.commandId = commandId;
    }
    
    /**
     * Returns the string representing the contents of this macro.
     * @return the information on this macro
     */
    public String getCommandId() {
        return commandId;
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
