/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.sample1;

import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.recorder.IMacroRecorder;
import org.jtool.macrorecorder.recorder.MacroRecorder;
import org.jtool.macrorecorder.recorder.MacroConsole;
import org.jtool.macrorecorder.recorder.MacroEvent;
import org.jtool.macrorecorder.recorder.IMacroListener;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * A sample listener that receives execution events and prints change macros.
 * Starting and stopping is switched to each other.
 */
public class SampleMacroPrintCommand extends AbstractHandler implements IMacroListener {
    
    private boolean recording = false;
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        recording = !recording;
        
        if (recording) {
            start();
        } else {
            stop();
        }
        return null;
    }
    
    private void start() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        recorder.addMacroListener(this);
        recorder.start();
    }
    
    private void stop() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        recorder.removeMacroListener(this);
        recorder.stop();
    }
    
    @Override
    public void macroAdded(MacroEvent evt) {
        Macro macro = evt.getMacro();
        MacroConsole.println("## " + macro.getDescription());
    }
    
    @Override
    public void rawMacroAdded(MacroEvent evt) {
    }
}
