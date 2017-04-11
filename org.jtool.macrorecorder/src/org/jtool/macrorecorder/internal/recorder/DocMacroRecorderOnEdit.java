/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.CodeCompletionMacro;
import org.jtool.macrorecorder.macro.CommandMacro;
import org.jtool.macrorecorder.macro.DocumentMacro;
import org.jtool.macrorecorder.macro.CopyMacro;
import org.jtool.macrorecorder.recorder.IMacroCompressor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.jface.text.IDocument;

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
                documentListener.register(doc, styledText);
                completionListener.register(editor);
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
                documentListener.unregister(doc);
                completionListener.unregister(editor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        
        super.stop();
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
        
        CopyMacro cmacro = new CopyMacro(CopyMacro.Action.COPY,
                               macro.getPath(), macro.getBranch(), getSelectionStart(), getSelectionText());
        recorder.recordRawMacro(cmacro);
        
        dumpMacro(cmacro);
    }
    
    /**
     * Returns the starting point of the text that is contained the selection.
     * @return the starting point of the selected text
     */
    int getSelectionStart() {
        assert styledText != null;
        return styledText.getSelectionRange().x;
    }
    
    /**
     * Returns the text that is contained the selection.
     * @return the selected text
     */
    String getSelectionText() {
        assert styledText != null;
        return styledText.getSelectionText();
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
