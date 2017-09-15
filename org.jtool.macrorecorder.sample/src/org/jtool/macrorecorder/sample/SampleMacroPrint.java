/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.sample;

import org.jtool.macrorecorder.recorder.MacroRecorder;
import org.jtool.macrorecorder.recorder.IMacroRecorder;
import org.jtool.macrorecorder.recorder.IMacroListener;
import org.jtool.macrorecorder.recorder.IMacroCompressor;
import org.jtool.macrorecorder.recorder.MacroEvent;
import org.jtool.macrorecorder.recorder.MacroConsole;
import org.jtool.macrorecorder.macro.Macro;

/**
 * Sample: Print macros 
 * @author Katsuhisa Maruyama
 */
public class SampleMacroPrint implements IMacroListener {
    
    public SampleMacroPrint() {
    }
    
    public void start() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        IMacroCompressor compressor = recorder.getMacroCompressor();
        compressor.setDelimiter(new char[] { '\n' });
        recorder.addMacroListener(this);
        recorder.start();
    }
    
    public void stop() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        recorder.removeMacroListener(this);
        recorder.stop();
    }
    
    @Override
    public void macroAdded(MacroEvent evt) {
        Macro macro = evt.getMacro();
        MacroConsole.println(macro.getDescription());
    }
    
    @Override
    public void rawMacroAdded(MacroEvent evt) {
    }
}
