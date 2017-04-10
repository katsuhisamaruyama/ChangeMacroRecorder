/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.CancelMacro;
import org.jtool.macrorecorder.macro.DocumentMacro;
import org.jtool.macrorecorder.macro.TriggerMacro;
import org.jtool.macrorecorder.macro.MacroPath;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.undo.IDocumentUndoListener;
import org.eclipse.text.undo.DocumentUndoEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Listens document events.
 * @author Katsuhisa Maruyama
 */
class DocumentListener implements IDocumentListener, IDocumentUndoListener, Listener {
    
    /**
     * A recorder that records macros.
     */
    private DocMacroRecorder docRecorder;
    
    /**
     * The contents of the document inserted by this macro.
     */
    private String insertedText;
    
    /**
     * The contents of the document deleted by this macro.
     */
    private String deletedText;
    
    /**
     * A flag that indicates if an undo action currently progressed.
     */
    private boolean undoInprogress = false;
    
    /**
     * A flag that indicates if an redo action currently progressed.
     */
    private boolean redoInprogress = false;
    
    /**
     * Creates an object that records document events.
     * @param recorder a recorder that records macros
     */
    DocumentListener(DocMacroRecorder recorder) {
        this.docRecorder = recorder;
    }
    
    /**
     * Receives a document event will be performed.
     * @param event the document event describing the document change
     */
    @Override
    public void documentAboutToBeChanged(DocumentEvent event) {
        insertedText = event.getText();
        deletedText = "";
        if (event.getLength() > 0) {
            IDocument doc = event.getDocument();
            
            try {
                deletedText = doc.get(event.getOffset(), event.getLength());
            } catch (Exception e) {
                return;
            }
        }
    }
    
    /**
     * Receives a document event has been performed.
     * @param event the document event describing the document change
     */
    @Override
    public void documentChanged(DocumentEvent event) {
        if (insertedText.length() == 0 && deletedText.length() == 0) {
            return;
        }
        
        String path = docRecorder.getPath();
        String branch = docRecorder.getGlobalMacroRecorder().getBranch(path);
        
        if (undoInprogress) {
            if (docRecorder.getPathToBeRefactored() == null) {
                DocumentMacro macro = new DocumentMacro(DocumentMacro.Action.UNDO,
                                        new MacroPath(path), branch, event.getOffset(), insertedText, deletedText);
                docRecorder.recordDocumentMacro(macro);
                
            } else {
                DocumentMacro macro = new CancelMacro(DocumentMacro.Action.UNDO,
                                        new MacroPath(path), branch, event.getOffset(), insertedText, deletedText);
                docRecorder.recordDocumentMacro(macro);
            }
            
        } else if (redoInprogress) {
            if (docRecorder.getPathToBeRefactored() == null) {
                DocumentMacro macro = new DocumentMacro(DocumentMacro.Action.REDO,
                                        new MacroPath(path), branch, event.getOffset(), insertedText, deletedText);
                docRecorder.recordDocumentMacro(macro);
                
            } else {
                DocumentMacro macro = new CancelMacro(DocumentMacro.Action.REDO,
                                        new MacroPath(path), branch, event.getOffset(), insertedText, deletedText);
                docRecorder.recordDocumentMacro(macro);
            }
            
        } else {
            DocumentMacro macro;
            if (docRecorder.getGlobalMacroRecorder().getCutInProgress()) {
                macro = new DocumentMacro(DocumentMacro.Action.CUT,
                          new MacroPath(path), branch, event.getOffset(), insertedText, deletedText);
            } else if (docRecorder.getGlobalMacroRecorder().getPasteInProgress()) {
                macro = new DocumentMacro(DocumentMacro.Action.PASTE,
                          new MacroPath(path), branch, event.getOffset(), insertedText, deletedText);
            } else {
                macro = new DocumentMacro(DocumentMacro.Action.EDIT,
                          new MacroPath(path), branch, event.getOffset(), insertedText, deletedText);
            }
            docRecorder.recordDocumentMacro(macro);
        }
    }
    
    /**
     * Receives a document undo event when the document is involved in an undo-related change.
     * @param event the document undo event describing the particular notification
     */
    @Override
    public void documentUndoNotification(DocumentUndoEvent event) {
        String path = docRecorder.getPath();
        String branch = docRecorder.getGlobalMacroRecorder().getBranch(path);
        
        int eventType = event.getEventType();
        if (eventType >= 16) {
            eventType = eventType - 16;
        }
        
        if (eventType == DocumentUndoEvent.ABOUT_TO_UNDO) {
            if (docRecorder.getPathToBeRefactored() == null) {
                TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.UNDO,
                                        new MacroPath(path), branch, TriggerMacro.Timing.BEGIN);
                docRecorder.recordMacro(tmacro);
            }
            undoInprogress = true;
            
        } else if (eventType == DocumentUndoEvent.ABOUT_TO_REDO) {
            if (docRecorder.getPathToBeRefactored() == null) {
                TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REDO,
                                        new MacroPath(path), branch, TriggerMacro.Timing.BEGIN);
                docRecorder.recordMacro(tmacro);
            }
            redoInprogress = true;
            
        } else if (eventType == DocumentUndoEvent.UNDONE) {
            if (docRecorder.getPathToBeRefactored() == null) {
                TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.UNDO,
                                        new MacroPath(path), branch, TriggerMacro.Timing.END);
                docRecorder.recordMacro(tmacro);
            }
            undoInprogress = false;
            
        } else if (eventType == DocumentUndoEvent.REDONE) {
            if (docRecorder.getPathToBeRefactored() == null) {
                TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REDO,
                                        new MacroPath(path), branch, TriggerMacro.Timing.END);
                docRecorder.recordMacro(tmacro);
            }
            redoInprogress = false;
        }
    }
    
    /**
     * Receives an event when the registered event occurs.
     * @param event the event which occurred
     */
    @Override
    public void handleEvent(Event event) {
        boolean cursorMoved = false;
        if (event.type == SWT.KeyDown) {
            cursorMoved = cursorMoved(event);
            
        } else if (event.type == SWT.MouseDown || event.type == SWT.MouseDoubleClick) {
            cursorMoved = true;
        }
        
        if (cursorMoved) {
            String path = docRecorder.getPath();
            String branch = docRecorder.getGlobalMacroRecorder().getBranch(path);
            
            TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.CURSOR_CHANGE,
                                    new MacroPath(path), branch, TriggerMacro.Timing.INSTANT);
            docRecorder.recordTriggerMacro(tmacro);
            
            docRecorder.getGlobalMacroRecorder().setSelectedPath(path);
        }
    }
    
    /**
     * Tests if a given key event may move the current cursor position.
     * @param event the key event
     * @return <code>true</code> if the key event may move the current cursor position, otherwise <code>false</code>
     */
    private boolean cursorMoved(Event event) {
        final int key = (SWT.KEY_MASK & event.keyCode);
        switch (key) {
            case SWT.CR:
            case SWT.ARROW_DOWN:
            case SWT.ARROW_LEFT:
            case SWT.ARROW_RIGHT: 
            case SWT.ARROW_UP: 
            case SWT.HOME:
            case SWT.END:
            case SWT.PAGE_UP:
            case SWT.PAGE_DOWN:
                return true;
        }
        return false;
    }
}
