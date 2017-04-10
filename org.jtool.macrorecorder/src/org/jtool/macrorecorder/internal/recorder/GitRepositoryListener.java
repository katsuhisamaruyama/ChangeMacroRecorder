/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.FileMacro;
import org.jtool.macrorecorder.macro.GitMacro;
import org.jtool.macrorecorder.macro.MacroPath;
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
     * Creates an object that records git events.
     * @param recorder a recorder that records global macros
     */
    GitRepositoryListener(GlobalMacroRecorder recorder, IProject[] projects) {
        this.globalRecorder = recorder;
        
        for (IProject project : projects) {
            String projectPath = project.getLocation().makeAbsolute().toOSString();
            try {
                Git git = Git.open(new File(projectPath));
                String gitPath = git.getRepository().getDirectory().getAbsolutePath();
                String repoPath = removeLastPathElement(gitPath);
                
                if (repoPath != null) {
                    GitMacro macro = createGitMacro(GitMacro.Action.OPEN, git);
                    globalRecorder.recordMacro(macro);
                    globalRecorder.putGitProject(repoPath, git.getRepository().getBranch());
                }
            } catch (IOException e) {
            }
        }
    }
    
    /**
     * Receives an event when any reference changes.
     * @param event the event about the changes
     */
    @Override
    public void onRefsChanged(RefsChangedEvent event) {
        Repository repository = event.getRepository();
        
        GitMacro macro = createGitMacro(GitMacro.Action.REFS_CHANGED, Git.wrap(repository));
        globalRecorder.recordMacro(macro);
    }
    
    /**
     * Receives an event when any change is made to the index.
     * @param event the event about the changes
     */
    @Override
    public void onIndexChanged(IndexChangedEvent event) {
        Repository repository = event.getRepository();
        
        GitMacro macro = createGitMacro(GitMacro.Action.INDEX_CHANGED, Git.wrap(repository));
        globalRecorder.recordMacro(macro);
        
        String branch = macro.getBranch();
        for (String fname : macro.getAddedFiles()) {
            FileMacro fmacro = new FileMacro(FileMacro.Action.GIT_ADDED, new MacroPath(fname), branch, "", "UTF-8");
            globalRecorder.recordMacro(fmacro);
        }
        for (String fname : macro.getRemovedFiles()) {
            FileMacro fmacro = new FileMacro(FileMacro.Action.GIT_REMOVED, new MacroPath(fname), branch, "", "UTF-8");
            globalRecorder.recordMacro(fmacro);
        }
        for (String fname : macro.getAddedFiles()) {
            FileMacro fmacro = new FileMacro(FileMacro.Action.GIT_MODIFIED, new MacroPath(fname), branch, "", "UTF-8");
            globalRecorder.recordMacro(fmacro);
        }
    }
    
    /**
     * Removes a path element on the last and returns it.
     * @param path the original path
     * @return the path after the removal
     */
    private String removeLastPathElement(String path) {
        if (path == null) {
            return null;
        }
        
        int lastIndexOf = path.lastIndexOf(File.separator);
        if (lastIndexOf != -1) {
            path.substring(0, lastIndexOf);
        }
        return null;
    }
    
    /**
     * Creates a macro corresponding to a git event.
     * @param action the action of the created git macro
     * @param git a git repository
     * @return the created git macro
     */
    private GitMacro createGitMacro(GitMacro.Action action, Git git) {
        try {
            String branch = git.getRepository().getBranch();
            Status status = git.status().call();
            String gitPath = git.getRepository().getDirectory().getAbsolutePath();
            String repoPath = removeLastPathElement(gitPath);
            
            return new GitMacro(action, new MacroPath(repoPath), branch,
                       status.getModified(), status.getAdded(), status.getRemoved());
        } catch (IOException e) {
        } catch (NoWorkTreeException e) {
        } catch (GitAPIException e) {
        }
        return null;
    }
}
