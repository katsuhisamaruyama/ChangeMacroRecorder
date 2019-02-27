/*
 *  Copyright 2016-2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;
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
        REFS_CHANGE, INDEX_CHANGE;
    }
    
    /**
     * The directory of the git repository.
     */
    private String dir;
    
    /**
     * Creates an object storing information about a git command macro.
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param dir the directory of the git repository
     */
    public GitMacro(String action, MacroPath mpath, String dir) {
        super(action, mpath);
        this.dir = dir;
    }
    
    /**
     * Creates an object storing information about a git command macro.
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param dir the directory of the git repository
     */
    public GitMacro(Action action, MacroPath mpath, String dir) {
        this(action.toString(), mpath, dir);
    }
    
    /**
     * Creates an object storing information about a git command macro.
     * @param time the time when this macro was performed
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param dir the directory of the git repository
     */
    protected GitMacro(ZonedDateTime time, String action, MacroPath mpath, String dir) {
        super(time, action, mpath);
        this.dir = dir;
    }
    
    /**
     * Creates a clone of this macro.
     */
    @Override
    public GitMacro clone() {
        return new GitMacro(time, action, macroPath, dir);
    }
    
    /**
     * Returns the directory of the git repository.
     * @return the git directory
     */
    public String getDir() {
        return dir;
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
     * Obtains a JSON object that stores information on this macro.
     * @return the JSON object
     */
    @Override
    public JsonObject getJSON() {
        JsonObject json = MacroJSON.getJSONObjectBuilder(this).build();
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
