/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.CommandMacro;
import org.jtool.macrorecorder.macro.TriggerMacro;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;

/**
 * Listens command events (menu etc.).
 * @author Katsuhisa Maruyama
 */
class CommandListener implements IExecutionListener {
    
    /**
     * A recorder that records global macros.
     */
    private GlobalMacroRecorder globalRecorder;
    
    /**
     * A trigger macro that represents an executed refactoring.
     */
    private TriggerMacro refactoringBeginMacro;
    
    /**
     * Creates an object that records command execution events.
     * @param recorder a recorder that records global macros
     */
    CommandListener(GlobalMacroRecorder recorder) {
        this.globalRecorder = recorder;
    }
    
    /**
     * Registers a command listener.
     */
    void register() {
        ICommandService cs = (ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class);
        if (cs != null) {
            cs.addExecutionListener(this);
        }
    }
    
    /**
     * Unregisters a command listener.
     */
    void unregister() {
        ICommandService cs = (ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class);
        if (cs != null) {
            cs.removeExecutionListener(this);
        }
    }
    
    /**
     * Receives a command that is about to execute.
     * @param commandId the identifier of the command that is about to execute
     * @param event the event that will be passed to the <code>execute</code> method
     */
    @Override
    public void preExecute(String commandId, ExecutionEvent event) {
        String path = EditorUtilities.getActiveInputFilePath();
        if (path != null) {
            globalRecorder.setSelectedPath(path);
        } else {
            path = globalRecorder.getSelectedPath();
        }
        String branch = globalRecorder.getBranch(path);
        
        CommandMacro macro = new CommandMacro(CommandMacro.Action.EXECUTION, path, branch, commandId);
        globalRecorder.recordMacro(macro);
        
        DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
        if (docRecorder != null) {
            docRecorder.applyDiff(false);
        }
        
        beginRefactoring(commandId, event, path, branch);
        
        setInProgressAction(commandId, true);
    }
    
    /**
     * Receives a command that has failed to complete execution.
     * @param commandId the identifier of the command that has executed
     * @param returnValue the return value from the command; may be <code>null</code>.
     */
    @Override
    public void postExecuteSuccess(String commandId, Object returnValue) {
        endRefactoring(commandId);
        
        setInProgressAction(commandId, false);
    }
    
    /**
     * Receives a command that has completed execution successfully.
     * @param commandId the identifier of the command that has executed
     * @param returnValue the return value from the command
     */
    @Override
    public void postExecuteFailure(String commandId, ExecutionException exception) {
        endRefactoring(commandId);
        
        setInProgressAction(commandId, false);
    }
    
    /**
     * Sets the flag that indicate the state of an action.
     * @param commandId the identifier of the command that has executed
     * @param bool <code>true</code> if the action is currently performed, otherwise <code>false</code>
     */
    private void setInProgressAction(String commandId, boolean bool) {
        if (commandId.equals(IWorkbenchCommandConstants.EDIT_CUT)) {
            globalRecorder.setCutInProgress(true);
            
        } else if (commandId.equals(IWorkbenchCommandConstants.EDIT_PASTE)) {
            globalRecorder.setPasteInProgress(true);
        
        } else if (commandId.equals(IWorkbenchCommandConstants.FILE_SAVE) ||
                   commandId.equalsIgnoreCase(IWorkbenchCommandConstants.FILE_SAVE_ALL)) {
            globalRecorder.setSaveInProgress(true);
        }
    }
    
    /**
     * Records a trigger macro when this command macro represents a refactoring.
     * @param commandId the identifier of the command that is about to execute
     * @param event the event that will be passed to the <code>execute</code> method
     * @param path the path of a file or a package on which this macro was performed
     * @param branch the branch name of a file or a package on which this macro was performed
     */
    private void beginRefactoring(String commandId, ExecutionEvent event, String path, String branch) {
        refactoringBeginMacro = null;
        
        try {
            String id = event.getCommand().getCategory().getId();
            if (id.endsWith("category.refactoring")) {
                if (path != null) {
                    globalRecorder.setPathToBeRefactored(path);
                    globalRecorder.setSelectedPath(null);
                }
                
                TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REFACTORING,
                                          path, branch, TriggerMacro.Timing.BEGIN, commandId);
                DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
                if (docRecorder != null) {
                    docRecorder.recordMacro(tmacro);
                    refactoringBeginMacro = tmacro;
                }
            }
        } catch (NotDefinedException e) {
        }
    }
    
    /**
     * Records a trigger macro that represents the end of the executed refactoring.
     * @param commandId the identifier of the command that has executed
     */
    private void endRefactoring(String commandId) {
        if (refactoringBeginMacro != null) {
            String path = refactoringBeginMacro.getPath();
            String branch = refactoringBeginMacro.getBranch();
            
            TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REFACTORING,
                                      path, branch, TriggerMacro.Timing.END, commandId);
            DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
            if (docRecorder != null) {
                docRecorder.recordMacro(tmacro);
                globalRecorder.setPathToBeRefactored(null);
            }
        }
        
        refactoringBeginMacro = null;
    }
    
    /**
     * Receives a command with no handler.
     * @param commandId the identifier of command that is not handled
     * @param exception the exception that occurred
     */
    @Override
    public void notHandled(String commandId, NotHandledException exception) {
    }
}
