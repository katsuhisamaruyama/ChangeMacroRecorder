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
     * There is no change operations with the same time within the same file
     * (with the same path and the same branch).
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
     * The name of a package containing a resource on which this macro was performed.
     */
    protected String packageName;
    
    /**
     * The cache of pairs of a project name and its source path.
     */
    private static Map<String, String> sourcePathMap = new HashMap<String, String>();
    
    /**
     * The collection of raw macros that were recorded.
     */
    protected List<Macro> rawMacros;
    
    /**
     * The string value that represents the extension of a Java file.
     */
    public static final String JAVA_FILE_EXT = ".java";
    
    /**
     * The string value that represents a Java default package.
     */
    public static final String JAVA_DEFAULT_PACKAGE = "(default package)";
    
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
        projectName = getProjectName(path);
        packageName = getPackageName(projectName, path);
        
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
     * Returns the name of a package containing a resource on which this macro was performed.
     * @return the package name
     */
    public String getPackageName() {
        return packageName;
    }
    
    /**
     * Extracts the name of a project from the path of a resource.
     * @param pathname the path of the resource
     * @return the project name, or an empty string if the path is invalid
     */
    private String getProjectName(String pathname) {
        if (pathname == null) {
            return "";
        }
        
        int index = pathname.indexOf(File.separatorChar, 1);
        if (index == -1) {
            return pathname.substring(1);
        }
        
        return pathname.substring(1, index);
    }
    
    /**
     * Returns the name of a package containing a resource on which this macro was performed.
     * @param pathname the path of the resource
     * @return the package name, or an empty string if the path is invalid
     */
    public String getPackageName(String projectName, String pathname) {
        if (pathname == null || projectName.length() == 0) {
            return "";
        }
        
        int index = pathname.lastIndexOf(File.separatorChar);
        if (index == -1 || index == 0) {
            return "";
        }
        
        if (pathname.endsWith(JAVA_FILE_EXT)) {
            pathname = pathname.substring(0, index);
        }
        String srcpath = getSourcePath(pathname);
        if (srcpath == null) {
            return "";
        }
        pathname = pathname.substring(srcpath.length());
        if (pathname.length() == 0) {
            return JAVA_DEFAULT_PACKAGE;
        }
        
        index = pathname.lastIndexOf(File.separatorChar);
        pathname = pathname.substring(1);
        return pathname.replace(File.separatorChar, '.');
    }
    
    /**
     * Returns the name of a resource on which this macro was performed.
     * @return the resource name without its location information
     */
    public String getFileName() {
        if (path == null || !path.endsWith(JAVA_FILE_EXT)) {
            return "";
        }
        
        int index = path.lastIndexOf(File.separatorChar);
        if (index == -1) {
            return "";
        }
        
        return path.substring(index + 1);
    }
    
    /**
     * Extracts the path of a source folder from the path of a file.
     * @param path the path of a file
     * @return the path of the source holder, or <code>null</code> the path is invalid
     */
    private String getSourcePath(String pathname) {
        if (projectName.length() == 0) {
            return null;
        }
        
        String key = Activator.getWorkspacePath() + "$" + projectName;
        String srcpath = sourcePathMap.get(key);
        if (srcpath != null) {
            return srcpath;
        }
        
        try {
            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
            IJavaProject javaProject = JavaCore.create(project);
            IPackageFragmentRoot[] packageFragmentRoot = javaProject.getAllPackageFragmentRoots();
            for (int i = 0; i < packageFragmentRoot.length; i++) {
                IPackageFragmentRoot packageRoot = packageFragmentRoot[i];
                if (packageRoot.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT &&
                    packageRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    
                    srcpath = packageRoot.getPath().toString();
                    if (pathname.startsWith(srcpath)) {
                        sourcePathMap.put(key, srcpath);
                        return srcpath;
                    }
                }
            }
        } catch (JavaModelException e) { /* empty */ }
        return null;
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
        buf.append(" resource=[" + projectName + "/" + packageName + "/" + getFileName() + "]");
        buf.append(" branch=[" + branch + "]");
        return buf.toString();
    }
}
