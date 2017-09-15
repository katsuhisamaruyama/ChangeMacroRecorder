/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;

import javax.json.JsonObject;

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
        ADDED_GIT_INDEX_CHANGED, REMOVED_GIT_INDEX_CHANGED, MODIFIED_GIT_INDEX_CHANGED;
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
     * Creates an object storing information about a file macro.
     * @param action the type of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param code the contents of source code of the file
     * @param charset the name of a charset of the file
     * @param sdpath the path of the source or destination of the rename or move
     */
    public FileMacro(String action, String path, String branch, String code, String charset, String sdpath) {
        super(action, path, branch);
        this.code = code;
        this.charset = charset;
        this.srcDstPath = sdpath;
    }
    
    /**
     * Creates an object storing information about a file macro.
     * @param action the type of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param code the contents of source code of the file
     * @param charset the name of a charset of the file
     * @param sdpath the path of the source or destination of the rename or move
     */
    public FileMacro(Action action, String path, String branch, String code, String charset, String sdpath) {
        this(action.toString(), path, branch, code, charset, sdpath);
    }
    
    /**
     * Creates an object storing information about a file macro.
     * @param action the type of this macro
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param code the contents of source code of the file
     * @param charset the name of a charset of the file
     */
    public FileMacro(Action action, String path, String branch, String code, String charset) {
        this(action.toString(), path, branch, code, charset, path);
    }
    
    /**
     * Creates an object storing information about a file macro.
     * @param time the time when this macro was performed
     * @param action the type of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param code the contents of source code of the file
     * @param charset the name of a charset of the file
     * @param sdpath the path of the source or destination of the rename or move
     */
    protected FileMacro(ZonedDateTime time, String action, MacroPath mpath, String code, String charset, String sdpath) {
        super(time, action, mpath);
        this.code = code;
        this.charset = charset;
        this.srcDstPath = sdpath;
    }
    
    /**
     * Creates a clone of this macro.
     */
    @Override
    public FileMacro clone() {
        return new FileMacro(time, action, macroPath, code, charset, srcDstPath);
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
    public boolean isGitAdded() {
        return action.equals(Action.ADDED_GIT_INDEX_CHANGED.toString());
    }
    
    /**
     * Tests if this macro removes a file from the git repository.
     * @return <code>true</code> if this macro removes a file from the git repository, otherwise <code>false</code>
     */
    public boolean isGitRemoved() {
        return action.equals(Action.REMOVED_GIT_INDEX_CHANGED.toString());
    }
    
    /**
     * Tests if this macro modifies a file within the git repository.
     * @return <code>true</code> if this macro modifies a file within the git repository, otherwise <code>false</code>
     */
    public boolean isGitModified() {
        return action.equals(Action.MODIFIED_GIT_INDEX_CHANGED.toString());
    }
    
    /**
     * Returns the textual description of this macro.
     * @return the textual description
     */
    @Override
    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.getDescription());
        
        buf.append(" code=[" + getShortText(code) + "]");
        return buf.toString();
    }
    
    /**
     * Returns a string that represents a JSON object for a macro.
     * @return the JSON string representation
     */
    @Override
    public String getJSON() {
        
        System.out.println("FILE");
        
        JsonObject json = MacroJSON.getJSONObjectBuikder(this)
          .add(MacroJSON.JSON_ATTR_CODE, code)
          .add(MacroJSON.JSON_ATTR_CHARSET, charset)
          .add(MacroJSON.JSON_ATTR_SRD_DST_PATH, srcDstPath)
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
