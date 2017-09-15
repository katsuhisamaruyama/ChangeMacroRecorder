/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import javax.json.JsonObject;

/**
 * Stores a macro related to a git event.
 * @author Katsuhisa Maruyama
 */
public class GitMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        OPEN, REFS_CHANGE, INDEX_CHANGE;
    }
    
    /**
     * Creates an object storing information about an resource change macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this macro was performed
     * @param branch the branch name of a file or a package on which this macro was performed
     */
    public GitMacro(Action action, String path, String branch) {
        super(action.toString(), path, branch);
    }
    
    /**
     * Tests if this macro represents an open action.
     * @return <code>true</code> if this macro represents an open action, otherwise <code>false</code>
     */
    public boolean isOpen() {
        return action.equals(Action.OPEN.toString());
    }
    
    /**
     * Tests if this macro represents a refs change action.
     * @return <code>true</code> if this macro represents a refs change action, otherwise <code>false</code>
     */
    public boolean isRefsChange() {
        return action.equals(Action.REFS_CHANGE.toString());
    }
    
    /**
     * Tests if this macro represents an index change action.
     * @return <code>true</code> if this macro represents an index change action, otherwise <code>false</code>
     */
    public boolean isIndexChange() {
        return action.equals(Action.INDEX_CHANGE.toString());
    }
    
    /**
     * Returns the textual description of this macro.
     * @return the textual description
     */
    @Override
    public String getDescription() {
        return super.getDescription();
    }
    
    /**
     * Returns the JSON object of this macro.
     * @return the JSON object
     */
    @Override
    public JsonObject getJSON() {
        return super.getJSONObjectBuikderOfMacro().build();
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
