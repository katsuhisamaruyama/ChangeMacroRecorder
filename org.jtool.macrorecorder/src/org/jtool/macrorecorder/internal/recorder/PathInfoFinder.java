/*
 *  Copyright 2017-2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.MacroPath;
import org.jtool.macrorecorder.Activator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import java.util.Map;
import java.util.HashMap;

/**
 * Finds path information about a resource.
 * @author Katsuhisa Maruyama
 */
public class PathInfoFinder {
    
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
     * The filename-separator character.
     * On both UNIX and Windows systems, the value of this field is <code>'/'</code>.
     */
    public static final char separatorChar = '/';
    
    /**
     * Prohibits an object.
     */
    private PathInfoFinder() {
    }
    
    /**
     * Obtains the path information of a resource.
     * @param path the path of the resource
     * @param branch the branch of the resource
     */
    public static MacroPath getMacroPath(String path, String branch) {
        if (path.charAt(0) != separatorChar) {
            path = separatorChar + path;
        }
        
        String projectName = getProjectName(path);
        String packageName = getPackageName(projectName, path);
        String fileName = getFileName(path);
        return new MacroPath(path, branch, projectName, packageName, fileName);
    }
    
    /**
     * Extracts the name of a project from the path of a resource.
     * @param path the path of the resource
     * @return the project name of the resource, or an empty string if the path is invalid
     */
    private static String getProjectName(String path) {
        if (path == null || path.length() == 0) {
            return "";
        }
        
        int index = path.indexOf(separatorChar, 1);
        if (index == -1) {
            return path.substring(1);
        }
        
        return path.substring(1, index);
    }
    
    /**
     * Returns the name of a package containing a resource.
     * @param projectName the name of the resource
     * @param path the path of the resource
     * @return the package name of the resource, or an empty string if the path is invalid
     */
    public static String getPackageName(String projectName, String path) {
        if (path == null || path.length() == 0 || projectName == null || projectName.length() == 0) {
            return "";
        }
        
        int index = path.lastIndexOf(separatorChar);
        if (index == -1 || index == 0) {
            return "";
        }
        
        if (path.endsWith(JAVA_FILE_EXT)) {
            path = path.substring(0, index);
        }
        String srcpath = getSourcePath(projectName, path);
        if (srcpath == null) {
            return "";
        }
        path = path.substring(srcpath.length());
        if (path.length() == 0) {
            return JAVA_DEFAULT_PACKAGE;
        }
        
        index = path.lastIndexOf(separatorChar);
        path = path.substring(1);
        return path.replace(separatorChar, '.');
    }
    
    /**
     * Returns the name of a file corresponding to a resource.
     * @param path the path of the resource
     * @return the file name of the resource without its location information
     */
    private static String getFileName(String path) {
        if (path == null || path.length() == 0 || !path.endsWith(JAVA_FILE_EXT)) {
            return "";
        }
        
        int index = path.lastIndexOf(separatorChar);
        if (index == -1) {
            return "";
        }
        
        return path.substring(index + 1);
    }
    
    /**
     * Extracts the path of a source folder from the path of a file.
     * @param projectName the name of the resource
     * @param path the path of a file
     * @return the path of the source holder, or <code>null</code> the path is invalid
     */
    private static String getSourcePath(String projectName, String path) {
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
                    if (path.startsWith(srcpath)) {
                        sourcePathMap.put(key, srcpath);
                        return srcpath;
                    }
                }
            }
        } catch (JavaModelException e) { /* empty */ }
        return null;
    }
}
