/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.recorder;

import org.jtool.macrorecorder.macro.Macro;

/**
 * Manages an event containing a change macro.
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
     * A change macro sent to listeners.
     */
    private Macro macro;
    
    /**
     * The type of a change macro sent to listeners.
     */
    private Type type;
    
    /**
     * Creates an object containing a change macro.
     * @param type the type of the change macro
     * @param macro the change macro
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
     * Returns the change macro of an event that listeners receive.
     * @return the change macro contained in the received event
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
