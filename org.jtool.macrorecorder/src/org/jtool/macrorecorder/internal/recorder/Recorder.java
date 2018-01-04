/*
 *  Copyright 2016-2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.recorder.MacroRecorder;
import org.jtool.macrorecorder.macro.CompoundMacro;
import org.jtool.macrorecorder.macro.TriggerMacro;
import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.recorder.MacroCompressor;
import org.jtool.macrorecorder.recorder.MacroConsole;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.progress.UIJob;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/**
 * Records all kinds of macros that were performed on Eclipse.
 * @author Katsuhisa Maruyama
 */
public class Recorder {
    
    /**
     * The collection of recorders that record document macros.
     */
    private Map<String, DocMacroRecorder> docRecorders = new HashMap<String, DocMacroRecorder>();
    
    /**
     * A recorder that records menu actions.
     */
    private GlobalMacroRecorder globalRecorder;
    
    /**
     * The facade of a macro recorder.
     */
    private MacroRecorder macroRecorder;
    
    /**
     * A compressor that compresses macros.
     */
    private MacroCompressor macroCompressor;
    
    /**
     * A compound macro that contains macros.
     */
    private CompoundMacro compoundMacro;
    
    /**
     * The collection of raw macros that were recorded.
     */
    private List<Macro> rawMacros = new ArrayList<Macro>();
    
    /**
     * The last raw macro.
     */
    private Macro lastRawMacro;
    
    /**
     * The last macro.
     */
    private Macro lastMacro = null;
    
    /**
     * Creates an object that records macros.
     * @param recorder the facade of a macro recorder
     * @param compressor the compressor
     */
    public Recorder(MacroRecorder recorder, MacroCompressor compressor) {
        this.macroRecorder = recorder;
        this.macroCompressor = compressor;
        globalRecorder = new GlobalMacroRecorder(this);
    }
    
    /**
     * Returns the recorder that records global macros.
     * @return the global macro recorder
     */
    GlobalMacroRecorder getGlobalMacroRecorder() {
        return globalRecorder;
    }
    
    /**
     * Sets a compressor that compresses macros.
     * @param compressor the compressor
     */
    public void setMacroCompressor(MacroCompressor compressor) {
        if (compressor != null) {
            this.macroCompressor = compressor;
        }
    }
    
    /**
     * Returns the compressor that compresses macros.
     * @return the macro compressor
     */
    public MacroCompressor getMacroCompressor() {
        return macroCompressor;
    }
    
    /**
     * Returns all the recorders that record document macros.
     * @return the collection of the recorders
     */
    Collection<DocMacroRecorder> getDocMacroRecorders() {
        return docRecorders.values();
    }
    
    /**
     * Returns a recorder that records document macros related to a file.
     * @param path the path of the file
     * @return the recorder, or <code>null</code> if none
     */
    DocMacroRecorder getDocMacroRecorder(String path) {
        if (path != null) {
            return docRecorders.get(path);
        }
        return null;
    }
    
    /**
     * Removes a recorder that records document macros related to a file.
     * @param path the path of the file
     */
    void removeDocMacroRecorder(String path) {
        docRecorders.remove(path);
    }
    
    /**
     * Starts the recording of document macros performed on an editor.
     */
    public void start() {
        UIJob job = new UIJob("Start") {
            
            /**
             * Run the job in the UI thread.
             * @param monitor the progress monitor to use to display progress
             */
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                globalRecorder.start();
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        
        compoundMacro = null;
        rawMacros.clear();
    }
    
    /**
     * Stops the recording of menu and document macros.
     */
    public void stop() {
        if (globalRecorder == null && docRecorders.values().size() == 0) {
            return;
        }
        
        UIJob job = new UIJob("Stop") {
            /**
             * Run the job in the UI thread.
             * @param monitor the progress monitor to use to display progress
             */
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                List<DocMacroRecorder> recorders = new ArrayList<DocMacroRecorder>(docRecorders.size());
                for (DocMacroRecorder docRrecorder : getDocMacroRecorders()) {
                    recorders.add(docRrecorder);
                }
                for (DocMacroRecorder docRrecorder : recorders) {
                    docRrecorder.stop();
                }
                recorders.clear();
                docRecorders.clear();
                
                globalRecorder.stop();
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        
        rawMacros.clear();
    }
    
    /**
     * Starts the recording of document macros performed on an editor.
     * @param editor the editor
     * @return the document recorder
     */
    DocMacroRecorder on(IEditorPart editor) {
        String path = EditorUtilities.getInputFilePath(editor);
        DocMacroRecorder docRecorder = getDocMacroRecorder(path);
        
        if (docRecorder == null) {
            docRecorder = new DocMacroRecorderOnEdit(editor, this, macroCompressor);
            docRecorder.start();
            docRecorders.put(path, docRecorder);
            
        } else if (docRecorder instanceof DocMacroRecorderOffEdit) {
            docRecorders.remove(path);
            docRecorder.stop();
            
            docRecorder = new DocMacroRecorderOnEdit(editor, this, macroCompressor);
            docRecorder.start();
            docRecorders.put(path, docRecorder);
        }
        return docRecorder;
    }
    
    /**
     * Stops the recording of menu and document macros performed on an editor.
     * @param editor the editor
     * @return the document recorder
     */
    DocMacroRecorder off(IEditorPart editor) {
        DocMacroRecorder docRecorder = off(EditorUtilities.getInputFile(editor));
        return docRecorder;
    }
    
    /**
     * Stops the recording of menu and document macros performed on an editor.
     * @param file a file resource
     * @return the document recorder
     */
    DocMacroRecorder off(IFile file) {
        String path = EditorUtilities.getInputFilePath(file);
        DocMacroRecorder docRecorder = getDocMacroRecorder(path);
        
        if (docRecorder == null) {
            docRecorder = new DocMacroRecorderOffEdit(file, this);
            docRecorder.start();
            docRecorders.put(path, docRecorder);
            
        } else if (docRecorder instanceof DocMacroRecorderOnEdit) {
            docRecorders.remove(path);
            docRecorder.stop();
            
            docRecorder = new DocMacroRecorderOffEdit(file, this);
            docRecorder.start();
            docRecorders.put(path, docRecorder);
        }
        return docRecorder;
    }
    
    /**
     * Records a macro.
     * @param macro the macro to be recorded
     */
    void recordMacro(Macro macro) {
        if (macro instanceof TriggerMacro) {
            
            TriggerMacro tmacro = (TriggerMacro)macro;
            if (compoundMacro == null && tmacro.isBegin()) {
                compoundMacro = new CompoundMacro(tmacro.getTime(), tmacro.getAction(), tmacro.getMacroPath(), tmacro.getCommandMacro());
                
            } else if (tmacro.isEnd()) {
                
                if (compoundMacro != null) {
                    compoundMacro.setRawMacros(new ArrayList<Macro>(rawMacros));
                    rawMacros.clear();
                    
                    if (compoundMacro.getMacros().size() != 0) {
                        compoundMacro.sort();
                        notifyMacro(compoundMacro);
                    }
                }
                compoundMacro = null;
                
            } else if (tmacro.isCancel()) {
                if (compoundMacro != null) {
                    for (Macro m : compoundMacro.getMacros()) {
                        notifyMacro(m);
                    }
                }
                compoundMacro = null;
            }
            
        } else {
            if (compoundMacro != null) {
                compoundMacro.addMacro(macro);
            } else {
                macro.setRawMacros(new ArrayList<Macro>(rawMacros));
                rawMacros.clear();
                
                notifyMacro(macro);
            }
        }
    }
    
    /**
     * Records a compound macro.
     * @param macro the compound macro to be recorded
     */
    void recordCompoundMacro(CompoundMacro cmacro) {
        List<Macro> macros = cmacro.getMacros();
        for (int idx = 0; idx < macros.size(); idx++) {
            Macro macro = macros.get(idx);
            
            if (compoundMacro != null) {
                compoundMacro.addMacro(macro);
            } else {
                notifyMacro(macro);
            }
        }
    }
    
    /**
     * Sends a macro event to all the listeners.
     * @param macro the macro sent to the listeners
     */
    private void notifyMacro(Macro macro) {
        if (macro != null && lastMacro != null) {
            if (getPathString(macro.getPath()).equals(getPathString(lastMacro.getPath())) &&
                    getPathString(macro.getBranch()).equals(getPathString(lastMacro.getBranch())) &&
                    !macro.getTime().isAfter(lastMacro.getTime())) {
                MacroConsole.println("The order of macros is abnormal: " + macro.toString() + " " + lastMacro.toString());
                return;
            }
        }
        
        macroRecorder.notifyMacro(macro);
        lastMacro = macro;
    }
    
    /**
     * Returns the string that represents a path name.
     * @param path the path name
     * @return the original path name or the empty string if path is <code>null</code>.
     */
    private String getPathString(String path) {
        if (path == null) {
            return "";
        }
        return path;
    }
    
    /**
     * Records a raw macro to be recorded.
     * @param macro the raw macro to be recorded
     */
    void recordRawMacro(Macro macro) {
        lastRawMacro = macro;
        rawMacros.add(macro);
        
        notifyRawMacro(macro);
    }
    
    /**
     * Sends a raw macro event to all the listeners.
     * @param macro the raw macro sent to the listeners
     */
    private void notifyRawMacro(Macro macro) {
        macroRecorder.notifyRawMacro(macro);
    }
    
    /**
     * Returns the last raw macro.
     * @return the last raw macro
     */
    Macro getLastRawMacro() {
        return lastRawMacro;
    }
}
