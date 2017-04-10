/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.CodeCompletionMacro;
import org.jtool.macrorecorder.macro.CommandMacro;
import org.jtool.macrorecorder.macro.DocumentMacro;
import org.jtool.macrorecorder.macro.MacroPath;
import org.jtool.macrorecorder.macro.CopyMacro;
import org.jtool.macrorecorder.recorder.IMacroCompressor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.ContentAssistantFacade;

/**
 * Records document macros performed on the editor.
 * @author Katsuhisa Maruyama
 */
class DocMacroRecorderOnEdit extends DocMacroRecorder {
    
    /**
     * An editor on which document macros are recorded.
     */
    private IEditorPart editor;
    
    /**
     * A compressor that compresses macros.
     */
    private IMacroCompressor compressor;
    
    /**
     * The document of a file.
     */
    private IDocument doc;
    
    /**
     * The styled text of an editor.
     */
    private StyledText styledText;
    
    /**
     * Creates an object that records document macros performed on an editor.
     * @param editor the editor
     * @param recorder a recorder that sends macro events
     * @param compressor a compressor that compresses macros
     */
    DocMacroRecorderOnEdit(IEditorPart editor, Recorder recorder, IMacroCompressor compressor) {
        super(EditorUtilities.getInputFilePath(editor), recorder);
        
        assert editor != null;
        this.editor = editor;
        this.doc = EditorUtilities.getDocument(editor);
        this.preCode = doc.get();
        this.compressor = compressor;
        this.styledText = EditorUtilities.getStyledText(editor);
    }
    
    /**
     * Starts to record document macros.
     */
    @Override
    void start() {
        UIJob job = new UIJob("Start") {
            
            /**
             * Run the job in the UI thread.
             * @param monitor the progress monitor to use to display progress
             */
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                register(documentListener);
                register(completionListener);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        
        super.start();
    }
    
    /**
     * Stops recording macros.
     */
    @Override
    void stop() {
        UIJob job = new UIJob("Stop") {
            /**
             * Run the job in the UI thread.
             * @param monitor the progress monitor to use to display progress
             */
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                unregister(documentListener);
                unregister(completionListener);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        
        super.stop();
    }
    
    /**
     * Registers a document manager with an editor.
     * @param doc the document to be managed
     * @param st the styled text of the editor
     * @param dm the document manager
     */
    private void register(final DocumentListener dl) {
        assert doc != null;
        doc.addDocumentListener(dl);
        
        DocumentUndoManagerRegistry.connect(doc);
        IDocumentUndoManager undoManager = DocumentUndoManagerRegistry.getDocumentUndoManager(doc);
        if (undoManager != null) {
            undoManager.addDocumentUndoListener(dl);
        }
        
        assert styledText != null;
        styledText.addListener(SWT.KeyDown, dl);
        styledText.addListener(SWT.MouseDown, dl);
        styledText.addListener(SWT.MouseDoubleClick, dl);
        
        styledText.addDisposeListener(new DisposeListener() {
             
            /**
             * Receives an event when the widget is disposed.
             * @param e - the event containing information about the dispose
             */
            public void widgetDisposed(DisposeEvent e) {
                styledText.removeListener(SWT.KeyDown, dl);
                styledText.removeListener(SWT.MouseDown, dl);
                styledText.removeListener(SWT.MouseDoubleClick, dl);
                
                styledText.removeDisposeListener(this);
            }
        });
    }
    
    /**
     * Unregisters a document manager with an editor.
     * @param dl the document manager
     */
    private void unregister(DocumentListener dl) {
        assert doc != null;
        doc.removeDocumentListener(dl);
        
        DocumentUndoManagerRegistry.connect(doc);
        IDocumentUndoManager undoManager = DocumentUndoManagerRegistry.getDocumentUndoManager(doc);
        DocumentUndoManagerRegistry.disconnect(doc);
        if (undoManager != null) {
            undoManager.removeDocumentUndoListener(dl);
        }
    }
    
    /**
     * Registers a code completion execution manager with the editor.
     * @param cl the code completion execution manager
     */
    private void register(CodeCompletionListener cl) {
        IQuickAssistAssistant assistant = EditorUtilities.getQuickAssistAssistant(editor);
        if (assistant != null) {
            assistant.addCompletionListener(cl);
        }
        
        ContentAssistantFacade facade = EditorUtilities.getContentAssistantFacade(editor);
        if (facade != null) {
            facade.addCompletionListener(cl);
        }
    }
    
    /**
     * Unregisters a code completion manager with the editor.
     * @param cl the code completion execution manager
     */
    private void unregister(CodeCompletionListener cl) {
        IQuickAssistAssistant assistant = EditorUtilities.getQuickAssistAssistant(editor);
        if (assistant != null) {
            assistant.removeCompletionListener(cl);
        }
        
        ContentAssistantFacade facade = EditorUtilities.getContentAssistantFacade(editor);
        if (facade != null) {
            facade.removeCompletionListener(cl);
        }
    }
    
    /**
     * Records a document macro.
     * @param macro the document macro to be recorded
     */
    @Override
    void recordDocumentMacro(DocumentMacro macro) {
        recorder.recordRawMacro(macro);
        
        if (macro.isCut() || macro.isPaste()) {
            dumpMacro(macro);
            return;
        }
        
        if (compressor.canCombine(macro)) {
            DocumentMacro newMacro = compressor.combine(lastDocumentMacro, macro);
            if (newMacro != null) {
                lastDocumentMacro = newMacro;
            } else {
                dumpLastDocumentMacro();
                lastDocumentMacro = macro;
            }
            
        } else {
            dumpMacro(macro);
        }
    }
    
    /**
     * Records a code completion macro.
     * @param macro the code completion macro to be recorded
     */
    @Override
    void recordCodeCompletionMacro(CodeCompletionMacro macro) {
        super.recordCodeCompletionMacro(macro);
    }
    
    /**
     * Records a command macro.
     * @param macro the command macro to be recorded
     */
    @Override
    void recordCopyMacro(CommandMacro macro) {
        assert styledText != null;
        
        int offset = styledText.getSelectionRange().x;
        String text = styledText.getSelectionText();
        
        CopyMacro cmacro = new CopyMacro(CopyMacro.Action.COPY, new MacroPath(macro.getPath()), macro.getBranch(), offset, text);
        recorder.recordRawMacro(cmacro);
        
        dumpMacro(cmacro);
    }
    
    /**
     * Obtains the current contents of a file under recording.
     * @return the contents of source code, or <code>null</code> if source code does not exist
     */
    @Override
    String getCurrentCode() {
        if (dispose) {
            return "";
        }
        
        IDocument doc = EditorUtilities.getDocument(editor);
        assert doc != null;
        return doc.get();
    }
}
