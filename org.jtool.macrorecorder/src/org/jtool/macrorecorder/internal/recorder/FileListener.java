/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.FileMacro;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import java.util.List;

/**
 * Listens file events obtained from file buffers and page changes.
 * @author Katsuhisa Maruyama
 */
class FileListener implements IPartListener {
    
    /**
     * A recorder that records global macros.
     */
    private GlobalMacroRecorder globalRecorder;
    
    /**
     * An active page in the workbench.
     */
    private IWorkbenchPage activePage;
    
    /**
     * The path of a file that a file operation previously performed.
     */
    private String prevFilePath;
    
    /**
     * Creates an object that records file operations.
     * @param recorder a recorder that records global macros
     */
    FileListener(GlobalMacroRecorder recorder) {
        this.globalRecorder = recorder;
    }
    
    /**
     * Registers a file buffer listener.
     */
    void register() {
        collectEditors();
        
        activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        activePage.addPartListener(this);
    }
    
    /**
     * Unregisters a file buffer listener.
     */
    void unregister() {
        activePage.removePartListener(this);
    }
    
    /**
     * Receives a part when it has been opened.
     * @param part the part that was opened
     */
    @Override
    public void partOpened(IWorkbenchPart part) {
        if (!(part instanceof AbstractTextEditor)) {
            return;
        }
        
        IEditorPart editor = (IEditorPart)part;
        globalRecorder.getRecorder().on(editor);
        String path = EditorUtilities.getInputFilePath(editor);
        String branch = globalRecorder.getBranch(path);
        
        FileMacro macro = new FileMacro(FileMacro.Action.OPENED,
                              path, branch, getCode(editor), getCharset(editor));
        globalRecorder.recordMacro(macro);
        prevFilePath = null;
        
        DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
        if (docRecorder != null) {
            docRecorder.applyDiff(false);
        }
    }
    
    /**
     * Receives a part when it has been closed.
     * @param part the part that was closed
     */
    @Override
    public void partClosed(IWorkbenchPart part) {
        if (!(part instanceof AbstractTextEditor)) {
            return;
        }
        
        IEditorPart editor = (IEditorPart)part;
        String path = EditorUtilities.getInputFilePath(editor);
        String branch = globalRecorder.getBranch(path);
        DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
        if (docRecorder != null) {
            docRecorder.applyDiff(false);
            
            String code = getCode(editor);
            String charset = getCharset(editor);
            
            FileMacro macro = new FileMacro(FileMacro.Action.CLOSED, path, branch, code, charset);
            globalRecorder.recordMacro(macro);
            prevFilePath = path;
            
            globalRecorder.getRecorder().off(editor);
        }
    }
    
    /**
     * Receives a part when it has been activated.
     * @param part the part that was activated
     */
    @Override
    public void partActivated(IWorkbenchPart part) {
        if (!(part instanceof AbstractTextEditor)) {
            return;
        }
        
        IEditorPart editor = (IEditorPart)part;
        String path = EditorUtilities.getInputFilePath(editor);
        if (path.equals(prevFilePath)) {
            return;
        }
        String code = getCode(editor);
        String charset = getCharset(editor);
        String branch = globalRecorder.getBranch(path);
        
        FileMacro macro = new FileMacro(FileMacro.Action.ACTIVATED, path, branch, code, charset);
        globalRecorder.recordMacro(macro);
        prevFilePath = path;
        
        DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
        if (docRecorder != null) {
            docRecorder.applyDiff(false);
        }
    }
    
    /**
     * Receives a part when it has been deactivated.
     * @param part the part that was deactivated
     */
    @Override
    public void partDeactivated(IWorkbenchPart part) {
    }
    
    /**
     * Receives a part when it has been brought to the top.
     * @param part the part that was surfaced
     */
    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
    }
    
    /**
     * Collects opened editors.
     */
    void collectEditors() {
        List<IEditorPart> editors = EditorUtilities.getEditors();
        for (IEditorPart editor : editors) {
            String path = EditorUtilities.getInputFilePath(editor);
            String branch = globalRecorder.getBranch(path);
            String code = getCode(editor);
            String charset = getCharset(editor);
            
            globalRecorder.getRecorder().on(editor);
            
            FileMacro macro = new FileMacro(FileMacro.Action.OPENED, path, branch, code, charset);
            globalRecorder.recordMacro(macro);
        }
        
        IEditorPart editor = EditorUtilities.getActiveEditor();
        if (editor != null) {
            String path = EditorUtilities.getInputFilePath(editor);
            String branch = globalRecorder.getBranch(path);
            String code = getCode(editor);
            String charset = getCharset(editor);
            
            FileMacro macro = new FileMacro(FileMacro.Action.ACTIVATED, path, branch, code, charset);
            globalRecorder.recordMacro(macro);
        }
    }
    
    /**
     * Obtains source code of the file on an editor.
     * @param editor editor the editor for the file
     * @return the contents of the source code, or an empty string
     */
    private String getCode(IEditorPart editor) {
        return EditorUtilities.getSourceCode(editor);
    }
    
    /**
     * Returns the name of a charset of the file on an editor.
     * @param editor editor the editor for the file
     * @return the name of a charset of the file, or <code>null</code>
     */
    private String getCharset(IEditorPart editor) {
        return EditorUtilities.getCharset(editor);
    }
}
