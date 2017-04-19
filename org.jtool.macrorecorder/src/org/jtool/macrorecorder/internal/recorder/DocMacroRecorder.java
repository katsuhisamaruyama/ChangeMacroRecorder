/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.macro.DocumentMacro;
import org.jtool.macrorecorder.macro.CommandMacro;
import org.jtool.macrorecorder.macro.TriggerMacro;
import org.jtool.macrorecorder.macro.CancelMacro;
import org.jtool.macrorecorder.macro.CompoundMacro;
import org.jtool.macrorecorder.macro.CodeCompletionMacro;
import org.jtool.macrorecorder.recorder.MacroConsole;
import org.jtool.macrorecorder.internal.diff.DiffMacro;
import org.jtool.macrorecorder.internal.diff.DiffMacroGenerator;
import java.util.List;
import java.util.ArrayList;

/**
 * Records document macros.
 * @author Katsuhisa Maruyama
 */
class DocMacroRecorder {
    
    /**
     * A manager that manages document events.
     */
    protected DocumentListener documentListener;
    
    /**
     * A manager that manages code completion events.
     */
    protected CodeCompletionListener completionListener;
    
    /**
     * The collection of raw macros that were recorded.
     */
    protected List<Macro> rawMacros;
    
    /**
     * The path of a file whose contents are changed by macros.
     */
    protected String path;
    
    /**
     * The branch name of a resource on which this macro was performed.
     */
    protected String branch;
    
    /**
     * A recorder factory that sends macro events.
     */
    protected Recorder recorder;
    
    /**
     * A compound macro that contains macros.
     */
    protected CompoundMacro compoundMacro;
    
    /**
     * The last document macro stored for macro compression.
     */
    protected DocumentMacro lastDocumentMacro;
    
    /**
     * The previous contents of the source code.
     */
    protected String preCode = "";
    
    /**
     * A flag that indicates whether this recorder will be disposed.
     */
    protected boolean dispose;
    
    /**
     * Creates an object that records document macros related to a file.
     * @param path the of the file
     * @param recorder a recorder that sends macro events
     */
    DocMacroRecorder(String path, Recorder recorder) {
        this.path = path;
        this.branch = recorder.getGlobalMacroRecorder().getBranch(path);
        this.recorder = recorder;
        
        this.documentListener = new DocumentListener(this);
        this.completionListener = new CodeCompletionListener(this);
        this.rawMacros = new ArrayList<Macro>();
        this.dispose = false;
    }
    
    /**
     * Obtains the global recorder.
     * @return the global recorder
     */
    GlobalMacroRecorder getGlobalMacroRecorder() {
        return recorder.getGlobalMacroRecorder();
    }
    
    /**
     * Sets the previous contents of the source code.
     * @param code the contents of the source code
     */
    void setPreCode(String code) {
        preCode = code;
    }
    
    /**
     * Returns the previous contents of the source code.
     * @return the contents of the source code
     */
    String getPreCode() {
        return preCode;
    }
    
    /**
     * Starts to record document macros.
     */
    void start() {
        rawMacros.clear();
        compoundMacro = null;
        lastDocumentMacro = null;
    }
    
    /**
     * Stops recording macros.
     */
    void stop() {
        dumpLastDocumentMacro();
        rawMacros.clear();
        applyDiff(false);
        
        recorder.removeDocMacroRecorder(path);
    }
    
    /**
     * Stops recording macros if a stop is scheduled.
     */
    void dispose() {
        if (dispose) {
            stop();
        }
    }
    
    /**
     * Schedules a disposal of this recorder.
     */
    void willDispose() {
        dispose = true;
    }
    
    /**
     * Returns the path of a file whose contents are changed by macros.
     * @return the file path
     */
    String getPath() {
        return path;
    }
    
    /**
     * Returns a resource to be refactored.
     * @return the path name of the resource to be refactored
     */
    String getPathToBeRefactored() {
        return getGlobalMacroRecorder().getPathToBeRefactored();
    }
    
    /**
     * Tests if the state of this document recorder is on.
     * @return <code>true</code> if the state of this document recorder is on, otherwise <code>false</code>
     */
    public boolean isOn() {
        return this instanceof DocMacroRecorderOnEdit;
    }
    
    /**
     * Tests if the state of this document recorder is off.
     * @return <code>true</code> if the state of this document recorder is off, otherwise <code>false</code>
     */
    public boolean isOff() {
        return this instanceof DocMacroRecorderOffEdit;
    }
    
    /**
     * Records a document macro.
     * @param macro the document macro to be recorded
     */
    void recordDocumentMacro(DocumentMacro macro) {
        recorder.recordRawMacro(macro);
        dumpMacro(macro);
    }
    
    /**
     * Records a code completion macro.
     * @param macro the code completion macro to be recorded
     */
    void recordCodeCompletionMacro(CodeCompletionMacro macro) {
        dumpLastDocumentMacro();
        recorder.recordRawMacro(macro);
        recorder.recordMacro(macro);
    }
    
    /**
     * Records a trigger macro.
     * @param macro the trigger macro to be recorded
     */
    void recordTriggerMacro(TriggerMacro macro) {
        dumpMacro(macro);
        recorder.recordRawMacro(macro);
    }
    
    /**
     * Records a copy macro.
     * @param macro a command macro that replaced with a copy macro to be recorded
     */
    void recordCopyMacro(CommandMacro macro) {
    }
    
    /**
     * Returns the command macro that was lastly performed.
     * @return the last command macro
     */
    CommandMacro getLastCommandMacro() {
        return getGlobalMacroRecorder().getLastCommandMacro();
    }
    
    /**
     * Returns the starting point of the text that is contained the selection.
     * @return always <code>-1</code>
     */
    int getSelectionStart() {
        return -1;
    }
    
    /**
     * Returns the text that is contained the selection.
     * @return always the empty string
     */
    String getSelectionText() {
        return "";
    }
    
    /**
     * Dumps the last macro.
     */
    void dumpLastDocumentMacro() {
        if (lastDocumentMacro != null) {
            
            if (!hasInconsistency(preCode, lastDocumentMacro)) {
                recordMacro(lastDocumentMacro);
                applyMacro(lastDocumentMacro);
                
                lastDocumentMacro = null;
            }
        }
    }
    
    /**
     * Dumps both the last macro and the latest one.
     * @param macro the latest macro
     */
    void dumpMacro(Macro macro) {
        dumpLastDocumentMacro();
        
        if (!hasInconsistency(preCode, macro)) {
            recordMacro(macro);
            applyMacro(macro);
        }
    }
    
    /**
     * Notifies a macro.
     * @param macro the macro to be recorded
     */
    void recordMacro(Macro macro) {
        if (macro instanceof TriggerMacro) {
            TriggerMacro tmacro = (TriggerMacro)macro;
            if (compoundMacro == null && tmacro.isBegin()) {
                compoundMacro = new CompoundMacro(tmacro.getTime(), tmacro.getAction(),
                                    tmacro.getPath(), tmacro.getBranch(), tmacro.getCommandMacro());
                
            } else if (tmacro.isEnd()) {
                if (compoundMacro != null) {
                    compoundMacro.setRawMacros(new ArrayList<Macro>(rawMacros));
                    rawMacros.clear();
                    
                    recorder.recordCompoundMacro(compoundMacro);
                }
                compoundMacro = null;
            }
            
        } else {
            if (compoundMacro != null) {
                if (macro instanceof CancelMacro) {
                    CancelMacro cmacro = (CancelMacro)macro;
                    boolean suc = compoundMacro.cancelMacro(cmacro);
                    if (suc) {
                        applyMacro(macro);
                    } else {
                        MacroConsole.println("Cancellation failed: " + cmacro.toString());
                        MacroConsole.println(compoundMacro.toString());
                    }
                    
                } else {
                    compoundMacro.addMacro(macro);
                }
                
            } else {
                macro.setRawMacros(new ArrayList<Macro>(rawMacros));
                rawMacros.clear();
                
                recorder.recordMacro(macro);
            }
        }
    }
    
    /**
     * Obtains the current contents of a file under recording.
     * @return the contents of source code, or <code>null</code> if source code does not exist
     */
    String getCurrentCode() {
        return null;
    }
    
    /**
     * Confirms the necessity of generation of difference macros.
     */
    void applyDiff(boolean expected) {
        dumpLastDocumentMacro();
        
        String code = getCurrentCode();
        if (code == null) {
            code = "";
        }
        
        if (!code.equals(preCode)) {
            
            List<DiffMacro> diffs = DiffMacroGenerator.generate(path, branch, preCode, code);
            if (diffs.size() > 0) {
                for (DiffMacro diff : diffs) {
                    
                    DocumentMacro macro;
                    if (expected) {
                        macro = DiffMacro.getExpectedDiff(diff);
                    } else {
                        macro = DiffMacro.getUnexpectedDiff(diff);
                    }
                    
                    recorder.recordRawMacro(macro);
                    recorder.recordMacro(macro);
                }
            }
        }
        preCode = code;
    }
    
    /**
     * Applies a specified normal operation into given code.
     * @param macro the macro to be applied
     */
    boolean applyMacro(Macro macro) {
        if (macro instanceof DocumentMacro) {
            DocumentMacro dmacro = (DocumentMacro)macro;
            StringBuilder postCode = new StringBuilder(preCode);
            
            int start = dmacro.getStart();
            int end = start + dmacro.getDeletedText().length();
            String itext = dmacro.getInsertedText();
            postCode.replace(start, end, itext);
            preCode = postCode.toString();
        }
        return true;
    }
    
    /**
     * Tests if the deletion derives any inconsistency.
     * @param code the code before the application
     * @param macro the macro to be applied
     * @return <code>true</code> if a inconsistency exists, otherwise <code>false</code>
     */
    private boolean hasInconsistency(String code, Macro macro) {
        if (!(macro instanceof DocumentMacro)) {
            return false;
        }
        
        DocumentMacro dmacro = (DocumentMacro)macro;
        int start = dmacro.getStart();
        if (start > code.length()) {
            return true;
        }
        
        String dtext = dmacro.getDeletedText();
        int end = start + dtext.length();
        if (dtext.length() > 0) {
            String rtext = code.substring(start, end);
            if (rtext != null && !rtext.equals(dtext)) {
                
                for (int i = 0; i < rtext.length(); i++) {
                    if (rtext.charAt(i) == dtext.charAt(i)) {
                        MacroConsole.println("DEBUG:" + ((int)rtext.charAt(i)) + " == " + ((int)dtext.charAt(i)));
                    } else {
                        MacroConsole.println("DEBUG:" + ((int)rtext.charAt(i)) + " != " + ((int)dtext.charAt(i)));
                    }
                }
                return true;
            }
        }
        return false;
    }
}
