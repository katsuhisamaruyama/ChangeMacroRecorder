/*
 *  Copyright 2016-2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.ResourceMacro;
import org.jtool.macrorecorder.macro.DocumentMacro;
import org.jtool.macrorecorder.macro.FileMacro;
import org.jtool.macrorecorder.macro.MacroPath;
import org.jtool.macrorecorder.recorder.MacroConsole;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.IJavaElement;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Listens resource change events.
 * @author Katsuhisa Maruyama
 */
class ResourceListener implements IResourceChangeListener {
    
    /**
     * A recorder that records global macros.
     */
    private GlobalMacroRecorder globalRecorder;
    
    /**
     * Creates an object that records resource change events.
     * @param recorder a recorder that records global macros
     */
    ResourceListener(GlobalMacroRecorder recorder) {
        this.globalRecorder = recorder;
    }
    
    /**
     * Registers a resource change listener.
     */
    void register() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }
    
    /**
     * Unregisters a resource change listener.
     */
    void unregister() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }
    
    /**
     * Receives an event when a resource has changed. 
     * @param event the resource change event
     */
    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
            
            IResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
            try {
                event.getDelta().accept(visitor);
            } catch (CoreException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    /**
     * Visits resource change deltas.
     */
    class ResourceDeltaVisitor implements IResourceDeltaVisitor {
        
        /**
         * Visits the given resource delta.
         */
        @Override
        public boolean visit(IResourceDelta delta) {
            IResource resource = delta.getResource();
            String path = resource.getFullPath().toString();
            ResourceMacro.Target target = getTarget(resource);
            
            if (path != null && target != null) {
                if (delta.getKind() == IResourceDelta.ADDED) {
                    recordResourceAddedMacro(delta, path, target);
                    
                } else if (delta.getKind() == IResourceDelta.REMOVED) {
                    recordResourceRemovedMacro(delta, path, target);
                    
                } else if (delta.getKind() == IResourceDelta.CHANGED) {
                    recordResourceChangedMacro(delta, path, target);
                    
                    if (resource.getType() == IResource.FILE) {
                        if (globalRecorder.getSaveInProgress()) { 
                            String branch = globalRecorder.getBranch(path);
                            String code = getCurrentCode(resource);
                            String charset = getCharset(resource);
                            
                            FileMacro macro = new FileMacro(FileMacro.Action.SAVED,
                                    PathInfoFinder.getMacroPath(path, branch), code, charset);
                            globalRecorder.recordMacro(macro);
                            
                        } else if (globalRecorder.getRefactoringInProgress()) {
                            String branch = globalRecorder.getBranch(path);
                            String code = getCurrentCode(resource);
                            String charset = getCharset(resource);
                            
                            FileMacro macro = new FileMacro(FileMacro.Action.REFACTORED,
                                    PathInfoFinder.getMacroPath(path, branch), code, charset);
                            globalRecorder.recordMacro(macro);
                        }
                    }
                }
            }
            
            return true;
        }
    }
    
    /**
     * Records a macro corresponding to the addition of a resource.
     * @param delta the resource delta
     * @param path the path of the the resource
     * @param target the kind of the target of the resource
     */
    private void recordResourceAddedMacro(IResourceDelta delta, String path, ResourceMacro.Target target) {
        if (target == ResourceMacro.Target.FILE) {
            recordFileResourceAddedMacro(delta, path, target);
        } else {
            recordNonFileResourceAddedMacro(delta, path, target);
        }
    }
    
    /**
     * Records a macro corresponding to the addition of a file resource.
     * @param delta the resource delta
     * @param path the path of the the resource
     * @param target the kind of the target of the resource
     */
    private void recordFileResourceAddedMacro(IResourceDelta delta, String path, ResourceMacro.Target target) {
        String branch = globalRecorder.getBranch(path);
        MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
        
        IResource resource = delta.getResource();
        String code = getCurrentCode(resource);
        String charset = getCharset(resource);
        
        ResourceMacro rmacro = null;
        FileMacro.Action ftype = null;
        String fromPath = path;
        if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
            fromPath = delta.getMovedFromPath().toString();
            if (getName(path).equals(getName(fromPath))) {
                rmacro = new ResourceMacro(ResourceMacro.Action.MOVED_FROM, mpath, target, fromPath);
                ftype = FileMacro.Action.MOVED_FROM;
                
            } else {
                rmacro = new ResourceMacro(ResourceMacro.Action.RENAMED_FROM, mpath, target, fromPath);
                ftype = FileMacro.Action.RENAMED_FROM;
            }
            
        } else {
            rmacro = new ResourceMacro(ResourceMacro.Action.ADDED, mpath, target, path);
            ftype = FileMacro.Action.ADDED;
        }
        
        globalRecorder.recordMacro(rmacro);
        
        DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
        if (docRecorder == null) {
            docRecorder = globalRecorder.getRecorder().off((IFile)resource);
        }
        
        if (ftype == FileMacro.Action.MOVED_FROM || ftype == FileMacro.Action.RENAMED_FROM) {
            String preCode = getPrevCode(resource);
            String curCode = getCurrentCode(resource);
            
            FileMacro fmacro = new FileMacro(ftype, mpath, preCode, charset, fromPath);
            globalRecorder.recordMacro(fmacro);
            
            docRecorder.setPreCode(preCode);
            docRecorder.applyDiff(curCode, true);
            
        } else {
            FileMacro fmacro = new FileMacro(ftype, mpath, "", charset, path);
            globalRecorder.recordMacro(fmacro);
            
            docRecorder.setPreCode("");
            DocumentMacro dmacro = new DocumentMacro(DocumentMacro.Action.EDIT, mpath, 0, code, "");
            docRecorder.recordDocumentMacro(dmacro);
            
            docRecorder.applyDiff(false);
        }
    }
    
    /**
     * Records a macro corresponding to the addition of a non-file resource.
     * @param delta the resource delta
     * @param path the path of the the resource
     * @param target the kind of the target of the resource
     */
    private void recordNonFileResourceAddedMacro(IResourceDelta delta, String path, ResourceMacro.Target target) {
        String branch = globalRecorder.getBranch(path);
        MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
        
        ResourceMacro rmacro = null;
        if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
            String fromPath = delta.getMovedFromPath().toString();
            if (getName(path).equals(getName(fromPath))) {
                rmacro = new ResourceMacro(ResourceMacro.Action.MOVED_FROM, mpath, target, fromPath);
                
            } else {
                rmacro = new ResourceMacro(ResourceMacro.Action.RENAMED_FROM, mpath, target, fromPath);
            }
            
        } else {
            rmacro = new ResourceMacro(ResourceMacro.Action.ADDED, mpath, target, path);
        }
        
        globalRecorder.recordMacro(rmacro);
    }
    
    /**
     * Records a macro corresponding to the addition of a resource.
     * @param delta delta the resource delta
     * @param path the path of the the resource
     * @param target the kind of the target of the resource
     */
    private void recordResourceRemovedMacro(IResourceDelta delta, String path, ResourceMacro.Target target) {
        if (target == ResourceMacro.Target.FILE) {
            recordFileResourceRemovedMacro(delta, path, target);
        } else {
            recordNonFileResourceRemovedMacro(delta, path, target);
        }
    }
    
    /**
     * Records a macro corresponding to the removal of a file resource.
     * @param delta delta the resource delta
     * @param path the path of the the resource
     * @param target the kind of the target of the resource
     */
    private void recordFileResourceRemovedMacro(IResourceDelta delta, String path, ResourceMacro.Target target) {
        String branch = globalRecorder.getBranch(path);
        MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
        
        IResource resource = delta.getResource();
        String charset = getCharset(resource);
        
        ResourceMacro rmacro = null;
        FileMacro.Action ftype = null;
        String toPath = path;
        if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
            toPath = delta.getMovedToPath().toString();
            if (getName(path).equals(getName(toPath))) {
                rmacro = new ResourceMacro(ResourceMacro.Action.MOVED_TO, mpath, target, toPath);
                ftype = FileMacro.Action.MOVED_TO;
                
            } else {
                rmacro = new ResourceMacro(ResourceMacro.Action.RENAMED_TO, mpath, target, toPath);
                ftype = FileMacro.Action.RENAMED_TO;
            }
            
        } else {
            rmacro = new ResourceMacro(ResourceMacro.Action.REMOVED, mpath, target, path);
            ftype = FileMacro.Action.REMOVED;
        }
        
        DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
        if (docRecorder == null) {
            docRecorder = globalRecorder.getRecorder().off((IFile)resource);
        }
        docRecorder.setPreCode("");
        docRecorder.willDispose();
        
        globalRecorder.recordMacro(rmacro);
        
        FileMacro fmacro = new FileMacro(ftype, mpath, "", charset, toPath);
        globalRecorder.recordMacro(fmacro);
    }
    
    /**
     * Records a macro corresponding to the removal of a non-file resource.
     * @param delta delta the resource delta
     * @param path the path of the the resource
     * @param target the kind of the target of the resource
     */
    private void recordNonFileResourceRemovedMacro(IResourceDelta delta, String path, ResourceMacro.Target target) {
        String branch = globalRecorder.getBranch(path);
        MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
        
        ResourceMacro rmacro = null;
        if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
            String toPath = delta.getMovedToPath().toString();
            if (getName(path).equals(getName(toPath))) {
                rmacro = new ResourceMacro(ResourceMacro.Action.MOVED_TO, mpath, target, toPath);
                
            } else {
                rmacro = new ResourceMacro(ResourceMacro.Action.RENAMED_TO, mpath, target, toPath);
            }
            
        } else {
            rmacro = new ResourceMacro(ResourceMacro.Action.REMOVED, mpath, target, path);
        }
        
        globalRecorder.recordMacro(rmacro);
    }
    
    /**
     * Records a macro corresponding to the change of a resource.
     * @param delta delta the resource delta
     * @param path the path of the the resource
     * @param target the kind of the target of the resource
     */
    private void recordResourceChangedMacro(IResourceDelta delta, String path, ResourceMacro.Target target) {
        if (target == ResourceMacro.Target.FILE && (delta.getFlags() & IResourceDelta.CONTENT) != 0) {
            recordFileResourceChangedMacro(delta, path, target);
        }
    }
    
    /**
     * Records a macro corresponding to the change of a file resource.
     * @param delta delta the resource delta
     * @param path the path of the the resource
     * @param target the kind of the target of the resource
     */
    private void recordFileResourceChangedMacro(IResourceDelta delta, String path, ResourceMacro.Target target) {
        String branch = globalRecorder.getBranch(path);
        MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
        
        IResource resource = delta.getResource();
        DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
        if (docRecorder == null) {
            docRecorder = globalRecorder.getRecorder().off((IFile)resource);
        } else {
            if (docRecorder.isOn()) {
                return;
            }
        }
        
        String preCode = getPrevCode(resource);
        String curCode = getCurrentCode(resource);
        String charset = getCharset(resource);
        
        if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
            preCode = docRecorder.getPreCode();
        }
        
        docRecorder.setPreCode(preCode);
        
        ResourceMacro rmacro = new ResourceMacro(ResourceMacro.Action.CHANGED, mpath, target, path);
        globalRecorder.recordMacro(rmacro);
        
        FileMacro fmacro = new FileMacro(FileMacro.Action.CONTENT_CHANGED, mpath, curCode, charset);
        globalRecorder.recordMacro(fmacro);
        
        docRecorder.applyDiff(curCode, true);
    }
    
    /**
     * Returns the kind of the target of the resource.
     * @param resource the resource
     * @return the target of the resource, or <code>null</code> if the target is not either a project, package, or file
     */
    private ResourceMacro.Target getTarget(IResource resource) {
        if (resource == null) {
            return null;
        }
        
        IJavaElement elem = JavaCore.create(resource);
        if (elem == null) {
            return null;
        }
        
        int type = elem.getElementType();
        if (type == IJavaElement.JAVA_PROJECT) {
            globalRecorder.checkGitProject(elem.getJavaProject().getProject());
            return ResourceMacro.Target.PROJECT;
            
        } else if (type == IJavaElement.PACKAGE_FRAGMENT) {
            return ResourceMacro.Target.PACKAGE;
            
        } else if (type == IJavaElement.COMPILATION_UNIT) {
            return ResourceMacro.Target.FILE;
        }
        return null;
    }
    
    /**
     * Obtains the contents of a file resource.
     * @param resource the file resource
     * @return the contents of the file resource, or <code>null</code> if the resource is not a file
     */
    private String getCurrentCode(IResource resource) {
        if (resource.getType() == IResource.FILE) {
            IFile file = (IFile)resource;
            
            try {
                InputStream is = file.getContents();
                return read(is);
            } catch (CoreException e) {
            }
        }
        return "";
    }
    
    /**
     * Obtains the previous contents of a file resource.
     * @param resource the file resource
     * @return the previous contents of the file resource, or <code>null</code> if the resource is not a file
     */
    private String getPrevCode(IResource resource) {
        try {
            if (resource.getType() == IResource.FILE) {
                IFile file = (IFile)resource;
                
                IFileState[] states = file.getHistory(null);
                if (states.length > 0) {
                    String content = read(states[0].getContents());
                    return content;
                } else {
                    MacroConsole.println("NO HISTORY " + file.getFullPath().toString());
                }
            }
        } catch (CoreException e) {
        }
        return "";
    }
    
    /**
     * Returns the name of a charset of a file resource.
     * @param elem the file resource
     * @return the name of a charset of the file resource, or <code>null</code> if the resource is not a file
     */
    private String getCharset(IResource resource) {
        if (resource.getType() == IResource.FILE) {
            IFile file = (IFile)resource;
            
            try {
                return file.getCharset();
            } catch (CoreException e) {
            }
        }
        return null;
    }
    
    /**
     * Returns the last segment of a path name
     * @param path the path name
     * @return  the last segment of the path name
     */
    private String getName(String path) {
        int sep = path.lastIndexOf(PathInfoFinder.separatorChar);
        if (sep != -1) {
            return path.substring(sep + 1);
        }
        return path;
    }
    
    /**
     * Reads the contents from an input stream.
     * @param input the input stream
     * @return the string that represents the contents
     */
    private String read(InputStream input) {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            StringBuilder builder = new StringBuilder();
            char[] buf = new char[1024];
            int num = 0;
            while (0 <= (num = buffer.read(buf))) {
                    builder.append(buf, 0, num);
            }
            return builder.toString();
        } catch (IOException e) {
        }
        return null;
    }
}
