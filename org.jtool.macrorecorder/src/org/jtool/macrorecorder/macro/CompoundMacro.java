/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.json.Json;
import javax.json.JsonObject;

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
     * @param path the path of a file on which this macro was performed
     * @param branch the branch name of a file on which this macro was performed
     * @param commandId the string representing the contents of the macro
     */
    public CompoundMacro(ZonedDateTime time, String action, String path, String branch, CommandMacro macro) {
        super(time, action, path, branch);
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
     * Adds a macro into this compound macro.
     * @param macro the macro to be added
     */
    public void addMacro(Macro macro) {
        macros.add(macro);
    }
    
    /**
     * Removes a macro from this compound macro.
     * @param index the index number of the macro to be removed
     */
    public void removeMacro(int index) {
        macros.remove(index);
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
    public boolean cancelMacro(DocumentMacro macro) {
        int index = getIndexOfCorrespondingMacro(macro);
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
    private int getIndexOfCorrespondingMacro(DocumentMacro macro) {
        for (int i = 0; i < macros.size(); i++) {
            Macro m = macros.get(i);
            if (m instanceof DocumentMacro) {
                DocumentMacro dm = (DocumentMacro)m;
                if (dm.getStart() == macro.getStart() &&
                    dm.getInsertedText().equals(macro.getDeletedText()) &&
                    dm.getDeletedText().equals(macro.getInsertedText())) {
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
            buf.append("\n " + macros.toString());
        }
        return buf.toString();
    }
    
    /**
     * Returns the JSON object of this macro.
     * @return the JSON object
     */
    @Override
    public JsonObject getJSON() {
        JsonObject json = Json.createObjectBuilder()
          .add(MacroJSON.JSON_MACRO_CLASS, getThisClassName())
          .add(MacroJSON.JSON_MACRO_TIME, getTimeAsISOString(time))
          .add(MacroJSON.JSON_MACRO_ACTION, action)
          .add(MacroJSON.JSON_MACRO_PATH, getPath())
          .add(MacroJSON.JSON_ATTR_COMMAND, getCommandId())
          .add(MacroJSON.JSON_ATTR_NUMBER, getMacroNumber())
          .add(MacroJSON.JSON_MACRO_PATH, MacroJSON.getJSONArrayBuilder(macros))
          .build();
        return json;
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
