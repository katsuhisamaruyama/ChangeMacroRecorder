/*
 *  Copyright 2017-2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * Displays a dialog with a message.
 * @author Katsuhisa Maruyama
 */
public class MacroConsole {
    
    /**
     * The name of the dedicated console.
     */
    private static final String CONSOLE_NAME = "MacroRecorder Console";
    
    /**
     * The stream of the dedicated console.
     */
    private static MessageConsoleStream consoleStream;
    
    /**
     * Shows the dedicated console.
     */
    private static void showConsole() {
        IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
        IConsole[] consoles = consoleManager.getConsoles();
        MessageConsole console = null;
        for (int i = 0; i < consoles.length; i++) {
            if (CONSOLE_NAME.equals(consoles[i].getName())) {
                console = (MessageConsole)consoles[i];
                break;
            }
        }
        if (console == null) {
            console = new MessageConsole(CONSOLE_NAME, null);
        }
        
        consoleManager.addConsoles(new MessageConsole[] { console });
        consoleManager.showConsoleView(console);
        consoleStream = console.newMessageStream();
    }
    
    /**
     * Displays macros on the dedicated console.
     * @param msg the message to be displayed
     */
    public static void print(String msg) {
        if (consoleStream == null) {
            showConsole();
        }
        
        consoleStream.print(msg);
    }
    
    /**
     * Displays macros on the dedicated console per line.
     * @param msg the message to be displayed
     */
    public static void println(String msg) {
        if (consoleStream == null) {
            showConsole();
        }
        
        consoleStream.println(msg);
    }
}
