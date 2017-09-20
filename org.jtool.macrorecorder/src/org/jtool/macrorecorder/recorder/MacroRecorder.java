/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.jtool.macrorecorder.internal.recorder.Recorder;
import org.jtool.macrorecorder.macro.Macro;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

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
     * The collection of macro handlers that are loaded from the extension point.
     */
    private Set<IMacroHandler> macroHandlers = new HashSet<IMacroHandler>();
    
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
        
        macroHandlers = MacroHandlerLoader.load();
    }
    
    /**
     * Returns the single instance of this recorder.
     * @return the single instance
     */
    public static IMacroRecorder getInstance() {
        return instance;
    }
    
    /**
     * Adds a listener that receives a change macro event.
     * @param listener the event listener to be added
     */
    @Override
    public void addMacroListener(IMacroListener listener) {
        assert listener != null;
        macroListeners.add(listener);
    }
    
    /**
     * Removes a listener that receives a change macro event.
     * @param listener the event listener to be removed
     */
    @Override
    public void removeMacroListener(IMacroListener listener) {
        assert listener != null;
        macroListeners.remove(listener);
    }
    
    /**
     * Starts the recording of change macros.
     */
    @Override
    public void start() {
        if (!displayMacro && !displayRawMacro && macroListeners.size() == 0) {
            return;
        }
        
        if (!running) {
            internalRecorder.start();
        }
        running = true;
    }
    
    /**
     * Stops the recording of change macros.
     */
    @Override
    public void stop() {
        if (displayMacro || displayRawMacro || macroListeners.size() > 0) {
            return;
        }
        
        if (running) {
            internalRecorder.stop();
        }
        running = false;
    }
    
    /**
     * Registers macro handlers that receives change macros.
     */
    public void registerHandlers() {
        for (IMacroHandler handler : macroHandlers) {
            handler.initialize();
            addMacroListener(handler);
        }
    }
    
    /**
     * Unregisters macro handlers that receives change macros.
     */
    public void unregisterHandlers() {
        for (IMacroHandler handler : macroHandlers) {
            removeMacroListener(handler);
            handler.terminate();
        }
    }
    
    /**
     * Tests if this macro recorder is running.
     * @return <code>true</code> if this macro recorder is running, otherwise <code>false</code>
     */
    @Override
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Returns the compressor that compresses change macros.
     * @return the macro compressor
     */
    protected MacroCompressor getMacroCompressor() {
        return internalRecorder.getMacroCompressor();
    }
    
    /**
     * Sets characters that delimit recorded document change macros.
     * @param chars characters representing delimiters
     */
    @Override
    public void setDelimiter(char[] chars) {
        getMacroCompressor().setDelimiter(chars);
    }
    
    /**
     * Sets a compressor that compresses change macros.
     * @param compressor the compressor
     */
    @Override
    public void setMacroCompressor(MacroCompressor compressor) {
        assert compressor != null;
        internalRecorder.setMacroCompressor(compressor);
    }
    
    /**
     * Sets flags that indicate if macros are displayed on the console for debugging.
     * @param display <code>true</code> if recorded macros are displayed, otherwise <code>false</code>
     */
    public void displayMacrosOnConsole(boolean display) {
        displayMacro = display;
        if (displayMacro) {
            start();
        } else {
            stop();
        }
    }
    
    /**
     * Sets flags that indicate if raw macros are displayed on the console for debugging.
     * @param display <code>true</code> if recorded raw macros are displayed, otherwise <code>false</code>
     */
    public void displayRawMacrosOnConsole(boolean display) {
        displayRawMacro = display;
        if (displayRawMacro) {
            start();
        } else {
            stop();
        }
    }
    
    /**
     * Sends a change macro event to all the listeners.
     * @param macro the change macro sent to the listeners
     */
    public void notifyMacro(Macro macro) {
        if (displayMacro) {
            MacroConsole.println(macro.getDescription());
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
            MacroConsole.println("-" + macro.getDescription());
        }
        
        MacroEvent evt = new MacroEvent(MacroEvent.Type.RAW_MACRO, macro);
        for (IMacroListener listener : macroListeners) {
            listener.rawMacroAdded(evt);
        }
    }
}
