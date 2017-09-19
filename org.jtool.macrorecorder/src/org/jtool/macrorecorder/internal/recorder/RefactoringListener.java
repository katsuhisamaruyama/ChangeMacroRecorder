/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.RefactoringMacro;
import org.jtool.macrorecorder.macro.TriggerMacro;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptorProxy;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.history.IRefactoringExecutionListener;
import org.eclipse.ltk.core.refactoring.history.RefactoringExecutionEvent;
import org.eclipse.ltk.core.refactoring.history.IRefactoringHistoryListener;
import org.eclipse.ltk.core.refactoring.history.IRefactoringHistoryService;
import org.eclipse.ltk.core.refactoring.history.RefactoringHistoryEvent;
import java.util.Map;

/**
 * Listens refactoring events.
 * @author Katsuhisa Maruyama
 */
class RefactoringListener implements IRefactoringExecutionListener, IRefactoringHistoryListener {
    
    /**
     * A recorder that records global macros.
     */
    private GlobalMacroRecorder globalRecorder;
    
    /**
     * Creates an object that records refectoring execution events.
     * @param recorder a recorder that records global macros
     */
    RefactoringListener(GlobalMacroRecorder recorder) {
        this.globalRecorder = recorder;
    }
    
    /**
     * Registers a refactoring listener.
     */
    void register() {
        IRefactoringHistoryService rs = RefactoringCore.getHistoryService();
        if (rs != null) {
            rs.addExecutionListener(this);
        }
    }
    
    /**
     * Unregisters a refactoring listener.
     */
    void unregister() {
        IRefactoringHistoryService rs = RefactoringCore.getHistoryService();
        if (rs != null) {
            rs.removeExecutionListener(this);
        }
    }
    
    /**
     * Receives an event when a refactoring execution event happened.
     * @param event the refactoring execution event
     */
    @Override
    public void executionNotification(RefactoringExecutionEvent event) {
        String path = globalRecorder.getPathToBeRefactored();
        String branch = globalRecorder.getBranch(path);
        
        RefactoringDescriptorProxy refactoringDescriptorProxy = event.getDescriptor();
        RefactoringDescriptor refactoringDescriptor = refactoringDescriptorProxy.requestDescriptor(null);
        String refactoringId = refactoringDescriptor.getID();
        RefactoringContribution refactoringContribution = RefactoringCore.getRefactoringContribution(refactoringId);
        Map<String, String> argumentMap = refactoringContribution.retrieveArgumentMap(refactoringDescriptor);
        
        int eventType = event.getEventType();
        if (eventType == RefactoringExecutionEvent.ABOUT_TO_PERFORM ||
            eventType == RefactoringExecutionEvent.ABOUT_TO_UNDO ||
            eventType == RefactoringExecutionEvent.ABOUT_TO_REDO) {
            
            RefactoringMacro macro = new RefactoringMacro(getRefactoringAction(eventType),
                                         path, branch, refactoringId, argumentMap);
            
            DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
            if (docRecorder != null && docRecorder.isOn()) {
                macro.setSelectionStart(docRecorder.getSelectionStart());
                macro.setSelectionText(docRecorder.getSelectionText());
            }
            
            globalRecorder.recordMacro(macro);
            globalRecorder.setPathToBeRefactored(path);
            
            
            
            TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REFACTORING,
                                      path, branch, TriggerMacro.Timing.BEGIN);
            globalRecorder.recordTriggerMacro(tmacro);
            globalRecorder.setRefactoringInProgress(true);
            
        } else if (eventType == RefactoringExecutionEvent.PERFORMED ||
                   eventType == RefactoringExecutionEvent.UNDONE ||
                   eventType == RefactoringExecutionEvent.REDONE) {
            
            TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REFACTORING,
                                      path, branch, TriggerMacro.Timing.END);
            globalRecorder.recordTriggerMacro(tmacro);
            globalRecorder.setPathToBeRefactored(null);
            
            RefactoringMacro macro = new RefactoringMacro(getRefactoringAction(eventType),
                                         path, branch, refactoringId, argumentMap);
            globalRecorder.recordMacro(macro);
            globalRecorder.setRefactoringInProgress(false);
            
            DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
            if (docRecorder != null) {
                docRecorder.dispose();
            }
        }
    }
    
    /**
     * Returns the type of refactoring
     * @param eventType the type of the refactoring event
     * @return the type of refactoring
     */
    private RefactoringMacro.Action getRefactoringAction(int eventType) {
        if (eventType == RefactoringExecutionEvent.ABOUT_TO_PERFORM) {
            return RefactoringMacro.Action.ABOUT_TO_PERFORM;
            
        } else if (eventType == RefactoringExecutionEvent.ABOUT_TO_UNDO) {
            return RefactoringMacro.Action.ABOUT_TO_UNDO;
            
        } else if (eventType == RefactoringExecutionEvent.ABOUT_TO_REDO) {
            return RefactoringMacro.Action.ABOUT_TO_REDO;
            
        } else if (eventType == RefactoringExecutionEvent.PERFORMED) {
            return RefactoringMacro.Action.PERFORMED;
            
        } else if (eventType == RefactoringExecutionEvent.UNDONE) {
            return RefactoringMacro.Action.UNDONE;
            
        } else if (eventType == RefactoringExecutionEvent.REDONE) {
            return RefactoringMacro.Action.REDONE;
        }
        return RefactoringMacro.Action.NONE;
    }
    
    /**
     * Receives an event when a refactoring history event happened.
     * @param event the refactoring history event
     */
    @Override
    public void historyNotification(RefactoringHistoryEvent event) {
    }
}
