/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.FileMacro;
import org.jtool.macrorecorder.macro.GitMacro;
import org.jtool.macrorecorder.macro.TriggerMacro;
import org.eclipse.core.resources.IProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.events.IndexChangedListener;
import org.eclipse.jgit.events.RefsChangedListener;
import org.eclipse.jgit.events.RefsChangedEvent;
import org.eclipse.jgit.events.IndexChangedEvent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import java.io.File;
import java.io.IOException;

/**
 * Listeners events occurred in git repositories.
 * @author Katsuhisa Maruyama
 */
class GitRepositoryListener implements RefsChangedListener, IndexChangedListener {
    
    /**
     * A recorder that records global macros.
     */
    private GlobalMacroRecorder globalRecorder;
    
    /**
     * Creates an object that records git events.
     * @param recorder a recorder that records global macros
     * @param projects the projects to be checked
     */
    GitRepositoryListener(GlobalMacroRecorder recorder, IProject[] projects) {
        this.globalRecorder = recorder;
        
        for (IProject project : projects) {
            registerGitProject(project);
        }
    }
    
    /**
     * Registers a project if it is git one.
     * @param project the project to be checked
     */
    void registerGitProject(IProject project) {
        try {
            String dir = project.getLocation().removeLastSegments(1).makeAbsolute().toOSString();
            Git git = Git.open(new File(dir));
            
            String branch = git.getRepository().getBranch();
            String path = extractLastPathElement(dir);
            if (path != null) {
                globalRecorder.putGitProject(path, branch);
            }
        } catch (IOException e) { /* empty */ }
    }
    
    /**
     * Registers a git repository listener.
     */
    void register() {
        Repository.getGlobalListenerList().addRefsChangedListener(this);
        Repository.getGlobalListenerList().addIndexChangedListener(this);
    }
    
    /**
     * Unregisters a git repository listener.
     */
    void unregister() {
    }
    
    /**
     * Receives an event when any reference changes.
     * @param event the event about the changes
     */
    @Override
    public void onRefsChanged(RefsChangedEvent event) {
        Repository repository = event.getRepository();
        Git git = Git.wrap(repository);
        GitMacro macro = createGitMacro(GitMacro.Action.REFS_CHANGE, git);
        if (macro != null) {
            globalRecorder.recordMacro(macro);
        }
    }
    
    /**
     * Receives an event when any change is made to the index.
     * @param event the event about the changes
     */
    @Override
    public void onIndexChanged(IndexChangedEvent event) {
        try {
            Repository repository = event.getRepository();
            Git git = Git.wrap(repository);
            GitMacro macro = createGitMacro(GitMacro.Action.INDEX_CHANGE, git);
            if (macro != null) {
                globalRecorder.recordMacro(macro);
                
                TriggerMacro bmacro = new TriggerMacro(TriggerMacro.Action.GIT,
                        PathInfoFinder.getMacroPath(macro.getPath(), macro.getBranch()), TriggerMacro.Timing.BEGIN);
                globalRecorder.recordTriggerMacro(bmacro);
                Status status = git.status().call();
                String branch = macro.getBranch();
                for (String path : status.getAdded()) {
                    FileMacro fmacro = new FileMacro(FileMacro.Action.ADDED_GIT_INDEX_CHANGED,
                            PathInfoFinder.getMacroPath(path, branch), "", "UTF-8");
                    globalRecorder.recordMacro(fmacro);
                }
                for (String path : status.getRemoved()) {
                    FileMacro fmacro = new FileMacro(FileMacro.Action.REMOVED_GIT_INDEX_CHANGED,
                            PathInfoFinder.getMacroPath(path, branch), "", "UTF-8");
                    globalRecorder.recordMacro(fmacro);
                }
                for (String path : status.getModified()) {
                    FileMacro fmacro = new FileMacro(FileMacro.Action.MODIFIED_GIT_INDEX_CHANGED,
                            PathInfoFinder.getMacroPath(path, branch), "", "UTF-8");
                    globalRecorder.recordMacro(fmacro);
                }
                TriggerMacro emacro = new TriggerMacro(TriggerMacro.Action.GIT,
                        PathInfoFinder.getMacroPath(macro.getPath(), macro.getBranch()), TriggerMacro.Timing.END);
                globalRecorder.recordTriggerMacro(emacro);
            }
        } catch (NoWorkTreeException e) { /* empty */
        } catch (GitAPIException e) { /* empty */ }
    }
    
    /**
     * Creates a macro corresponding to a git event.
     * @param action the action of the created git macro
     * @param git a git repository
     * @return the created git macro, or <code>null</code> if the macro creation failed
     */
    private GitMacro createGitMacro(GitMacro.Action action, Git git) {
        try {
            String branch = git.getRepository().getBranch();
            String gitPath = git.getRepository().getDirectory().getAbsolutePath();
            String dir = removeLastPathElement(gitPath);
            String path = extractLastPathElement(dir);
            if (path == null || dir == null) {
                return null;
            }
            return new GitMacro(action, PathInfoFinder.getMacroPath(path, branch), dir);
        } catch (IOException e) { /* empty */ }
        return null;
    }
    
    /**
     * Removes a path element on the last and returns it.
     * @param path the original path
     * @return the path after the removal, or <code>null</code> if the original path is invalid
     */
    private String removeLastPathElement(String path) {
        if (path == null) {
            return null;
        }
        
        int lastIndexOf = path.lastIndexOf(File.separator);
        if (lastIndexOf != -1) {
            return path.substring(0, lastIndexOf);
        }
        return null;
    }
    
    /**
     * Extracts a path elements on the first and returns it.
     * @param path the original path
     * @return the path after the extraction, or <code>null</code> if the original path is invalid
     */
    private String extractLastPathElement(String path) {
        if (path == null) {
            return null;
        }
        
        int lastIndexOf = path.lastIndexOf(File.separator);
        if (lastIndexOf != -1) {
            return path.substring(lastIndexOf + 1);
        }
        return null;
    }
}
