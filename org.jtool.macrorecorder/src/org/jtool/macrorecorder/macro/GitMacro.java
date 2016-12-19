/*
 *  Copyright 2016
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.util.Set;

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
     * The name of the current branch that HEAD points to.
     */
    private String branch;
    
    /**
     * The list of files modified on disk relative to the index.
     */
    private Set<String> filesModified;
    
    /**
     * The list of files added to the index, not in HEAD.
     */
    private Set<String> filesAdded;
    
    /**
     * The list of files removed from index, but in HEAD.
     */
    private Set<String> filesRemoved;
    
    /**
     * Creates an object storing information on an resource change macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this macro was performed
     * @param branch the branch name of a file or a package on which this macro was performed
     * @param mfiles files modified on disk relative to the index
     * @param afiles files added to the index
     * @param rfiles files removed from the index
     */
    public GitMacro(Action action, String path, String branch, Set<String> mfiles, Set<String> afiles, Set<String> rfiles) {
        super(action.toString(), path, branch);
        this.branch = branch;
        this.filesModified = mfiles;
        this.filesAdded = afiles;
        this.filesRemoved = rfiles;
    }
    
    /**
     * Returns the name of the current branch that HEAD points to.
     * @return the branch name
     */
    public String getBranch() {
        return branch;
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
     * Returns files added to the index.
     * @return the list of the names of the added files
     */
    public Set<String> getAddedFiles() {
        return filesAdded;
    }
    
    /**
     * Returns files removed from index.
     * @return the list of the names of the removed files
     */
    public Set<String> getRemovedFiles() {
        return filesRemoved;
    }
    
    /**
     * Returns files modified on disk relative to the index.
     * @return the list of the names of the modified files
     */
    public Set<String> getModifiedFiles() {
        return filesModified;
    }
    
    /**
     * Obtains the list of names.
     * @param the collection of names
     * @return the name string
     */
    public static String getNameList(Set<String> names) {
        if (names.size() == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        for (String name : names) {
            buf.append(":" + name);
        }
        return buf.substring(1);
    }
    
    /**
     * Returns the string for printing, which does not contain a new line character at its end.
     * @return the string for printing
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        
        buf.append(" added=[" + getNameList(getAddedFiles()) + "]");
        buf.append(" removed=[" + getNameList(getRemovedFiles()) + "]");
        buf.append(" modified=[" + getNameList(getModifiedFiles()) + "]");
        return buf.toString();
    }
}
