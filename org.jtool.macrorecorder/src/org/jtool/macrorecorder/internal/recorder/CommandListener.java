/*
 *  Copyright 2017-2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.CommandMacro;
import org.jtool.macrorecorder.macro.TriggerMacro;
import org.jtool.macrorecorder.macro.MacroPath;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jdt.core.ICompilationUnit;

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
     * A flag that indicates if the refactoring is about to execute.
     */
    private boolean isRefactoring = false;
    
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
     * @param event the received event
     */
    @Override
    public void preExecute(String commandId, ExecutionEvent event) {
        String path = getPath();
        String branch = globalRecorder.getBranch(path);
        
        if (path != null && isCommandToBeRecorded(event)) {
            CommandMacro macro = new CommandMacro(CommandMacro.Action.EXECUTION, PathInfoFinder.getMacroPath(path, branch), commandId);
            globalRecorder.recordCommandMacro(macro);
            
            DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
            if (docRecorder != null) {
                docRecorder.applyDiff(false);
            }
            
            checkRefactoringBegin(event, macro);
        }
        
        setInProgressAction(commandId, true);
    }
    
    /**
     * Obtains the path of a file on which a macro was performed.
     * @return the path of the macro, or <code>null</code> if target resource is not a file or unknown
     */
    private String getPath() {
        String path = EditorUtilities.getActiveInputFilePath();
        if (path != null) {
            return path;
        }
        
        IWorkbenchWindow window = EditorUtilities.getActiveWorkbenchWindow();
        if (window != null) {
            ISelection sel = window.getSelectionService().getSelection();
            if (sel instanceof IStructuredSelection) {
                Object elem = ((IStructuredSelection)sel).getFirstElement();
                if (elem instanceof ICompilationUnit) {
                    ICompilationUnit cu = (ICompilationUnit)elem;
                    return cu.getPath().toString();
                }
            }
        }
        return null;
    }
    
    /**
     * Tests if this command will be recorded. 
     * @param event event the execution event
     * @return <code>true</code> if this command will be recorded, otherwise <code>false</code>
     */
    private boolean isCommandToBeRecorded(ExecutionEvent event) {
        try {
            String commandCategory = event.getCommand().getCategory().getId();
            if (commandCategory.endsWith("category.file") ||
                commandCategory.endsWith("category.edit") ||
                commandCategory.endsWith("category.refactoring")) {
                return true;
            }
        } catch (NotDefinedException e) { /* empty */ }
        return false;
    }
    
    /**
     * Tests if this command is related to refactoring. 
     * @param event event the execution event
     * @return <code>true</code> if this command is related to refactoring, otherwise <code>false</code>
     */
    private boolean isRefactoringCommand(ExecutionEvent event) {
        try {
            String commandCategory = event.getCommand().getCategory().getId();
            if (commandCategory.endsWith("category.refactoring")) {
                return true;
            }
        } catch (NotDefinedException e) { /* empty */ }
        return false;
    }
    
    /**
     * Records a trigger macro that represents the beginning of a refactoring so as to detect document macros to be canceled.
     * @param event event the execution event
     * @param macro the macro for the command that is about to execute
     */
    private void checkRefactoringBegin(ExecutionEvent event, CommandMacro macro) {
        if (isRefactoringCommand(event)) {
            isRefactoring = true;
            
            String path = macro.getPath();
            if (path != null) {
                globalRecorder.setPathToBeRefactored(path);
            }
            
            macro.setAction(CommandMacro.Action.REFACTORING.toString());
            TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REFACTORING, macro.getMacroPath(), TriggerMacro.Timing.BEGIN, macro);
            globalRecorder.recordTriggerMacro(tmacro);
        }
    }
    
    /**
     * Receives a command that has failed to complete execution.
     * @param commandId the identifier of the command that has executed
     * @param returnValue the return value from the command; may be <code>null</code>.
     */
    @Override
    public void postExecuteSuccess(String commandId, Object returnValue) {
        setInProgressAction(commandId, false);
        
        if (isRefactoring && !globalRecorder.getRefactoringInProgress()) {
            isRefactoring = false;
            
            String path = globalRecorder.getPathToBeRefactored();
            String branch = globalRecorder.getBranch(path);
            MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
            TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REFACTORING, mpath, TriggerMacro.Timing.CANCEL); 
            globalRecorder.recordTriggerMacro(tmacro);
        }
    }
    
    /**
     * Receives a command that has completed execution successfully.
     * @param commandId the identifier of the command that has executed
     * @param returnValue the return value from the command
     */
    @Override
    public void postExecuteFailure(String commandId, ExecutionException exception) {
        setInProgressAction(commandId, false);
        isRefactoring = false;
    }
    
    /**
     * Sets the flag that indicate the state of an action.
     * @param commandId the identifier of the command that has executed
     * @param bool <code>true</code> if the action is currently performed, otherwise <code>false</code>
     */
    private void setInProgressAction(String commandId, boolean bool) {
        if (commandId.equals(IWorkbenchCommandConstants.EDIT_CUT)) {
            globalRecorder.setCutInProgress(bool);
            
        } else if (commandId.equals(IWorkbenchCommandConstants.EDIT_PASTE)) {
            globalRecorder.setPasteInProgress(bool);
        
        } else if (commandId.equals(IWorkbenchCommandConstants.FILE_SAVE) ||
                   commandId.equalsIgnoreCase(IWorkbenchCommandConstants.FILE_SAVE_ALL)) {
            globalRecorder.setSaveInProgress(bool);
        }
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
