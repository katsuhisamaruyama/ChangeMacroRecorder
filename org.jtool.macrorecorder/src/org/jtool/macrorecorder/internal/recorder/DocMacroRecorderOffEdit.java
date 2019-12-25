/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

/**
 * Records document macros related to a file.
 * @author Katsuhisa Maruyama
 */
class DocMacroRecorderOffEdit extends DocMacroRecorder {
    
    /**
     * A file related to recorded macros.
     */
    private IFile file;
    
    /**
     * Creates an object that records document macros related to a file.
     * @param file the file
     * @param recorder a recorder that sends macro events
     */
    DocMacroRecorderOffEdit(IFile file, Recorder recorder) {
        super(EditorUtilities.getInputFilePath(file), recorder);
        
        assert file != null;
        this.file = file;
        setPreCode(getCurrentCode());
    }
    
    /**
     * Starts to record document macros.
     */
    @Override
    void start() {
        super.start();
    }
    
    /**
     * Stops recording macros.
     */
    @Override
    void stop() {
        super.stop();
    }
    
    /**
     * Obtains the current contents of a file under recording.
     * @return the contents of source code, or <code>null</code> if source code does not exist
     */
    @Override
    String getCurrentCode() {
        if (dispose) {
            return null;
        }
        
        IDocument doc = EditorUtilities.getDocument(file);
        if (doc != null) {
            return doc.get();
        }
        return null;
    }
}
