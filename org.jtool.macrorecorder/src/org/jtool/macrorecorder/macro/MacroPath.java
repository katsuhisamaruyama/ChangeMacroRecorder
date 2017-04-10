/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

public class MacroPath {
    
    /**
     * The path of a file on which this macro was performed.
     */
    private String fullPath;
    
    /**
     * The path of a source folder containing a file on which this macro was performed.
     */
    private String sourcePath;
    
    /**
     * The cache of pairs of a project name and its source path.
     */
    private Map<String, String> sourcePathMap = new HashMap<String, String>();
    
    /**
     * Creates an object storing information about the path of a file.
     * @param path the path of a file on which this macro was performed
     */
    public MacroPath(String path) {
        this.fullPath = path;
        this.sourcePath = getSourcePath(path);
    }
    
    /**
     * Returns the path of a file on which this macro was performed.
     * @return the path
     */
    public String getPath() {
        return fullPath;
    }
    
    /**
     * Returns the path of a source folder containing a file on which this macro was performed.
     * @return the path of the source holder
     */
    public String getSourcePath() {
        return sourcePath;
    }
    
    /**
     * Returns the name of a project containing a file on which this macro was performed.
     * @return the project name
     */
    public String getProjectName() {
        int firstIndex = fullPath.indexOf(File.separatorChar, 1);
        if (firstIndex == -1) {
            return "";
        }
        
        String name = fullPath.substring(1, firstIndex);
        return name;
    }
    
    /**
     * Extracts the package name from the path of a file.
     * @return the package name
     */
    public String getPackageName() {
        int firstIndex = sourcePath.length() + 1;
        int lastIndex = fullPath.lastIndexOf(File.separatorChar) + 1;
        if (firstIndex == -1 || lastIndex == -1) {
            return "";
        }
        
        if (firstIndex  == lastIndex) {
            return "(default package)";
        }
        
        String name = fullPath.substring(firstIndex, lastIndex - 1);
        name = name.replace(File.separatorChar, '.');
        return name;
    }
    
    /**
     * Extracts the file name from the path of a file.
     * @return the file name without its path information
     */
    public String getFileName() {
        int lastIndex = fullPath.lastIndexOf(File.separatorChar) + 1;
        if (lastIndex == -1) {
            return "";
        }
        
        String name = fullPath.substring(lastIndex);
        return name;
    }
    
    /**
     * Extracts the path of a source folder from the path of a file.
     * @param path the path of a file
     * @return the path of the source holder
     */
    private String getSourcePath(String path) {
        String projectName = getProjectName();
        
        String srcpath = sourcePathMap.get(projectName);
        if (srcpath != null) {
            return srcpath;
        }
        
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
        IJavaProject javaProject = JavaCore.create(project);
        
        try {
            IPackageFragmentRoot[] packageFragmentRoot = javaProject.getAllPackageFragmentRoots();
            for (int i = 0; i < packageFragmentRoot.length; i++) {
                IPackageFragmentRoot packageRoot = packageFragmentRoot[i];
                if (packageRoot.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT &&
                    packageRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    
                    srcpath = packageRoot.getPath().toString();
                    if (path.startsWith(srcpath)) {
                        sourcePathMap.put(projectName, srcpath);
                        return srcpath;
                    }
                }
            }
        } catch (JavaModelException e) { /* empty */ }
        return "";
    }
    
    /**
     * Returns the string for printing.
     * @return the string for printing
     */
    @Override
    public String toString() {
        return fullPath;
    }
}
