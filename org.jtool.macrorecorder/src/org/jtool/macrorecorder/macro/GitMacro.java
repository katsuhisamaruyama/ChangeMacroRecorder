/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

/**
 * Stores a macro related to a git event.
 * @author Katsuhisa Maruyama
 */
public class GitMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        OPEN, REFS_CHANGED, INDEX_CHANGED;
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
        return action.equals(Action.REFS_CHANGED.toString());
    }
    
    /**
     * Tests if this macro represents an index change action.
     * @return <code>true</code> if this macro represents an index change action, otherwise <code>false</code>
     */
    public boolean isIndexChange() {
        return action.equals(Action.INDEX_CHANGED.toString());
    }
    
    /**
     * Returns the string for printing, which does not contain a new line character at its end.
     * @return the string for printing
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        return buf.toString();
    }
}
