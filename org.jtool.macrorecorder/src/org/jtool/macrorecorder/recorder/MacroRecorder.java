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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
     * A flag that indicates if this macro recorder is running.
     */
    private boolean running = false;
    
    /**
     * A flag that indicates if recorded macros are displayed.
     */
    private boolean displayMacro = false;
    
    /**
     * A flag that indicates if recorded raw macros are displayed.
     */
    private boolean displayRawMacro = false;
    
    /**
     * The URL of a server which macros are posted to.
     */
    private String urlForPost = null;
    
    /**
     * A handler that displays recorded change macros.
     */
    private IMacroListener macroHandlerForDebugging = null;
    
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
        if (macroNotifiers.size() == 0) {
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
        if (macroNotifiers.size() > 0) {
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
     * Sends a change macro event to all the listeners.
     * @param listener a listener that receives the change macro
     * @param macro the change macro sent to the listeners
     */
    public void notifyMacro(IMacroListener listener, Macro macro) {
        MacroEvent evt = new MacroEvent(MacroEvent.Type.GENERIC_MACRO, macro);
        listener.macroAdded(evt);
    }
    
    /**
     * Sends a raw change macro event to all the listeners.
     * @param listener a listener that receives the raw change macro
     * @param macro the raw change macro sent to the listeners
     */
    public void notifyRawMacro(IMacroListener listener, Macro macro) {
        MacroEvent evt = new MacroEvent(MacroEvent.Type.RAW_MACRO, macro);
        listener.rawMacroAdded(evt);
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
     * Sets a flag that indicates if macros are displayed on the console for debugging.
     * @param display <code>true</code> if recorded macros are displayed, otherwise <code>false</code>
     */
    public void displayMacrosOnConsole(boolean display) {
        setMacroHandlerForDebugging(display || displayRawMacro || urlForPost != null);
        displayMacro = display;
        
        if (displayMacro) {
            start();
        } else {
            stop();
        }
    }
    
    /**
     * Sets a flag that indicates if raw macros are displayed on the console for debugging.
     * @param display <code>true</code> if recorded raw macros are displayed, otherwise <code>false</code>
     */
    public void displayRawMacrosOnConsole(boolean display) {
        setMacroHandlerForDebugging(display || displayMacro || urlForPost != null);
        displayRawMacro = display;
        
        if (displayRawMacro) {
            start();
        } else {
            stop();
        }
    }
    
    /**
     * Sets the URL of a server for debugging.
     * @param url the URL of a server which macros are posted to
     */
    public void postMacros(String url) {
        setMacroHandlerForDebugging(url != null || displayMacro || displayRawMacro);
        urlForPost = url;
        
        if (urlForPost != null) {
            start();
        } else {
            stop();
        }
    }
    
    /**
     * Adds or removes a handler that displays recorded change macros.
     * @param addition <code>true</code> if the handler is added, otherwise <code>false</code>
     */
    private void setMacroHandlerForDebugging(boolean addition) {
        if (macroHandlerForDebugging == null && addition) {
            macroHandlerForDebugging = new IMacroListener() {
                
                /**
                 * Receives an event when a new change macro is added.
                 * @param evt the macro event
                 */
                public void macroAdded(MacroEvent evt) {
                    if (displayMacro) {
                        MacroConsole.println(evt.getMacro().getDescription());
                    }
                    if (urlForPost != null) {
                        postMacro(evt.getMacro().getDescription());
                    }
                }
                
                /**
                 * Receives an event when a new raw change macro is added.
                 * @param evt the raw macro event
                 */
                public void rawMacroAdded(MacroEvent evt) {
                    if (displayRawMacro) {
                        MacroConsole.println("- " + evt.getMacro().getDescription());
                    }
                    if (urlForPost != null) {
                        postMacro("- " + evt.getMacro().getDescription());
                    }
                }
            };
            addMacroListener(macroHandlerForDebugging);
            
        } if (macroHandlerForDebugging != null && !addition) {
            removeMacroListener(macroHandlerForDebugging);
            macroHandlerForDebugging = null;
        }
    }
    
    /**
     * Posts macros to a server.
     * @param description the description of the macro to be posted
     */
    private void postMacro(String description) {
        try {
            URL url = new URL(urlForPost);
            HttpURLConnection connection = null;
            
            try {
                connection = (HttpURLConnection)url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/plain; charset=utf-8");
                connection.setRequestProperty("Content-Length", String.valueOf(description.length()));
                
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
                writer.write(description);
                writer.flush();
                
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    MacroConsole.println("POST FAILURE: " + description);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            
        } catch (IOException e) {
            MacroConsole.println("POST FAILURE: " + description);
        }
    }
}
