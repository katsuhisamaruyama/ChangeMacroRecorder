/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

/**
 * Information about the path of a resource on which a macro was performed.
 * @author Katsuhisa Maruyama
 */
public class MacroPath {
    
    /**
     * The path of a resource on which a macro was performed.
     */
    private String path;
    
    /**
     * The branch name of a resource on which a macro was performed.
     */
    private String branch;
    
    /**
     * The name of a project containing a resource on which a macro was performed.
     */
    private String projectName;
    
    /**
     * The name of a package containing a resource on which a macro was performed.
     */
    private String packageName;
    
    /**
     * The name of a file containing a resource on which a macro was performed.
     */
    private String fileName;
    
    /**
     * Creates information about the path of a resource on which a macro was performed.
     * @param path the path of the resource
     * @param branch the branch of the resource
     * @param projectName the project name of the resource
     * @param packageName the package name of the resource
     * @param fileName the file name of the resource
     */
    public MacroPath(String path, String branch, String projectName, String packageName, String fileName) {
        this.path = path;
        this.branch = branch;
        this.projectName = projectName;
        this.packageName = packageName;
        this.fileName = fileName;
    }
    
    /**
     * Returns the path of the resource.
     * @return the path name
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Returns the branch of the resource.
     * @return the branch name
     */
    public String getBranch() {
        return branch;
    }
    
    /**
     * Returns the name of a project that contains the resource.
     * @return the project name
     */
    public String getProjectName() {
        return projectName;
    }
    
    /**
     * Returns the name of a package that contains the resource.
     * @return the package name
     */
    public String getPackageName() {
        return packageName;
    }
    
    /**
     * Returns the name of a file that contains the resource.
     * @return the filename
     */
    public String getFileName() {
        return fileName;
    }
}
