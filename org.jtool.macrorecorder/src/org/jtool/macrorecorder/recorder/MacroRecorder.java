/*
 *  Copyright 2016
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
     * Creates an object that records macros.
     */
    private MacroRecorder() {
        IMacroCompressor defaultCompressor = new MacroCompressor();
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
     * Returns the compressor that compresses macros.
     * @return the macro compressor
     */
    public IMacroCompressor getMacroCompressor() {
        return internalRecorder.getMacroCompressor();
    }
    
    /**
     * Sets a compressor that compresses macros.
     * @param compressor the compressor
     */
    public void setMacroCompressor(IMacroCompressor compressor) {
        assert compressor != null;
        internalRecorder.setMacroCompressor(compressor);
    }
    
    /**
     * Starts the recording of document macros performed on an editor.
     */
    public void start() {
        internalRecorder.start();
    }
    
    /**
     * Stops the recording of menu and document macros.
     */
    public void stop() {
        internalRecorder.stop();
    }
    
    /**
     * Adds a listener that receives a macro event.
     * @param listener the event listener to be added
     */
    public void addMacroListener(IMacroListener listener) {
        assert listener != null;
        macroListeners.add(listener);
    }
    
    /**
     * Removes a listener that receives a macro event.
     * @param listener the event listener to be removed
     */
    public void removeMacroListener(IMacroListener listener) {
        assert listener != null;
        macroListeners.remove(listener);
    }
    
    /**
     * Sends a macro event to all the listeners.
     * @param macro the macro sent to the listeners
     */
    public void notifyMacro(Macro macro) {
        MacroEvent evt = new MacroEvent(MacroEvent.Type.GENERIC_MACRO, macro);
        for (IMacroListener listener : macroListeners) {
            listener.macroAdded(evt);
        }
    }
    
    /**
     * Sends a raw macro event to all the listeners.
     * @param macro the raw macro sent to the listeners
     */
    public void notifyRawMacro(Macro macro) {
        
        
        MacroEvent evt = new MacroEvent(MacroEvent.Type.RAW_MACRO, macro);
        for (IMacroListener listener : macroListeners) {
            listener.rawMacroAdded(evt);
        }
    }
}
