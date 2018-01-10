
package $packageName$;

import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.recorder.IMacroRecorder;
import org.jtool.macrorecorder.recorder.MacroConsole;
import org.jtool.macrorecorder.recorder.MacroRecorder;
import org.jtool.macrorecorder.recorder.IMacroHandler;
import org.jtool.macrorecorder.recorder.MacroEvent;

%if comment
/**
 * A sample handler that hadles change macros.
 */
%endif

public class $className$ implements IMacroHandler {
    
    public $className$() {
    }
    
    @Override
    public boolean recordingAllowed() {
        return true;
    }
    
    @Override
    public void initialize() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        recorder.setDelimiters("\n");
    }
    
    @Override
    public void terminate() {
    }
    
    @Override
    public void macroAdded(MacroEvent evt) {
        Macro macro = evt.getMacro();
        MacroConsole.println("T " + macro.getDescription());
    }
    
    @Override
    public void rawMacroAdded(MacroEvent evt) {
        Macro macro = evt.getMacro();
        MacroConsole.println("R " + macro.getDescription());
    }
}
