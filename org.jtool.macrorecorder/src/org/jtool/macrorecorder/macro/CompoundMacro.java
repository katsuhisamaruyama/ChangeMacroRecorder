/*
 *  Copyright 2016-2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Stores a compound macro that contains macros.
 * @author Katsuhisa Maruyama
 */
public class CompoundMacro extends Macro {
    
    /**
     * The collection of macros contained in this compound macro.
     */
    private List<Macro> macros = new ArrayList<Macro>();
    
    /**
     * The command macro that causes this trigger macro.
     */
    private CommandMacro commandMacro = null;
    
    /**
     * Creates an object storing information about a compound macro.
     * @param time the time when this macro was performed
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     * @param commandId the string representing the contents of the macro
     */
    public CompoundMacro(ZonedDateTime time, String action, MacroPath mpath, CommandMacro macro) {
        super(time, action, mpath);
        this.commandMacro = macro;
    }
    
    /**
     * Returns the command macro that causes this compound macro.
     * @return the command macro for the compound macro
     */
    public CommandMacro getCommandMacro() {
        return commandMacro;
    }
    
    /**
     * Returns information about the command macro that causes this trigger macro.
     * @return the string representing the contents of the command macro
     */
    public String getCommandId() {
        if (commandMacro != null) {
            return commandMacro.getCommandId();
        }
        return "";
    }
    
    /**
     * Returns a macro at the specified position into this compound macro.
     * @param index the index number of the macro to return
     * @return the macro at the specified position
     */
    public Macro getMacro(int index) {
        if (0 <= index && index < macros.size()) {
            return macros.get(index);
        }
        return null;
    }
    
    /**
     * Adds a macro into this compound macro.
     * @param macro the macro to be added
     */
    public void addMacro(Macro macro) {
        macros.add(macro);
    }
    
    /**
     * Inserts a macro at the specified position into this compound macro.
     * @param index the index number at which the macro is to be inserted
     * @param macro the macro to be inserted
     */
    public void addMacro(int index, Macro macro) {
        if (0 <= index && index < macros.size()) {
            macros.add(index, macro);
        }
    }
    
    /**
     * Removes a macro from this compound macro.
     * @param index the index number of the macro to be removed
     */
    public void removeMacro(int index) {
        if (0 <= index && index < macros.size()) {
            macros.remove(index);
        }
    }
    
    /**
     * Returns the number of macros contained in this macro.
     * @return the the number of the contained macros
     */
    public int getMacroNumber() {
        return macros.size();
    }
    
    /**
     * Removes a macro that will be canceled by a given macro.
     * @param macro the macro that cancels the macro stored in this compound macro
     * @return <code>true</code> if the cancellation succeeded, otherwise <code>false</code> 
     */
    public boolean cancelMacro(CancelMacro cmacro) {
        int index = getIndexOfCorrespondingMacro(cmacro);
        if (index < 0) {
            return false;
        }
        
        macros.remove(index);
        return true;
    }
    
    /**
     * Returns the collection of macros contained in this compound macro.
     * @return the the collection of the contained macros
     */
    public List<Macro> getMacros() {
        return macros;
    }
    
    /**
     * Obtains the index number of the macro corresponding to a given macro.
     * @param macro the given macro
     * @return the index number of the corresponding macro
     */
    private int getIndexOfCorrespondingMacro(CancelMacro cmacro) {
        for (int i = 0; i < macros.size(); i++) {
            Macro macro = macros.get(i);
            if (macro instanceof DocumentMacro) {
                DocumentMacro dmacro = (DocumentMacro)macro;
                if (dmacro.getStart() == cmacro.getStart() &&
                    dmacro.getInsertedText().equals(cmacro.getDeletedText()) &&
                    dmacro.getDeletedText().equals(cmacro.getInsertedText())) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Sorts the macros in time order.
     */
    public void sort() {
        Collections.sort(macros, new Comparator<Macro>() {
            
            /**
             * Compares two macros for order.
             * @param macro1 - the first macro to be compared
             * @param macro2 - the second macro to be compared
             */
            public int compare(Macro macro1, Macro macro2) {
                ZonedDateTime time1 = macro1.getTime();
                ZonedDateTime time2 = macro2.getTime();
                
                if (time1.isAfter(time2)) {
                    return 1;
                } else if (time1.isBefore(time2)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }
    
    /**
     * Returns the textual description of this macro.
     * @return the textual description
     */
    @Override
    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        buf.append("{" + getThisClassName() + "} ");
        buf.append(getFormatedTime(time));
        buf.append(" " + action);
        buf.append(" commandId=[" + getCommandId() + "]");
        buf.append(" num=[" + getMacroNumber() + "]");
        for (Macro macros: macros) {
            buf.append("\n !" + macros.toString());
        }
        return buf.toString();
    }
    
    /**
     * Returns a string that represents a JSON object for a macro.
     * @return the JSON string representation
     */
    @Override
    public String getJSON() {
        JsonObjectBuilder builder = MacroJSON.getJSONObjectBuikder(this)
          .add(MacroJSON.JSON_MACRO_CLASS, getThisClassName())
          .add(MacroJSON.JSON_MACRO_TIME, getTimeAsISOString(time))
          .add(MacroJSON.JSON_MACRO_ACTION, action)
          .add(MacroJSON.JSON_MACRO_PATH, getPath())
          .add(MacroJSON.JSON_ATTR_COMMAND, getCommandId())
          .add(MacroJSON.JSON_ATTR_NUMBER, getMacroNumber());
        JsonArrayBuilder array = MacroJSON.getJSONArrayBuilder(macros);
        if (array != null) {
            builder.add(MacroJSON.JSON_MACRO_PATH, array);
        }
        JsonObject json = builder.build();
        return MacroJSON.stringify(json);
    }
    
    /**
     * Returns the string for printing.
     * @return the string for printing
     */
    @Override
    public String toString() {
        return getDescription();
    }
}
