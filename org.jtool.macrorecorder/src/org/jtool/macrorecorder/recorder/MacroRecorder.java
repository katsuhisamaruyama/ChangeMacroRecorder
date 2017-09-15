/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.jtool.macrorecorder.internal.recorder.Recorder;
import org.jtool.macrorecorder.macro.Macro;
import java.util.List;
import java.util.ArrayList;

/**
 * Records macros that were performed on Eclipse.
 * @author Katsuhisa Maruyama
 */
public class MacroRecorder implements IMacroRecorder {
    
    /**
     * The single instance of this recorder.
     */
    private static MacroRecorder instance = new MacroRecorder();
    
    /**
     * An internal recorder that records all kinds of macros.
     */
    private Recorder internalRecorder;
    
    /**
     * The collection of listeners that receives macro events.
     */
    private List<IMacroListener> macroListeners = new ArrayList<IMacroListener>();
    
    /**
     * A flag that indicates if recorded macros are displayed.
     */
    private boolean displayMacro;
    
    /**
     * A flag that indicates if recorded raw macros are displayed.
     */
    private boolean displayRawMacro;
    
    /**
     * A flag that indicates if this macro recorder is running.
     */
    private boolean running = false;
    
    /**
     * Creates an object that records macros.
     */
    private MacroRecorder() {
        MacroCompressor defaultCompressor = new MacroCompressor();
        internalRecorder = new Recorder(this, defaultCompressor);
    }
    
    /**
     * Returns the single instance of this recorder.
     * @return the single instance
     */
    public static IMacroRecorder getInstance() {
        return instance;
    }
    
    /**
     * Returns the compressor that compresses change macros.
     * @return the macro compressor
     */
    public IMacroCompressor getMacroCompressor() {
        return internalRecorder.getMacroCompressor();
    }
    
    /**
     * Sets a compressor that compresses change macros.
     * @param compressor the compressor
     */
    public void setMacroCompressor(MacroCompressor compressor) {
        assert compressor != null;
        internalRecorder.setMacroCompressor(compressor);
    }
    
    /**
     * Starts the recording of change macros.
     */
    public void start() {
        if (!running) {
            internalRecorder.start();
        }
        running = true;
       
    }
    
    /**
     * Stops the recording of change macros.
     */
    public void stop() {
        if (running) {
            internalRecorder.stop();
        }
        running = false;
    }
    
    /**
     * Adds a listener that receives a change macro event.
     * @param listener the event listener to be added
     */
    public void addMacroListener(IMacroListener listener) {
        assert listener != null;
        macroListeners.add(listener);
    }
    
    /**
     * Removes a listener that receives a change macro event.
     * @param listener the event listener to be removed
     */
    public void removeMacroListener(IMacroListener listener) {
        assert listener != null;
        macroListeners.remove(listener);
    }
    
    /**
     * Sets flags that indicate if macros are displayed on the console for debugging.
     * @param display <code>true</code> if recorded macros are displayed, otherwise <code>false</code>
     */
    public void displayMacrosOnConsole(boolean display) {
        displayMacro = display;
        if (!running && displayMacro) {
            start();
        }
    }
    
    /**
     * Sets flags that indicate if raw macros are displayed on the console for debugging.
     * @param display <code>true</code> if recorded raw macros are displayed, otherwise <code>false</code>
     */
    public void displayRawMacrosOnConsole(boolean display) {
        displayRawMacro = display;
        if (!running && displayRawMacro) {
            start();
        }
    }
    
    /**
     * Sends a change macro event to all the listeners.
     * @param macro the change macro sent to the listeners
     */
    public void notifyMacro(Macro macro) {
        if (displayMacro) {
            MacroConsole.println(macro.toString());
        }
        
        MacroEvent evt = new MacroEvent(MacroEvent.Type.GENERIC_MACRO, macro);
        for (IMacroListener listener : macroListeners) {
            listener.macroAdded(evt);
        }
    }
    
    /**
     * Sends a raw change macro event to all the listeners.
     * @param macro the raw change macro sent to the listeners
     */
    public void notifyRawMacro(Macro macro) {
        if (displayRawMacro) {
            MacroConsole.println("-" + macro.toString());
        }
        
        MacroEvent evt = new MacroEvent(MacroEvent.Type.RAW_MACRO, macro);
        for (IMacroListener listener : macroListeners) {
            listener.rawMacroAdded(evt);
        }
    }
}
