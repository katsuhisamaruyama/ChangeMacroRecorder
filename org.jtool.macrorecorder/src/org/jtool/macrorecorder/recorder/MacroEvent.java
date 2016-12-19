/*
 *  Copyright 2016
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.jtool.macrorecorder.macro.Macro;

/**
 * Manages an event containing a macro.
 * @author Katsuhisa Maruyama
 */
public class MacroEvent {
    
    /**
     * The type of this event.
     */
    public enum Type {
        GENERIC_MACRO, RAW_MACRO;
    }
    
    /**
     * A macro sent to listeners.
     */
    private Macro macro;
    
    /**
     * The type of a macro sent to listeners.
     */
    private Type type;
    
    /**
     * Creates an object containing a macro.
     * @param type the type of the macro
     * @param macro the macro
     */
    public MacroEvent(Type type, Macro macro) {
        assert type != null;
        assert macro != null;
        this.type = type;
        this.macro = macro;
    }
    
    /**
     * Returns the type of an event that listeners receive.
     * @return the type of the received event
     */
    public Type getEventType() {
        return type;
    }
    
    /**
     * Returns the macro of an event that listeners receive.
     * @return the macro contained in the received event
     */
    public Macro getMacro() {
        return macro;
    }
    
    /**
     * Returns the string for printing, which does not contain a new line character at its end.
     * @return the string for printing
     */
    @Override
    public String toString() {
        return macro.toString();
    }
}
