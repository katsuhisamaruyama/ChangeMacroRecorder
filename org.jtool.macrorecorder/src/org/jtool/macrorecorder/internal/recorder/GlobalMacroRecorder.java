/*
 *  Copyright 2016-2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.macro.MacroPath;
import org.jtool.macrorecorder.macro.CommandMacro;
import org.jtool.macrorecorder.macro.TriggerMacro;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import java.util.HashMap;
import java.util.Map;

/**
 * Records macros representing global actions occurring in the workspace.
 * @author Katsuhisa Maruyama
 */
class GlobalMacroRecorder {
    
    /**
     * A recorder that sends all kinds of macro events.
     */
    private Recorder recorder;
    
    /**
     * A listener that manages command execution events.
     */
    private CommandListener commandListener;
    
    /**
     * A listener that manages refactoring execution events.
     */
    private RefactoringListener refactoringListener;
    
    /**
     * A listener that manages events related to file operations.
     */
    private FileListener fileListener;
    
    /**
     * A listener that manages resource changes in the Java model.
     */
    private ResourceListener resourceListener;
    
    /**
     * A listener that manages resource changes in the Java model.
     */
    private GitRepositoryListener gitRepositoryListener;
    
    /**
     * The path name of a resource to be refactored.
     */
    private String pathToBeRefactored;
    
    /**
     * The command macro that was lastly performed.
     */
    private CommandMacro lastCommandMacro = null;
    
    /**
     * A flag that indicates the save action is currently progressed.
     */
    private boolean saveInProgress;
    
    /**
     * A flag that indicates the refactoring is currently progressed.
     */
    private boolean refactoringInProgress = false;
    
    /**
     * A flag that indicates the cut action is currently progressed.
     */
    private boolean cutInProgress = false;
    
    /**
     * A flag that indicates the paste action is currently progressed.
     */
    private boolean pasteInProgress = false;
    
    /**
     * The collection of projects in the git repository.
     */
    private Map<String, String> gitProjects = new HashMap<String, String>();
    
    /**
     * Creates an object that records global macros.
     * @param recorder the recorder
     */
    GlobalMacroRecorder(Recorder recorder) {
        this.recorder = recorder;
        
        commandListener = new CommandListener(this);
        refactoringListener = new RefactoringListener(this);
        fileListener = new FileListener(this);
        resourceListener = new ResourceListener(this);
        gitRepositoryListener = new GitRepositoryListener(this, ResourcesPlugin.getWorkspace().getRoot().getProjects());
    }
    
    /**
     * Returns the recorder that sends all kinds of macro events.
     * @return the recorder
     */
    Recorder getRecorder() {
        return recorder;
    }
    
    /**
     * Returns a recorder that records document macros related to a file.
     * @param path the path of the file
     * @return the recorder, or <code>null</code> if none
     */
    DocMacroRecorder getDocMacroRecorder(String path) {
        return recorder.getDocMacroRecorder(path);
    }
    
    /**
     * Starts the recording of menu actions.
     */
    void start() {
        commandListener.register();
        refactoringListener.register();
        fileListener.register();
        resourceListener.register();
        gitRepositoryListener.register();
        
        pathToBeRefactored = null;
        saveInProgress = false;
        refactoringInProgress = false;
    }
    
    /**
     * Stops the recording of menu actions.
     */
    void stop() {
        commandListener.unregister();
        refactoringListener.unregister();
        fileListener.unregister();
        resourceListener.unregister();
        gitRepositoryListener.unregister();
    }
    
    /**
     * Sets a resource to be refactored.
     * @param path the path name of the resource to be refactored
     */
    void setPathToBeRefactored(String path) {
        pathToBeRefactored = path;
    }
    
    /**
     * Returns a resource to be refactored.
     * @return the path name of the resource to be refactored
     */
    String getPathToBeRefactored() {
        return pathToBeRefactored;
    }
    
    /**
     * Cancels the refactoring being in execution.
     */
    void cancelRefactoring() {
        if (getRefactoringInProgress()) {
            String path = getPathToBeRefactored();
            String branch = getBranch(path);
            MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
            
            TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REFACTORING, mpath, TriggerMacro.Timing.CANCEL);
            recordTriggerMacro(tmacro);
            setRefactoringInProgress(false);
        }
    }
    
    /**
     * Sets the flag that indicates a save action is currently progressed.
     * @param bool <code>true</code> if the save action is currently progressed, otherwise <code>false</code>
     */
    void setSaveInProgress(boolean bool) {
        saveInProgress = bool;
    }
    
    /**
     * Returns the flag that indicates a save action is currently progressed.
     * @return <code>true</code> if the save action is currently progressed, otherwise <code>false</code>
     */
    boolean getSaveInProgress() {
        return saveInProgress;
    }
    
    /**
     * Sets the flag that indicates the refactoring is currently progressed.
     * @param bool <code>true</code> if the refactoring is currently progressed, otherwise <code>false</code>
     */
    void setRefactoringInProgress(boolean bool) {
        refactoringInProgress = bool;
    }
    
    /**
     * Returns the flag that indicates a refactoring is currently progressed.
     * @return <code>true</code> if the refactoring is currently progressed, otherwise <code>false</code>
     */
    boolean getRefactoringInProgress() {
        return refactoringInProgress;
    }
    
    /**
     * Sets the flag that indicates a cut action is currently progressed.
     * @param bool <code>true</code> if the cut action is currently progressed, otherwise <code>false</code>
     */
    void setCutInProgress(boolean bool) {
        cutInProgress = bool;
    }
    
    /**
     * Returns the flag that indicates a cut action is currently progressed.
     * @return <code>true</code> if the cut action is currently progressed, otherwise <code>false</code>
     */
    boolean getCutInProgress() {
        return cutInProgress;
    }
    
    /**
     * Sets the flag that indicates a paste action is currently progressed.
     * @param bool <code>true</code> if the paste action is currently progressed, otherwise <code>false</code>
     */
    void setPasteInProgress(boolean bool) {
        pasteInProgress = bool;
    }
    
    /**
     * Returns the flag that indicates a paste action is currently progressed.
     * @return <code>true</code> if the paste action is currently progressed, otherwise <code>false</code>
     */
    boolean getPasteInProgress() {
        return pasteInProgress;
    }
    
    /**
     * Sets the the path name of the project and its corresponding branch name.
     * @param path the path name of the project
     * @param branch the branch name of the project
     */
    void putGitProject(String ppath, String branch) {
        gitProjects.put(ppath, branch);
    }
    
    /**
     * Returns the name of a branch corresponding to a resource
     * @param path the path name of the resource
     * @return the branch name of the resource
     */
    String getBranch(String path) {
        String ppath = getProjectName(path);
        String branch = gitProjects.get(ppath);
        if (branch != null) {
            return branch;
        }
        return "";
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
        
        int index = pathname.indexOf(PathInfoFinder.separatorChar, 1);
        if (index == -1) {
            return pathname.substring(1);
        }
        
        return pathname.substring(1, index);
    }
    
    /**
     * Records a macro.
     * @param macro the macro to be recorded
     */
    void recordMacro(Macro macro) {
        String path = macro.getPath();
        DocMacroRecorder docRecorder = recorder.getDocMacroRecorder(path);
        if (docRecorder != null) {
            docRecorder.dumpLastDocumentMacro();
        }
        
        recorder.recordRawMacro(macro);
        recorder.recordMacro(macro);
    }
    
    /**
     * Records a resource macro.
     * @param macro the resource macro to be recorded
     */
    void recordCommandMacro(CommandMacro macro) {
        String path = macro.getPath();
        DocMacroRecorder docRecorder = recorder.getDocMacroRecorder(path);
        if (docRecorder != null) {
            docRecorder.dumpLastDocumentMacro();
            docRecorder.cancelCodeCompletion();
        }
        cancelRefactoring();
        
        recorder.recordRawMacro(macro);
        recorder.recordMacro(macro);
        lastCommandMacro = macro;
        
        if (macro.getCommandId().equals(IWorkbenchCommandConstants.EDIT_COPY) ||
            macro.getCommandId().equals("org.eclipse.jdt.ui.edit.text.java.copy.qualified.name")) {
            if (docRecorder != null) {
                docRecorder.recordCopyMacro(macro);
            }
        }
    }
    
    /**
     * Returns the command macro that was lastly performed.
     * @return the last command macro
     */
    CommandMacro getLastCommandMacro() {
        return lastCommandMacro;
    }
    
    /**
     * Records a trigger macro.
     * @param macro the trigger macro to be recorded
     */
    void recordTriggerMacro(TriggerMacro macro) {
        String path = macro.getPath();
        DocMacroRecorder docRecorder = recorder.getDocMacroRecorder(path);
        
        if (macro.cursorChanged()) {
            if (docRecorder != null) {
                docRecorder.cancelCodeCompletion();
            }
            cancelRefactoring();
        }
        
        if (docRecorder != null) {
            docRecorder.recordTriggerMacro(macro);
        } else {
            recorder.recordRawMacro(macro);
        }
        recorder.recordMacro(macro);
    }
    
    /**
     * Checks if a project is under git.
     * @param project the project to be checked
     */
    void checkGitProject(IProject project) {
        if (project.getLocation() != null) {
            gitRepositoryListener.registerGitProject(project);
        }
    }
    
    /**
     * Obtains the active page part in the workbench.
     * @return the active page
     */
    IWorkbenchPage getActivePage() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    }
}
