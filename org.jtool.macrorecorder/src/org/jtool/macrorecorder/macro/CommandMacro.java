/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;
import javax.json.JsonObject;

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
     * The string representing the command identification of this macro.
     */
    private String commandId;
    
    /**
     * Creates an object storing information about a command execution macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this macro was performed
     * @param branch the branch name of a file or a package on which this macro was performed
     * @param commandId the command information about this macro
     */
    public CommandMacro(String action, String path, String branch, String commandId) {
        super(action, path, branch);
        this.commandId = commandId;
    }
    
    /**
     * Creates an object storing information about a command execution macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this macro was performed
     * @param branch the branch name of a file or a package on which this macro was performed
     * @param commandId the command information about this macro
     */
    public CommandMacro(Action action, String path, String branch, String commandId) {
        this(action.toString(), path, branch, commandId);
    }
    
    /**
     * Creates an object storing information about a command execution macro.
     * @param time the time when this macro was performed
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param commandId the command information about this macro
     */
    protected CommandMacro(ZonedDateTime time, String action, MacroPath mpath, String commandId) {
        super(time, action, mpath);
        this.commandId = commandId;
    }
    
    /**
     * Creates a clone of this macro.
     */
    @Override
    public CommandMacro clone() {
        return new CommandMacro(time, action, macroPath, commandId);
    }
    
    /**
     * Returns command information about this macro.
     * @return the string representing the command identification of this macro
     */
    public String getCommandId() {
        return commandId;
    }
    
    /**
     * Tests if this macro represents refactoring.
     * @return <code>true</code> if this macro represents refactoring, otherwise <code>false</code>
     */
    public boolean isRefactoring() {
        return action.equals(Action.REFACTORING.toString());
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
