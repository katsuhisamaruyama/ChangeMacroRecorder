/*
 *  Copyright 2016
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.CodeCompletionMacro;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistantExtension2;

/**
 * Listens code completion events (quick assist or content assist).
 * @author Katsuhisa Maruyama
 */
class CodeCompletionListener implements ICompletionListener {
    
    /**
     * A recorder that records document macros.
     */
    private DocMacroRecorder docRecorder;
    
    /**
     * Creates an object that records code completion events.
     * @param recorder a recorder that records macros
     */
    CodeCompletionListener(DocMacroRecorder recorder) {
        this.docRecorder = recorder;
    }
    
    /**
     * Receives an event when code assist is invoked.
     * @param event the content assist event
     */
    @Override
    public void assistSessionStarted(ContentAssistEvent event) {
        if (event.assistant == null) {
            return;
        }
        
        String path = docRecorder.getPath();
        String branch = docRecorder.getGlobalMacroRecorder().getBranch(path);
        String commandId = event.assistant.getClass().getCanonicalName();
        CodeCompletionMacro.Action action = CodeCompletionMacro.Action.NONE;
        if (event.assistant instanceof IQuickAssistAssistant) {
            action = CodeCompletionMacro.Action.QUICK_ASSIST_BEGIN;
        } else if (event.assistant instanceof IContentAssistantExtension2) {
            action = CodeCompletionMacro.Action.CONTENT_ASSIST_BEGIN;
        }
        
        CodeCompletionMacro cmacro = new CodeCompletionMacro(action, path, branch, commandId);
        docRecorder.recordCodeCompletionMacro(cmacro);
    }
    
    /**
     * Receives an event when a code assist session ends.
     * @param event the content assist event
     */
    @Override
    public void assistSessionEnded(ContentAssistEvent event) {
        if (event.assistant == null) {
            return;
        }
        
        String path = docRecorder.getPath();
        String branch = docRecorder.getGlobalMacroRecorder().getBranch(path);
        String commandId = event.assistant.getClass().getCanonicalName();
        CodeCompletionMacro.Action action = CodeCompletionMacro.Action.NONE;
        if (event.assistant instanceof IQuickAssistAssistant) {
            action = CodeCompletionMacro.Action.QUICK_ASSIST_END;
        } else if (event.assistant instanceof IContentAssistantExtension2) {
            action = CodeCompletionMacro.Action.CONTENT_ASSIST_END;
        }
        
        CodeCompletionMacro cmacro = new CodeCompletionMacro(action, path, branch, commandId);
        docRecorder.recordCodeCompletionMacro(cmacro);
    }
    
    /**
     * Receives information when the selection in the proposal pop-up is changed or if the insert-mode changed.
     * @param proposal the newly selected proposal, possibly <code>null</code>
     * @param smartToggle <code>true</code> if the insert-mode toggle is being pressed, otherwise <code>false</code>
     */
    @Override
    public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
    }
}
