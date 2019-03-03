/*
 *  Copyright 2016-2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.CommandMacro;
import org.jtool.macrorecorder.macro.DocumentMacro;
import org.jtool.macrorecorder.macro.CopyMacro;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractTextEditor;
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
     * The document of a file.
     */
    private IDocument doc;
    
    /**
     * The styled text of an editor.
     */
    private StyledText styledText;
    
    /**
     * The starting point of the text that is contained the selection.
     */
    private int selectionStart;
    
    
    /**
     * The text that is contained the selection.
     */
    private String selectionText;
    
    /**
     * Creates an object that records document macros performed on an editor.
     * @param editor the editor
     * @param recorder a recorder that sends macro events
     */
    DocMacroRecorderOnEdit(IEditorPart editor, Recorder recorder) {
        super(EditorUtilities.getInputFilePath(editor), recorder);
        
        assert editor != null;
        this.editor = editor;
        this.doc = EditorUtilities.getDocument(editor);
        this.preCode = doc.get();
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
        dumpMacro(macro);
    }
    
    /**
     * Records a command macro.
     * @param macro a command macro that replaced with a copy macro to be recorded
     */
    @Override
    void recordCopyMacro(CommandMacro macro) {
        int start = getSelectionStart();
        if (start < 0) {
            return;
        }
        
        CopyMacro cmacro = new CopyMacro(CopyMacro.Action.COPY, macro.getMacroPath(), start, getSelectionText());
        recorder.recordRawMacro(cmacro);
        
        dumpMacro(cmacro);
    }
    
    /**
     * Returns the starting point of the text that is contained the selection.
     * @return the starting point of the selected text, <code>-1</code> if the selection is invalid
     */
    int getSelectionStart() {
        IWorkbenchPart part = EditorUtilities.getActivePart();
        if (!(part instanceof AbstractTextEditor)) {
            return -1;
        }
        
        if (styledText == null) {
            return -1;
        }
        
        String path = EditorUtilities.getActiveInputFilePath();
        if (path == null) {
            return -1;
        }
        
        Display display = PlatformUI.getWorkbench().getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                selectionStart = styledText.getSelectionRange().x;
            }
        });
        return selectionStart;
    }
    
    /**
     * Returns the text that is contained the selection.
     * @return the selected text, the empty string if the selection is invalid
     */
    String getSelectionText() {
        if (styledText == null) {
            return "";
        }
        
        String path = EditorUtilities.getActiveInputFilePath();
        if (path == null) {
            return "";
        }
        
        Display display = PlatformUI.getWorkbench().getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                selectionText = styledText.getSelectionText();
            }
        });
        return selectionText;
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
