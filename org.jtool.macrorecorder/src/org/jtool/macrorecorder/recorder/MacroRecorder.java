/*
 *  Copyright 2016-2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.MacroHandlerLoader;
import org.jtool.macrorecorder.internal.recorder.Notifier;
import org.jtool.macrorecorder.internal.recorder.Recorder;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
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
     * The collection of notifiers that send change macros.
     */
    private List<Notifier> macroNotifiers = new ArrayList<Notifier>();
    
    /**
     * The collection of macro handlers that are loaded from the extension point.
     */
    private Set<IMacroHandler> macroHandlers = new HashSet<IMacroHandler>();
    
    /**
     * A flag that indicates if recorded macros are displayed.
     */
    private boolean displayMacro = false;
    
    /**
     * A flag that indicates if recorded raw macros are displayed.
     */
    private boolean displayRawMacro = false;
    
    /**
     * A flag that indicates if this macro recorder is running.
     */
    private boolean running = false;
    
    /**
     * Creates an object that records macros.
     */
    private MacroRecorder() {
        internalRecorder = new Recorder(this);
        
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
        macroNotifiers.add(new Notifier(listener, new DocMacroCombinator()));
        
        start();
    }
    
    /**
     * Removes a listener that receives a change macro event.
     * @param listener the event listener to be removed
     */
    @Override
    public void removeMacroListener(IMacroListener listener) {
        assert listener != null;
        Notifier notifier = getNotifier(listener);
        if (notifier != null) {
            macroNotifiers.remove(notifier);
        }
        
        stop();
    }
    
    /**
     * Obtains a notifier that corresponds to a listener.
     * @param listener the listener
     * @return the corresponding notifier
     */
    private Notifier getNotifier(IMacroListener listener) {
        for (Notifier notifier : macroNotifiers) {
            if (notifier.getMacroListener().equals(listener)) {
                return notifier;
            }
        }
        return null;
    }
    
    /**
     * Starts the recording of change macros.
     */
    private void start() {
        if (!displayMacro && !displayRawMacro && macroNotifiers.size() == 0) {
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
    private void stop() {
        if (displayMacro || displayRawMacro || macroNotifiers.size() > 0) {
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
            addMacroListener(handler);
            handler.initialize();
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
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Returns notifiers that send macro events.
     * @return the collection of notifiers
     */
    public List<Notifier> getNotifiers() {
        return macroNotifiers;
    }
    
    /**
     * Sets characters that delimit recorded document change macros.
     * @param a listener that receives a change macro event
     * @param delimiters string that contains delimiter characters
     * @return <code>true</code> if the delimiters are attached to a combinator corresponding to the listener, otherwise <code>false</code>
     */
    @Override
    public boolean setDelimiters(IMacroListener listener, String delimiters) {
        assert listener != null;
        Notifier notifier = getNotifier(listener);
        if (notifier == null) {
            return false;
        }
        
        IDocMacroCombinator combinator = notifier.getDocMacroCombinator();
        if (combinator instanceof DocMacroCombinator) {
            ((DocMacroCombinator)combinator).setDelimiter(delimiters);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Sets a combinator that combines document macros.
     * @param a listener that receives a change macro event
     * @param combinator the combinator
     * @return <code>true</code> if the combinator is attached to the listener, otherwise <code>false</code>
     */
    @Override
    public boolean setDocMacroCombinator(IMacroListener listener, IDocMacroCombinator combinator) {
        assert listener != null;
        Notifier notifier = getNotifier(listener);
        if (notifier == null) {
            return false;
        }
        
        notifier.setDocMacroCombinator(combinator);
        return true;
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
     * @param listener a listener that receives the change macro
     * @param macro the change macro sent to the listeners
     */
    public void notifyMacro(IMacroListener listener, Macro macro) {
        if (displayMacro) {
            MacroConsole.println(macro.getDescription());
        }
        
        MacroEvent evt = new MacroEvent(MacroEvent.Type.GENERIC_MACRO, macro);
        listener.macroAdded(evt);
    }
    
    /**
     * Sends a raw change macro event to all the listeners.
     * @param listener a listener that receives the raw change macro
     * @param macro the raw change macro sent to the listeners
     */
    public void notifyRawMacro(IMacroListener listener, Macro macro) {
        if (displayRawMacro) {
            MacroConsole.println("-" + macro.getDescription());
        }
        
        MacroEvent evt = new MacroEvent(MacroEvent.Type.RAW_MACRO, macro);
        listener.rawMacroAdded(evt);
    }
}
