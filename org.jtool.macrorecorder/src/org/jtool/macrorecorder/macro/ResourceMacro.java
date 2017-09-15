/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import javax.json.JsonObject;

/**
 * Stores a macro related to a resource change.
 * @author Katsuhisa Maruyama
 */
public class ResourceMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        ADDED, REMOVED, CHANGED, RENAMED_FROM, RENAMED_TO, MOVED_FROM, MOVED_TO;
    }
    
    /**
     * The type of this macro.
     */
    public enum Target {
        PROJECT, PACKAGE, FILE, TYPE, OTHERS, NONE;
    }
    
    /**
     * The kind of the target of the resource.
     */
    private Target target;
    
    /**
     * The path of the source or destination of the resource.
     */
    private String srcDstPath;
    
    /**
     * Creates an object storing information about a resource change macro.
     * @param action the action of this macro
     * @param path the path of the resource on which this macro was performed
     * @param branch the branch name of the resource on which this macro was performed
     * @param target the kind of the target of the resource
     * @param sdpath the path of the source or destination of the resource
     */
    public ResourceMacro(Action action, String path, String branch, Target target, String sdpath) {
        super(action.toString(), path, branch);
        this.target = target;
        this.srcDstPath = sdpath;
    }
    
    /**
     * Returns the kind of the target of the resource.
     * @return the target of the resource, or <code>null</code> if the target is not either a project, package, or file
     */
    public String getTarget() {
        return target.toString();
    }
    
    /**
     * Returns the path of the source or destination of the resource.
     * @return the the source or destination path of the resource
     */
    public String getSrcDstPath() {
        return srcDstPath;
    }
    
    /**
     * Tests if the changed resource is a project.
     * @return <code>true</code> if the changed resource is a project, otherwise <code>false</code>
     */
    public boolean isProjectChange() {
        return target == Target.PROJECT;
    }
    
    /**
     * Tests if the changed resource is a package.
     * @return <code>true</code> if the changed resource is a package, otherwise <code>false</code>
     */
    public boolean isPackageChange() {
        return target == Target.PACKAGE;
    }
    
    /**
     * Tests if the changed resource is a file.
     * @return <code>true</code> if the changed resource is a file, otherwise <code>false</code>
     */
    public boolean isFileChange() {
        return target == Target.FILE;
    }
    
    /**
     * Tests if the changed resource can be recorded.
     * @return <code>true</code> if the changed resource can be recorded, otherwise <code>false</code>
     */
    public boolean canRecord() {
        return isProjectChange() || isPackageChange() || isFileChange();
    }
    
    /**
     * Tests if the changed target was added in this macro.
     * @return <code>true</code> if the changed target was added, otherwise <code>false</code>
     */
    public boolean isAdd() {
        return action.equals(Action.ADDED.toString());
    }
    
    /**
     * Tests if the changed target was removed in this macro.
     * @return <code>true</code> if the changed target was removed, otherwise <code>false</code>
     */
    public boolean isRemove() {
        return action.equals(Action.REMOVED.toString());
    }
    
    /**
     * Tests if the changed target was removed in this macro.
     * @return <code>true</code> if the changed target was removed, otherwise <code>false</code>
     */
    public boolean isChange() {
        return action.equals(Action.CHANGED.toString());
    }
    
    /**
     * Tests if the changed target was renamed from another in this macro.
     * @return <code>true</code> if the changed target was renamed from another, otherwise <code>false</code>
     */
    public boolean isRenameFrom() {
        return action.equals(Action.RENAMED_FROM.toString());
    }
    
    /**
     * Tests if the changed target was renamed to another in this macro.
     * @return <code>true</code> if the changed target was renamed to another, otherwise <code>false</code>
     */
    public boolean isRenameTo() {
        return action.equals(Action.RENAMED_TO.toString());
    }
    
    /**
     * Tests if the changed target was moved from another in this macro.
     * @return <code>true</code> if the changed target was moved from another, otherwise <code>false</code>
     */
    public boolean isMoveFrom() {
        return action.equals(Action.MOVED_FROM.toString());
    }
    
    /**
     * Tests if the changed target was moved to another in this macro.
     * @return <code>true</code> if the changed target was moved to another, otherwise <code>false</code>
     */
    public boolean isMoveTo() {
        return action.equals(Action.MOVED_TO.toString());
    }
    
    /**
     * Returns the textual description of this macro.
     * @return the textual description
     */
    @Override
    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.getDescription());
        
        buf.append(" target=[" + target.toString() + "]");
        if (!getPath().equals(srcDstPath)) {
            if (isRemove() || isMoveTo() || isRenameTo()) {
                buf.append(" to [" + srcDstPath + "]");
            } else {
                buf.append(" from [" + srcDstPath + "]");
            }
        }
        return buf.toString();
    }
    
    /**
     * Returns the JSON object of this macro.
     * @return the JSON object
     */
    @Override
    public JsonObject getJSON() {
        JsonObject json = super.getJSONObjectBuikderOfMacro()
          .add(MacroJSON.JSON_ATTR_RESOURCE_TARGET, target.toString())
          .add(MacroJSON.JSON_ATTR_SRD_DST_PATH, srcDstPath)
          .build();
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
