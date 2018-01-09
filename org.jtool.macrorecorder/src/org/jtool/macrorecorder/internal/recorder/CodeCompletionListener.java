/*
 *  Copyright 2017-2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.macro.CodeCompletionMacro;
import org.jtool.macrorecorder.macro.DocumentMacro;
import org.jtool.macrorecorder.macro.CompoundMacro;
import org.jtool.macrorecorder.macro.TriggerMacro;
import org.jtool.macrorecorder.macro.MacroPath;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionListenerExtension2;
import org.eclipse.jface.text.source.ContentAssistantFacade;
import org.eclipse.ui.IEditorPart;

/**
 * Listens code completion events (quick assist or content assist).
 * @author Katsuhisa Maruyama
 */
class CodeCompletionListener implements ICompletionListener, ICompletionListenerExtension2 {
    
    /**
     * A recorder that records document macros.
     */
    private DocMacroRecorder docRecorder;
    
    /**
     * A macro that represents the code completion.
     */
    private CodeCompletionMacro codeCompletionMacro;
    
    /**
     * A flag that indicates the content assist is active.
     */
    private boolean contentAssistActive = false;
    
    /**
     * Creates an object that records code completion events.
     * @param recorder a recorder that records macros
     */
    CodeCompletionListener(DocMacroRecorder recorder) {
        this.docRecorder = recorder;
    }
    
    /**
     * Registers a code completion execution manager with the editor.
     * @param editor the editor
     */
    void register(IEditorPart editor) {
        ContentAssistantFacade facade = EditorUtilities.getContentAssistantFacade(editor);
        if (facade != null) {
            facade.addCompletionListener(this);
        }
    }
    
    /**
     * Unregisters a code completion manager with the editor.
     * @param editor the editor
     */
    void unregister(IEditorPart editor) {
        ContentAssistantFacade facade = EditorUtilities.getContentAssistantFacade(editor);
        if (facade != null) {
            facade.removeCompletionListener(this);
        }
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
        MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
        String commandId = event.assistant.getClass().getCanonicalName();
        
        CodeCompletionMacro.Action action = CodeCompletionMacro.Action.CONTENT_ASSIST_BEGIN;
        codeCompletionMacro = new CodeCompletionMacro(action, PathInfoFinder.getMacroPath(path, branch), commandId);
        
        TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.CODE_COMPLETION, mpath, TriggerMacro.Timing.BEGIN);
        docRecorder.getGlobalMacroRecorder().recordTriggerMacro(tmacro);
        
        this.contentAssistActive = false;
    }
    
    /**
     * Receives an event when a code assist session ends.
     * @param event the content assist event
     */
    @Override
    public void assistSessionEnded(ContentAssistEvent event) {
        if (event.assistant == null || !contentAssistActive) {
            return;
        }
        
        String path = docRecorder.getPath();
        String branch = docRecorder.getGlobalMacroRecorder().getBranch(path);
        MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
        
        TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.CODE_COMPLETION, mpath, TriggerMacro.Timing.BEGIN);
        docRecorder.getGlobalMacroRecorder().recordTriggerMacro(tmacro);
        docRecorder.setCodeCompletionInProgress(true);
        
        contentAssistActive = false;
    }
    
    /**
     * Receives information when the selection in the proposal pop-up is changed or if the insert-mode changed.
     * @param proposal the newly selected proposal, possibly <code>null</code>
     * @param smartToggle <code>true</code> if the insert-mode toggle is being pressed, otherwise <code>false</code>
     */
    @Override
    public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
    }
    
    /**
     * Invoked after applying a proposal.
     * @param proposal the applied proposal
     */
    public void applied(ICompletionProposal proposal) {
        docRecorder.setCodeCompletionInProgress(false);
        
        String path = docRecorder.getPath();
        String branch = docRecorder.getGlobalMacroRecorder().getBranch(path);
        MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
        CompoundMacro compoundMacro = docRecorder.getCompoundMacro();
        int size = compoundMacro.getMacroNumber();
        
        if (size > 0) {
            Macro cmacro = compoundMacro.getMacro(size - 1);
            if (cmacro instanceof DocumentMacro) {
                DocumentMacro dmacro = (DocumentMacro)cmacro;
                DocumentMacro macro = new DocumentMacro(DocumentMacro.Action.COMPLETE, dmacro.getMacroPath(),
                        dmacro.getStart(), dmacro.getInsertedText(), dmacro.getDeletedText());
                compoundMacro.removeMacro(size - 1);
                compoundMacro.addMacro(macro);
            }
        }
        
        docRecorder.recordCodeCompletionMacro(codeCompletionMacro);
        compoundMacro.removeMacro(compoundMacro.getMacroNumber() - 1);
        compoundMacro.addMacro(0, codeCompletionMacro);
        
        TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.CODE_COMPLETION, mpath, TriggerMacro.Timing.END);
        docRecorder.getGlobalMacroRecorder().recordTriggerMacro(tmacro);
        
        CodeCompletionMacro.Action action = CodeCompletionMacro.Action.CONTENT_ASSIST_END;
        CodeCompletionMacro cmacro = new CodeCompletionMacro(action, codeCompletionMacro.getMacroPath(), codeCompletionMacro.getCommandId());
        docRecorder.recordCodeCompletionMacro(cmacro);
    }
}
