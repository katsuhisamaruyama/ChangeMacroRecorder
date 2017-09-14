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
            String ppath = project.getLocation().makeAbsolute().toOSString();
            Git git;
            try {
                git = Git.open(new File(ppath));
                GitMacro macro = createGitMacro(GitMacro.Action.OPEN, git);
                if (macro != null) {
                    globalRecorder.recordMacro(macro);
                    globalRecorder.putGitProject(ppath, git.getRepository().getBranch());
                }
            } catch (IOException e) { /* empty */ }
        }
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
                        macro.getPath(), macro.getBranch(), TriggerMacro.Timing.BEGIN);
                globalRecorder.recordTriggerMacro(bmacro);
                Status status = git.status().call();
                String branch = macro.getBranch();
                for (String path : status.getAdded()) {
                    FileMacro fmacro = new FileMacro(FileMacro.Action.ADDED_GIT_INDEX_CHANGED, path, branch, "", "UTF-8");
                    globalRecorder.recordMacro(fmacro);
                }
                for (String path : status.getRemoved()) {
                    FileMacro fmacro = new FileMacro(FileMacro.Action.REMOVED_GIT_INDEX_CHANGED, path, branch, "", "UTF-8");
                    globalRecorder.recordMacro(fmacro);
                }
                for (String path : status.getModified()) {
                    FileMacro fmacro = new FileMacro(FileMacro.Action.MODIFIED_GIT_INDEX_CHANGED, path, branch, "", "UTF-8");
                    globalRecorder.recordMacro(fmacro);
                }
                TriggerMacro emacro = new TriggerMacro(TriggerMacro.Action.GIT,
                        macro.getPath(), macro.getBranch(), TriggerMacro.Timing.END);
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
            String path = removeLastPathElement(gitPath);
            if (path == null) {
                return null;
            }
            return new GitMacro(action, path, branch);
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
            path.substring(0, lastIndexOf);
        }
        return null;
    }
}
