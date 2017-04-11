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
    private boolean running;
    
    /**
     * Creates an object that records macros.
     */
    private MacroRecorder() {
        IMacroCompressor defaultCompressor = new MacroCompressor();
        internalRecorder = new Recorder(this, defaultCompressor);
        
        Message.showConsole();
        displayMacro = false;
        displayRawMacro = false;
        running = false;
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
    public void setMacroCompressor(IMacroCompressor compressor) {
        assert compressor != null;
        internalRecorder.setMacroCompressor(compressor);
    }
    
    /**
     * Starts the recording of change macros.
     */
    public void start() {
        running = true;
        internalRecorder.start();
    }
    
    /**
     * Stops the recording of change macros.
     */
    public void stop() {
        running = false;
        internalRecorder.stop();
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
     * @param displayMacro a flag that indicates if recorded macros are displayed
     * @param displayRawMacro a flag that indicates if recorded raw macros are displayed
     */
    public void displayMacrosOnConsole(boolean displayMacro, boolean displayRawMacro) {
        this.displayMacro = displayMacro;
        this.displayRawMacro = displayRawMacro;
        
        if (displayMacro || displayRawMacro) {
            if (!running) {
                start();
            }
        } else {
            if (running) {
                stop();
            }
        }
    }
    
    /**
     * Sends a change macro event to all the listeners.
     * @param macro the change macro sent to the listeners
     */
    public void notifyMacro(Macro macro) {
        if (displayMacro) {
            Message.println(macro.toString());
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
            Message.println(macro.toString());
        }
        
        MacroEvent evt = new MacroEvent(MacroEvent.Type.RAW_MACRO, macro);
        for (IMacroListener listener : macroListeners) {
            listener.rawMacroAdded(evt);
        }
    }
}
