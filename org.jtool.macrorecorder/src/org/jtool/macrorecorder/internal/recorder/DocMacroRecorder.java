/*
 *  Copyright 2017-2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.macro.MacroPath;
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
     * The previous contents of the source code.
     */
    protected String preCode = "";
    
    /**
     * A flag that indicates whether this recorder will be disposed.
     */
    protected boolean dispose;
    
    /**
     * A flag that indicates the code completion is currently progressed.
     */
    private boolean codeCompletionInProgress = false;
    
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
     * Starts to record document macros.
     */
    void start() {
        rawMacros.clear();
        compoundMacro = null;
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
     * Sets the previous contents of the source code.
     * @param code the contents of the source code
     */
    void setPreCode(String code) {
        if (code != null) {
            preCode = code;
        } else {
            preCode = "";
        }
    }
    
    /**
     * Returns the previous contents of the source code.
     * @return the contents of the source code
     */
    String getPreCode() {
        return preCode;
    }
    
    /**
     * Sets the flag that indicates the code completion is currently progressed.
     * @param bool <code>true</code> if the code completion is currently progressed, otherwise <code>false</code>
     */
    void setCodeCompletionInProgress(boolean bool) {
        codeCompletionInProgress = bool;
    }
    
    /**
     * Returns the flag that indicates the code completion is currently progressed.
     * @return <code>true</code> if the code completion is currently progressed, otherwise <code>false</code>
     */
    boolean getCodeCompletionInProgress() {
        return codeCompletionInProgress;
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
        dumpMacro(macro);
    }
    
    /**
     * Cancels the code completion being in execution.
     */
    void cancelCodeCompletion() {
        if (getCodeCompletionInProgress()) {
            String path = getPath();
            String branch = getGlobalMacroRecorder().getBranch(path);
            MacroPath mpath = PathInfoFinder.getMacroPath(path, branch);
            
            TriggerMacro tmacro = new TriggerMacro(TriggerMacro.Action.CODE_COMPLETION, mpath, TriggerMacro.Timing.CANCEL);
            getGlobalMacroRecorder().recordTriggerMacro(tmacro);
            setCodeCompletionInProgress(false);
        }
    }
    
    /**
     * Records a trigger macro.
     * @param macro the trigger macro to be recorded
     */
    void recordTriggerMacro(TriggerMacro macro) {
        dumpLastDocumentMacro();
        
        recorder.recordRawMacro(macro);
        dumpMacro(macro);
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
     * Returns the current compound macro.
     * @return the compound macro
     */
    CompoundMacro getCompoundMacro() {
        return compoundMacro;
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
     * Dumps the last document macro.
     */
    void dumpLastDocumentMacro() {
        recorder.dumpLastDocumentMacro();
    }
    
    /**
     * Dumps a macro.
     * @param macro the macro
     */
    void dumpMacro(Macro macro) {
        checkMacro(macro);
        
        if (macro instanceof TriggerMacro) {
            TriggerMacro tmacro = (TriggerMacro)macro;
            if (compoundMacro == null && tmacro.isBegin()) {
                compoundMacro = new CompoundMacro(tmacro.getTime(), tmacro.getAction(), tmacro.getMacroPath(), tmacro.getCommandMacro());
                
            } else if (tmacro.isEnd()) {
                if (compoundMacro != null) {
                    compoundMacro.setRawMacros(new ArrayList<Macro>(rawMacros));
                    rawMacros.clear();
                    
                    recorder.recordCompoundMacro(compoundMacro);
                }
                compoundMacro = null;
                
            } else if (tmacro.isCancel()) {
                if (compoundMacro != null) {
                    for (Macro m : compoundMacro.getMacros()) {
                        recorder.recordMacro(m);
                    }
                }
                compoundMacro = null;
            }
            
        } else {
            if (compoundMacro != null) {
                if (macro instanceof CancelMacro) {
                    CancelMacro cmacro = (CancelMacro)macro;
                    boolean suc = compoundMacro.cancelMacro(cmacro);
                    if (!suc) {
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
     * @param expected <code>true</code> if the difference is expected, otherwise <code>false</code>
     */
    void applyDiff(boolean expected) {
        String code = getCurrentCode();
        generateDiff(code, expected);
    }
    
    /**
     * Confirms the necessity of generation of difference macros.
     * @param backupCode the current code
     * @param expected <code>true</code> if the difference is expected, otherwise <code>false</code>
     */
    void applyDiff(String backupCode, boolean expected) {
        String code = getCurrentCode();
        if (code == null) {
            code = backupCode;
        }
        
        System.out.println("CODE = " + code);
        
        generateDiff(code, expected);
    }
    
    /**
     * Generates document macros that represent differences.
     * @param code the current code
     * @param expected <code>true</code> if the difference is expected, otherwise <code>false</code>
     */
    private void generateDiff(String code, boolean expected) {
        dumpLastDocumentMacro();
        
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
     * Checks if a macro can be uneventfully applied.
     * @param macro the macro to be applied
     * @return <code>true</code> if the macro can be applied, otherwise <code>false</code>
     */
    private boolean checkMacro(Macro macro) {
        if (macro instanceof DocumentMacro) {
            DocumentMacro dmacro = (DocumentMacro)macro;
            if (hasInconsistency(dmacro)) {
                return false;
            } else {
                return applyMacro(dmacro);
            }
        }
        return true;
    }
    
    /**
     * Tests if the application of a document macro causes any inconsistency.
     * @param macro the document macro to be applied
     * @return <code>true</code> if a inconsistency exists, otherwise <code>false</code>
     */
    private boolean hasInconsistency(DocumentMacro macro) {
        int start = macro.getStart();
        if (start > preCode.length()) {
            return true;
        }
        
        String dtext = macro.getDeletedText();
        int end = start + dtext.length();
        if (dtext.length() > 0) {
            String rtext = preCode.substring(start, end);
            if (rtext != null && !rtext.equals(dtext)) {
                
                for (int i = 0; i < rtext.length(); i++) {
                    if (rtext.charAt(i) == dtext.charAt(i)) {
                        MacroConsole.println("DEBUG:" + start + " " + ((int)rtext.charAt(i)) + " == " + ((int)dtext.charAt(i)));
                    } else {
                        MacroConsole.println("DEBUG:" + start + " " + ((int)rtext.charAt(i)) + " != " + ((int)dtext.charAt(i)));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Applies a document macro to previous code.
     * @param macro the document macro to be applied
     */
    private boolean applyMacro(DocumentMacro macro) {
        StringBuilder postCode = new StringBuilder(preCode);
        int start = macro.getStart();
        int end = start + macro.getDeletedText().length();
        String itext = macro.getInsertedText();
        try {
            postCode.replace(start, end, itext);
            preCode = postCode.toString();
            return true;
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }
    }
}
