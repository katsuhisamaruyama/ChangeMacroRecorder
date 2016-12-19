/*
 *  Copyright 2016
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

/**
 * Stores a macro related to a file operation.
 * @author Katsuhisa Maruyama
 */
public class FileMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        ADDED, REMOVED, OPENED, CLOSED, SAVED, ACTIVATED, REFACTORED,
        MOVED_FROM, MOVED_TO, RENAMED_FROM, RENAMED_TO, CONTENT_CHANGED,
        GIT_ADDED, GIT_REMOVED, GIT_MODIFIED;
    }
    
    /**
     * The contents of source code of the file.
     */
    private String code;
    
    /**
     * The name of a charset of the file.
     */
    private String charset;
    
    /**
     * The path of the source or destination of the rename or move.
     */
    private String srcDstPath;
    
    /**
     * Creates an object storing information on a file macro.
     * @param action the type of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param code the contents of source code of the file
     * @param charset the name of a charset of the file
     * @param sdpath the path of the source or destination of the rename or move
     */
    public FileMacro(Action action, String path, String branch, String code, String charset, String sdpath) {
        super(action.toString(), path, branch);
        this.code = code;
        this.charset = charset;
        this.srcDstPath = sdpath;
    }
    
    /**
     * Creates an object storing information on a file macro.
     * @param action the type of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param code the contents of source code of the file
     * @param charset the name of a charset of the file
     */
    public FileMacro(Action action, String path, String branch, String code, String charset) {
        this(action, path, branch, code, charset, path);
    }
    
    /**
     * Returns the path of the source or destination of the rename or move.
     * @return the the source or destination path of the rename or move
     */
    public String getSrcDstPath() {
        return srcDstPath;
    }
    
    /**
     * Returns source code of the file.
     * @return the contents of the source code
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Returns the name of a charset of the file.
     * @return the name of a charset of the source code
     */
    public String getCharset() {
        return charset;
    }
    
    /**
     * Tests if this macro adds a file.
     * @return <code>true</code> if this macro adds a file, otherwise <code>false</code>
     */
    public boolean isAdd() {
        return action.equals(Action.ADDED.toString());
    }
    
    /**
     * Tests if this macro deletes a file.
     * @return <code>true</code> if this macro deletes a file, otherwise <code>false</code>
     */
    public boolean isDelete() {
        return action.equals(Action.REMOVED.toString());
    }
    
    /**
     * Tests if this macro opens a file.
     * @return <code>true</code> if this macro opens a file, otherwise <code>false</code>
     */
    public boolean isOpen() {
        return action.equals(Action.OPENED.toString());
    }
    
    /**
     * Tests if this macro closes a file.
     * @return <code>true</code> if this macro closes a file, otherwise <code>false</code>
     */
    public boolean isClose() {
        return action.equals(Action.CLOSED.toString());
    }
    
    /**
     * Tests if this macro saves a file.
     * @return <code>true</code> if this macro saves a file, otherwise <code>false</code>
     */
    public boolean isSave() {
        return action.equals(Action.SAVED.toString());
    }
    
    /**
     * Tests if this macro activates a file.
     * @return <code>true</code> if this macro activates a file, otherwise <code>false</code>
     */
    public boolean isActivate() {
        return action.equals(Action.ACTIVATED.toString());
    }
    
    /**
     * Tests if this macro refactors the contents of a file.
     * @return <code>true</code> if this macro refactors the contents of a file, otherwise <code>false</code>
     */
    public boolean isRefactor() {
        return action.equals(Action.REFACTORED.toString());
    }
    
    /**
     * Tests if this macro moves a file from somewhere.
     * @return <code>true</code> if this macro moves a file from somewhere, otherwise <code>false</code>
     */
    public boolean isMoveFrom() {
        return action.equals(Action.MOVED_FROM.toString());
    }
    
    /**
     * Tests if this macro moves a file to somewhere.
     * @return <code>true</code> if this macro moves a file to somewhere, otherwise <code>false</code>
     */
    public boolean isMoveTo() {
        return action.equals(Action.MOVED_TO.toString());
    }
    
    /**
     * Tests if this macro changes the name of a file from the old one.
     * @return <code>true</code> if this macro changes the name of a file from the old one, otherwise <code>false</code>
     */
    public boolean isRenameFrom() {
        return action.equals(Action.RENAMED_FROM.toString());
    }
    
    /**
     * Tests if this macro changes the name of a file to the new one.
     * @return <code>true</code> if this macro changes the name of a file to the new one, otherwise <code>false</code>
     */
    public boolean isRenameTo() {
        return action.equals(Action.RENAMED_TO.toString());
    }
    
    /**
     * Tests if this macro changes the content of a file.
     * @return <code>true</code> if this macro changes the content of a file, otherwise <code>false</code>
     */
    public boolean isContentChange() {
        return action.equals(Action.CONTENT_CHANGED.toString());
    }
    
    /**
     * Tests if this macro adds a file to the git repository.
     * @return <code>true</code> if this macro adds a file to the git repository, otherwise <code>false</code>
     */
    public boolean isGitAdd() {
        return action.equals(Action.GIT_ADDED.toString());
    }
    
    /**
     * Tests if this macro removes a file from the git repository.
     * @return <code>true</code> if this macro removes a file from the git repository, otherwise <code>false</code>
     */
    public boolean isGitRemove() {
        return action.equals(Action.GIT_REMOVED.toString());
    }
    
    /**
     * Tests if this macro modifies a file within the git repository.
     * @return <code>true</code> if this macro modifies a file within the git repository, otherwise <code>false</code>
     */
    public boolean isGitModify() {
        return action.equals(Action.GIT_MODIFIED.toString());
    }
    
    /**
     * Returns the string for printing, which does not contain a new line character at its end.
     * @return the string for printing
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append(" code=[" + getShortText(code) + "]");
        return buf.toString();
    }
}
