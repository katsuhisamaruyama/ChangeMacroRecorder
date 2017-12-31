/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.CancelMacro;
import org.jtool.macrorecorder.macro.DocumentMacro;
import org.jtool.macrorecorder.macro.TriggerMacro;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.undo.IDocumentUndoListener;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.text.undo.DocumentUndoEvent;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
     * Registers a document manager with an editor.
     * @param doc the document to be managed
     * @param styledText the styled text of the editor
     */
    void register(IDocument doc, StyledText styledText) {
        assert doc != null;
        doc.addDocumentListener(this);
        
        DocumentUndoManagerRegistry.connect(doc);
        IDocumentUndoManager undoManager = DocumentUndoManagerRegistry.getDocumentUndoManager(doc);
        if (undoManager != null) {
            undoManager.addDocumentUndoListener(this);
        }
        
        assert styledText != null;
        if (!styledText.isDisposed()) {
            styledText.addListener(SWT.KeyDown, this);
            styledText.addListener(SWT.MouseDown, this);
            styledText.addListener(SWT.MouseDoubleClick, this);
        }
        final DocumentListener dl = this;
        styledText.addDisposeListener(new DisposeListener() {
             
            /**
             * Receives an event when the widget is disposed.
             * @param e - the event containing information about the dispose
             */
            public void widgetDisposed(DisposeEvent e) {
                if (!styledText.isDisposed()) {
                    styledText.removeListener(SWT.KeyDown, dl);
                    styledText.removeListener(SWT.MouseDown, dl);
                    styledText.removeListener(SWT.MouseDoubleClick, dl);
                    styledText.removeDisposeListener(this);
                }
            }
        });
    }
    
    /**
     * Unregisters a document manager with an editor.
     * @param doc the document to be managed
     */
    void unregister(IDocument doc) {
        assert doc != null;
        doc.removeDocumentListener(this);
        
        DocumentUndoManagerRegistry.connect(doc);
        IDocumentUndoManager undoManager = DocumentUndoManagerRegistry.getDocumentUndoManager(doc);
        DocumentUndoManagerRegistry.disconnect(doc);
        if (undoManager != null) {
            undoManager.removeDocumentUndoListener(this);
        }
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
                        PathInfoFinder.getMacroPath(path, branch), event.getOffset(), insertedText, deletedText);
                docRecorder.recordDocumentMacro(macro);
                
            } else {
                DocumentMacro macro = new CancelMacro(DocumentMacro.Action.UNDO,
                        PathInfoFinder.getMacroPath(path, branch), event.getOffset(), insertedText, deletedText);
                docRecorder.recordDocumentMacro(macro);
            }
            
        } else if (redoInprogress) {
            if (docRecorder.getPathToBeRefactored() == null) {
                DocumentMacro macro = new DocumentMacro(DocumentMacro.Action.REDO,
                        PathInfoFinder.getMacroPath(path, branch), event.getOffset(), insertedText, deletedText);
                docRecorder.recordDocumentMacro(macro);
                
            } else {
                DocumentMacro macro = new CancelMacro(DocumentMacro.Action.REDO,
                        PathInfoFinder.getMacroPath(path, branch), event.getOffset(), insertedText, deletedText);
                docRecorder.recordDocumentMacro(macro);
            }
            
        } else {
            DocumentMacro macro;
            if (docRecorder.getGlobalMacroRecorder().getCutInProgress()) {
                macro = new DocumentMacro(DocumentMacro.Action.CUT,
                        PathInfoFinder.getMacroPath(path, branch), event.getOffset(), insertedText, deletedText);
            } else if (docRecorder.getGlobalMacroRecorder().getPasteInProgress()) {
                macro = new DocumentMacro(DocumentMacro.Action.PASTE,
                        PathInfoFinder.getMacroPath(path, branch), event.getOffset(), insertedText, deletedText);
            } else {
                macro = new DocumentMacro(DocumentMacro.Action.EDIT,
                        PathInfoFinder.getMacroPath(path, branch), event.getOffset(), insertedText, deletedText);
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
                        PathInfoFinder.getMacroPath(path, branch), TriggerMacro.Timing.BEGIN, docRecorder.getLastCommandMacro());
                docRecorder.recordTriggerMacro(tmacro);
            }
            undoInprogress = true;
            
        } else if (eventType == DocumentUndoEvent.ABOUT_TO_REDO) {
            if (docRecorder.getPathToBeRefactored() == null) {
                TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REDO,
                        PathInfoFinder.getMacroPath(path, branch), TriggerMacro.Timing.BEGIN, docRecorder.getLastCommandMacro());
                docRecorder.recordTriggerMacro(tmacro);
            }
            redoInprogress = true;
            
        } else if (eventType == DocumentUndoEvent.UNDONE) {
            if (docRecorder.getPathToBeRefactored() == null) {
                TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.UNDO,
                        PathInfoFinder.getMacroPath(path, branch), TriggerMacro.Timing.END, docRecorder.getLastCommandMacro());
                docRecorder.recordTriggerMacro(tmacro);
            }
            undoInprogress = false;
            
        } else if (eventType == DocumentUndoEvent.REDONE) {
            if (docRecorder.getPathToBeRefactored() == null) {
                TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.REDO,
                        PathInfoFinder.getMacroPath(path, branch), TriggerMacro.Timing.END, docRecorder.getLastCommandMacro());
                docRecorder.recordTriggerMacro(tmacro);
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
                    PathInfoFinder.getMacroPath(path, branch), TriggerMacro.Timing.INSTANT);
            docRecorder.recordTriggerMacro(tmacro);
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
