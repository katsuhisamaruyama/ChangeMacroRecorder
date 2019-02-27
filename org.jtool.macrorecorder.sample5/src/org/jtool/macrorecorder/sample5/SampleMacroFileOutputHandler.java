/*
 *  Copyright 2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.sample5;

import org.jtool.macrorecorder.macro.FileMacro;
import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.recorder.IMacroHandler;
import org.jtool.macrorecorder.recorder.MacroEvent;
import org.jtool.macrorecorder.recorder.MacroConsole;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

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
    
    private List<Macro> macroList = new ArrayList<Macro>();
    
    private static String DEFAULT_DIRECTORY_PATH = "#record";
    private String location;
    
    public SampleMacroFileOutputHandler() {
    }
    
    @Override
    public boolean recordingAllowed() {
        return true;
    }
    
    @Override
    public void initialize() {
        location = getDefaultLoaction();
        macroList.clear();
    }
    
    @Override
    public void terminate() {
        macroList.clear();
    }
    
    @Override
    public void macroAdded(MacroEvent evt) {
        Macro macro = evt.getMacro();
        MacroConsole.println(macro.getDescription());
        
        if (location == null) {
            return;
        }
        
        macroList.add(macro);
        if (macro instanceof FileMacro) {
            FileMacro fmacro = (FileMacro)macro;
            if (fmacro.isClose() || fmacro.isClose() || fmacro.isSave()) {
                storeMacros();
            }
        }
    }
    
    @Override
    public void rawMacroAdded(MacroEvent evt) {
    }
    
    public void storeMacros() {
        long time = macroList.get(0).getTimeAsLong();
        String filename = location + File.separatorChar + String.valueOf(time) + ".txt";
        StringBuilder buf = new StringBuilder();
        for (Macro macro : macroList) {
            buf.append(macro.getDescription());
            buf.append("\n");
        }
        
        try {
            FileWriter filewriter = new FileWriter(new File(filename));
            filewriter.write(buf.toString());
            filewriter.close();
            macroList.clear();
            
            MacroConsole.println("WRITE MACROS ON " + filename);
        } catch(IOException e) {
            MacroConsole.println(e.getMessage());
        }
    }
    
    private static String getDefaultLoaction() {
        IPath workspaceDir = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        
        String loc = new Path(workspaceDir.toString() + File.separatorChar + DEFAULT_DIRECTORY_PATH).toOSString();
        
        File file = new File(loc);
        if (!file.exists()) {
            file.mkdir();
            return loc;
        }
        return loc;
    }
}
