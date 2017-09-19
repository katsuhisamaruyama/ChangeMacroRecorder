/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.sample;

import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.recorder.IMacroRecorder;
import org.jtool.macrorecorder.recorder.MacroRecorder;
import org.jtool.macrorecorder.recorder.IMacroListener;
import org.jtool.macrorecorder.recorder.MacroEvent;
import org.jtool.macrorecorder.recorder.MacroConsole;

/**
 * SampleMacroPrintCommand sample handler that prints change macros.
 * <p>
 * This is intended to be specified in the extension point of <code>org.jtool.macrorecorder.handlers</code>.
 * <pre>(@code
 * <extension
 *        point="org.jtool.macrorecorder.handlers">
 *     <handler
 *           class="org.jtool.macrorecorder.sample.SampleMacroPrintHandler"
 *           commandId="org.eclipse.macrorecorder.handler.SampleMacroPrintHandler">
 *     </handler>
 *  </extension>
 * )</pre>
 * </p>
 * @author Katsuhisa Maruyama
 */
public class SampleMacroPrintHandler implements IMacroListener {
    
    public SampleMacroPrintHandler() {
    }
    
    @Override
    public void initialize() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        recorder.setDelimiter(new char[] { '\n' });
    }
    
    @Override
    public void terminate() {
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
