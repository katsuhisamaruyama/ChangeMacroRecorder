/*
 *  Copyright 2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.sample5;

import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.recorder.IMacroHandler;
import org.jtool.macrorecorder.recorder.MacroEvent;
import org.jtool.macrorecorder.recorder.MacroConsole;

/**
 * A sample handler that prints change macros.
 * <p>
 * This is intended to be specified in the extension point of <code>org.jtool.macrorecorder.handlers</code>.
 * <pre><code>
 * <extension point="org.jtool.macrorecorder.handlers">
 *     <handler class="org.jtool.macrorecorder.sample5.SampleMacroFileOutputHandler">
 *     </handler>
 *  </extension>
 * </code></pre>
 * </p>
 */
public class SampleMacroFileOutputHandler implements IMacroHandler {
    
    public SampleMacroFileOutputHandler() {
    }
    
    @Override
    public boolean recordingAllowed() {
        return true;
    }
    
    @Override
    public void initialize() {
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
        Macro macro = evt.getMacro();
        MacroConsole.println("- " + macro.getDescription());
    }
}
