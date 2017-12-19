/*
 *  Copyright 2017
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
import java.util.Map;
import java.util.HashMap;

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
     * The cache of pairs of a project name and its source path.
     */
    private static Map<String, String> sourcePathMap = new HashMap<String, String>();
    
    /**
     * The string value that represents the extension of a Java file.
     */
    public static final String JAVA_FILE_EXT = ".java";
    
    /**
     * The string value that represents a Java default package.
     */
    public static final String JAVA_DEFAULT_PACKAGE = "(default package)";
    
    /**
     * Creates information about the path of a resource on which a macro was performed.
     * @param path the path of the resource
     * @param branch the branch of the resource
     */
    public MacroPath(String path, String branch) {
        this.path = path;
        this.branch = branch;
        this.projectName = getProjectName(path);
        this.packageName = getPackageName(projectName, path);
        this.fileName = getFileName(path);
    }
    
    /**
     * Returns the name of a project that contains the resource.
     * @return the project name
     */
    public String getProjectName() {
        return projectName;
    }
    
    /**
     * Extracts the name of a project from the path of a resource.
     * @param pathname the path of the resource
     * @return the project name, or an empty string if the path is invalid
     */
    private String getProjectName(String pathname) {
        if (pathname == null || pathname.length() == 0) {
            return "";
        }
        
        int index = pathname.indexOf(File.separatorChar, 1);
        if (index == -1) {
            return pathname.substring(1);
        }
        
        return pathname.substring(1, index);
    }
    
    /**
     * Returns the name of a package that contains the resource.
     * @return the package name
     */
    public String getPackageName() {
        return packageName;
    }
    
    /**
     * Returns the name of a package containing a resource on which this macro was performed.
     * @param pathname the path of the resource
     * @return the package name, or an empty string if the path is invalid
     */
    public String getPackageName(String projectName, String pathname) {
        if (pathname == null || pathname.length() == 0 || projectName.length() == 0) {
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
     * Returns the name of a file that contains the resource.
     * @return the filename
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Returns the name of a resource on which this macro was performed.
     * @param pathname the path of the resource
     * @return the resource name without its location information
     */
    private String getFileName(String pathname) {
        if (path == null || pathname.length()== 0 || !path.endsWith(JAVA_FILE_EXT)) {
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
}
