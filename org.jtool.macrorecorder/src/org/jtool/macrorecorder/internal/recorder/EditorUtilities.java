/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension3;
import org.eclipse.jface.text.source.ISourceViewerExtension4;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.text.source.ContentAssistantFacade;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import java.util.List;
import java.util.ArrayList;

/**
 * Provides utilities that obtain information on editors.
 * @author Katsuhisa Maruyama
 */
class EditorUtilities {
    
    /**
     * Obtains the source viewer of an editor.
     * @param editor the editor
     * @return the source viewer of the editor
     */
    static ISourceViewer getSourceViewer(IEditorPart editor) {
        if (editor == null) {
            return null;
        }
        
        ISourceViewer viewer = (ISourceViewer)editor.getAdapter(ITextOperationTarget.class);
        return viewer;
    }
    
    /**
     * Obtains the source viewer of an editor.
     * @param editor the editor
     * @return the source viewer of the editor
     */
    static ISourceViewerExtension3 getSourceViewerExtension3(IEditorPart editor) {
        ISourceViewer viewer = getSourceViewer(editor);
        
        if (viewer instanceof ISourceViewerExtension3) {
            return (ISourceViewerExtension3)viewer;
        }
        return null;
    }
    
    /**
     * Obtains the source viewer of an editor.
     * @param editor the editor
     * @return the source viewer of the editor
     */
    static ISourceViewerExtension4 getSourceViewerExtension4(IEditorPart editor) {
        ISourceViewer viewer = getSourceViewer(editor);
        
        if (viewer instanceof ISourceViewerExtension4) {
            return (ISourceViewerExtension4)viewer;
        }
        return null;
    }
    
    /**
     * Obtains the text viewer of an editor.
     * @param editor the editor
     * @return the text viewer of the editor
     */
    static ITextViewerExtension5 getTextViewerExtension5(IEditorPart editor) {
        if (editor == null) {
            return null;
        }
        
        ITextViewer viewer = (ITextViewer) editor.getAdapter(ITextOperationTarget.class);
        if (viewer instanceof ITextViewerExtension5) {
            return (ITextViewerExtension5) viewer;
        }
        return null;
    }
    
    /**
     * Obtains the styled text of an editor.
     * @param editor the editor
     * @return the styled text of the editor
     */
    static StyledText getStyledText(IEditorPart editor) {
        ISourceViewer viewer = getSourceViewer(editor);
        if (viewer != null) {
            return viewer.getTextWidget();
        }
        return null;
    }
    
    /**
     * Obtains the quick assist assistant of an editor.
     * @param editor the editor
     * @return the content assistant facade of the editor
     */
    public static IQuickAssistAssistant getQuickAssistAssistant(IEditorPart editor) {
        ISourceViewerExtension3 viewer = getSourceViewerExtension3(editor);
        if (viewer != null) {
            return viewer.getQuickAssistAssistant();
        }
        return null;
    }
    
    /**
     * Obtains the content assistant facade of an editor.
     * @param editor the editor
     * @return the content assistant facade of the editor
     */
    static ContentAssistantFacade getContentAssistantFacade(IEditorPart editor) {
        ISourceViewerExtension4 viewer = EditorUtilities.getSourceViewerExtension4(editor);
        if (viewer != null) {
            return viewer.getContentAssistantFacade();
        }
        return null;
    }
    
    /**
     * Obtains a file existing on an editor.
     * @param editor the editor
     * @return the file existing on the editor, or <code>null</code> if none
     */
    static IFile getInputFile(IEditorPart editor) {
        if (editor == null) {
            return null;
        }
        
        IEditorInput input = editor.getEditorInput();
        if (input instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput)input).getFile();
            return file;
        }
        return null;
    }
    
    /**
     * Obtains the path of a file existing on an editor.
     * @param editor the editor
     * @return the path of the file, which is relative to the path of the workspace
     */
    static String getInputFilePath(IEditorPart editor) {
        IFile file = getInputFile(editor);
        return getInputFilePath(file);
    }
    
    /**
     * Obtains the path of a file.
     * @param file the file
     * @return the path of the file, which is relative to the path of the workspace
     */
    static String getInputFilePath(IFile file) {
        if (file == null) {
            return null;
        }
        return file.getFullPath().toString();
    }
    
    /**
     * Obtains the document of a file.
     * @param file the file
     * @return the document of the file, or <code>null</code> if none
     */
    static IDocument getDocument(IFile file) {
        if (file == null) {
            return null;
        }
        
        try {
            TextFileDocumentProvider provider = new TextFileDocumentProvider();
            provider.connect(file);
            IDocument doc = provider.getDocument(file);
            provider.disconnect(file);
            return doc;
        } catch (CoreException e) {
        }
        return null;
    }
    
    /**
     * Obtains the document of a file existing on an editor.
     * @param editor the editor
     * @return the document of the file, or <code>null</code> if none
     */
    static IDocument getDocument(IEditorPart editor) {
        IFile file = getInputFile(editor);
        if (file == null) {
            return null;
        }
        return getDocument(file);
    }
    
    /**
     * Obtains the contents of source code appearing in an editor.
     * @param editor the editor
     * @return the contents of the source code, or <code>null</code> if the source code is not valid
     */
    static String getSourceCode(IEditorPart editor) {
        IDocument doc = getDocument(editor);
        if (doc == null) {
            return null;
        }
        return doc.get();
    }
    
    /**
     * Returns the name of a charset of the file on an editor.
     * @param editor editor the editor for the file
     * @return the name of a charset of the file, or <code>null</code>
     */
    static String getCharset(IEditorPart editor) {
        IFile file = EditorUtilities.getInputFile(editor);
        try {
            return file.getCharset();
        } catch (CoreException e) {
        }
        return null;
    }
    
    /**
     * Obtains an editor that may edits the contents of a file.
     * @param file the file
     * @return the editor of the file, or <code>null</code> if none
     */
    static IEditorPart getEditor(IFile file) {
        IEditorInput input = new FileEditorInput(file);
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
        
        for (IWorkbenchWindow window : windows) {
            IWorkbenchPage[] pages = window.getPages();
            
            for (IWorkbenchPage page : pages) {
                IEditorPart part = page.findEditor(input);
                return part;
            }
        }
        return null;
    }
    
    /**
     * Obtains all editors that are currently opened.
     * @return the collection of the opened editors
     */
    static List<IEditorPart> getEditors() {
        List<IEditorPart> editors = new ArrayList<IEditorPart>();
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IEditorReference[] refs = window.getActivePage().getEditorReferences();
            for (IEditorReference ref : refs) {
                IEditorPart part = ref.getEditor(true);
                if (part instanceof AbstractTextEditor) {
                    IEditorPart editor = (IEditorPart)part;
                    if (editor != null) {
                        editors.add(part);
                    }
                }
            }
        }
        return editors;
    }
    
    /**
     * Obtains an editor that is currently active.
     * @return the active editor, or <code>null</code> if none
     */
    static IEditorPart getActiveEditor() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        if (window != null) {
            IEditorPart part = window.getActivePage().getActiveEditor();
            return part;
        }
        return null;
    }
    
    /**
     * Obtains the path of a active file.
     * @return the path of the file, or <code>null</code> if none
     */
    static String getActiveInputFilePath() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        if (window != null) {
            ISelection selection = window.getSelectionService().getSelection();
            if (selection instanceof ITextSelection) {
                IEditorPart editor = getActiveEditor();
                if (editor != null) {
                    return getInputFilePath(editor);
                }
            }
        }
        return null;
    }
    
    /**
     * Obtains a workbench window that is currently active..
     * @return the active workbench window, or <code>null</code> if none
     */
    static IWorkbenchWindow getActiveWorkbenchWindow() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }
}
