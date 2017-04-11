/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import org.jtool.macrorecorder.Activator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores a macro.
 * @author Katsuhisa Maruyama
 */
public class Macro {
    
    /**
     * The time when this macro was executed.
     */
    protected ZonedDateTime time;
    
    /**
     * The action of this macro.
     */
    protected String action;
    
    /**
     * The path of a resource on which this macro was performed.
     */
    protected String path;
    
    /**
     * The branch name of a resource on which this macro was performed.
     */
    protected String branch;
    
    /**
     * The name of a project containing a resource on which this macro was performed.
     */
    protected String projectName;
    
    /**
     * The path of a source folder containing a file on which this macro was performed.
     */
    private String sourcePath;
    
    /**
     * The cache of pairs of a project name and its source path.
     */
    private static Map<String, String> sourcePathMap = new HashMap<String, String>();
    
    /**
     * The collection of raw macros that were recorded.
     */
    protected List<Macro> rawMacros;
    
    /**
     * Creates an object storing information about a macro.
     * @param time the time when this macro was performed
     * @param action the action of this macro
     * @param path the path of a resource on which this macro was performed
     * @param branch the branch of a resource on which this macro was performed
     */
    public Macro(ZonedDateTime time, String action, String path, String branch) {
        this.time = time;
        this.action = action;
        this.path = path;
        this.branch = branch;
        this.projectName = getProjectName(path);
        this.sourcePath = getSourcePath(path);
        
        delay();
    }
    
    /**
     * Creates an object storing information about a macro.
     * @param action the action of this macro
     * @param path the path of a resource on which this macro was performed
     * @param branch the branch of a resource on which this macro was performed
     */
    public Macro(String action, String path, String branch) {
        this(ZonedDateTime.now(), action, path, branch);
    }
    
    /**
     * Delay so as to prohibit macros with the same time stamp
     */
    private void delay() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }
    }
    
    /**
     * Returns the time when this macro was executed.
     * @return the time of this macro
     */
    public ZonedDateTime getTime() {
        return time;
    }
    
    /**
     * Returns the time when this macro was executed.
     * @return the time of this macro
     */
    public long getTimeAsLong() {
        return time.toInstant().toEpochMilli();
    }
    
    /**
     * Sets the action of this macro
     * @param action the action of the macro
     */
    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * Returns the action of this macro
     * @return the action of the macro
     */
    public String getAction() {
        return action;
    }
    
    /**
     * Returns the path of a resource on which this macro was performed.
     * @return the path
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Returns the branch of a resource on which this macro was performed.
     * @return the branch
     */
    public String getBranch() {
        return branch;
    }
    
    /**
     * Returns the name of a project containing a resource on which this macro was performed.
     * @return the project name
     */
    public String getProjectName() {
        return projectName;
    }
    
    /**
     * Extracts the name of a project from the path of a resource.
     * @param path the path of the resource
     * @return the project name, or an empty string if the path is invalid
     */
    private String getProjectName(String fullPath) {
        if (fullPath == null) {
            return "";
        }
        
        int firstIndex = fullPath.indexOf(File.separatorChar, 1);
        if (firstIndex == -1) {
            return "";
        }
        
        String name = fullPath.substring(1, firstIndex);
        return name;
    }
    
    /**
     * Returns the name of a package containing a resource on which this macro was performed.
     * @return the package name, or an empty string if the path is invalid
     */
    public String getPackageName() {
        if (path == null || sourcePath == null) {
            return "";
        }
        
        int firstIndex = sourcePath.length() + 1;
        int lastIndex = path.lastIndexOf(File.separatorChar) + 1;
        if (firstIndex == -1 || lastIndex == -1) {
            return "";
        }
        
        if (firstIndex  == lastIndex) {
            return "(default package)";
        }
        
        String name = path.substring(firstIndex, lastIndex - 1);
        name = name.replace(File.separatorChar, '.');
        return name;
    }
    
    /**
     * Returns the name of a resource on which this macro was performed.
     * @return the resource name without its location information
     */
    public String getFileName() {
        if (path == null) {
            return "";
        }
        
        int lastIndex = path.lastIndexOf(File.separatorChar) + 1;
        if (lastIndex == -1) {
            return "";
        }
        
        String name = path.substring(lastIndex);
        return name;
    }
    
    /**
     * Extracts the path of a source folder from the path of a file.
     * @param path the path of a file
     * @return the path of the source holder
     */
    private String getSourcePath(String path) {
        String key = Activator.getWorkspacePath() + "$" + projectName;
        String srcpath = sourcePathMap.get(key);
        if (srcpath != null) {
            return srcpath;
        }
        
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        IJavaProject javaProject = JavaCore.create(project);
        
        try {
            IPackageFragmentRoot[] packageFragmentRoot = javaProject.getAllPackageFragmentRoots();
            for (int i = 0; i < packageFragmentRoot.length; i++) {
                IPackageFragmentRoot packageRoot = packageFragmentRoot[i];
                if (packageRoot.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT &&
                    packageRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    
                    srcpath = packageRoot.getPath().toString();
                    if (path.startsWith(srcpath)) {
                        sourcePathMap.put(key, srcpath);
                        return srcpath;
                    }
                }
            }
        } catch (JavaModelException e) { /* empty */ }
        return "";
    }
    
    /**
     * Returns the path of a source folder containing a resource on which this macro was performed.
     * @return the path of the source holder
     */
    public String getSourcePath() {
        return sourcePath;
    }
    
    /**
     * Obtains the name of a user.
     * @return the user name
     */
    public String getUserName() {
        return System.getProperty("user.name");
    }
    
    /**
     * Sets the collection of raw macros that were recorded.
     * @param macros the raw macros to be stored
     */
    public void setRawMacros(List<Macro> macros) {
        rawMacros = macros;
    }
    
    /**
     * Returns the collection of raw macros that were recorded.
     * @return raw macros
     */
    public List<Macro> getRawMacros() {
        return rawMacros;
    }
    
    /**
     * Obtains the formated time information.
     * @param time the time information
     * @return the formatted string of the time
     */
    protected String getFormatedTime(ZonedDateTime t) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        return t.format(formatter);
    }
    
    /**
     * Converts a text into its pretty one.
     * @param text the original text
     * @return the text consists of the first four characters not including the new line
     */
    protected String getShortText(String text) {
        if (text == null) {
            return "NULL";
        }
        
        final int LESS_LEN = 20;
        String text2;
        if (text.length() > LESS_LEN) {
            text2 = text.substring(0, LESS_LEN) + "...";
        } else {
            text2 = text;
        }
        return text2.replace('\n', '~');
    }
    
    /**
     * Returns the name of this instance.
     * @return the name of the instance without its package name.
     */
    protected String getClassName() {
        String fqn = this.getClass().getName();
        int sep = fqn.lastIndexOf('.');
        if (sep != -1) {
            return fqn.substring(sep + 1, fqn.length());
        }
        return fqn;
    }
    
    /**
     * Returns the string for printing.
     * @return the string for printing
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{" + getClassName() + "} ");
        buf.append(getFormatedTime(time));
        buf.append(" " + action);
        buf.append(" path=[" + path + "]");
        buf.append(" branch=[" + branch + "]");
        return buf.toString();
    }
}
