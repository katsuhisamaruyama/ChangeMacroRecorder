/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.util.Map;
import javax.json.JsonObject;

/**
 * Stores a refactoring macro.
 * @author Katsuhisa Maruyama
 */
public class RefactoringMacro extends Macro {
    
    /**
     * The type of this macro.
     */
    public enum Action {
        ABOUT_TO_PERFORM, ABOUT_TO_UNDO, ABOUT_TO_REDO, PERFORMED, UNDONE, REDONE, NONE;
    }
    
    /**
     * The name of a refactoring.
     */
    private String name;
    
    /**
     * The map that stores arguments of a refactoring.
     */
    private Map<String, String> argumentMap;
    
    /**
     * The starting point of the text that is contained the selection.
     */
    private int selectionStart = -1;
    
    /**
     * The text that is contained the selection.
     */
    private String selectionText = "";
    
    /**
     * Creates an object storing information about a refactpring macro.
     * @param action the action of this macro
     * @param path the path of a file or a package on which this macro was performed
     * @param branch the branch name of a file or a package on which this macro was performed
     * @param name the name of a refactoring
     * @param map the map that stores arguments of a refactoring
     */
    public RefactoringMacro(Action action, String path, String branch, String name, Map<String, String> map) {
        super(action.toString(), path, branch);
        this.name = name;
        this.argumentMap = map;
    }
    
    /**
     * Returns the name of a refactoring.
     * @return the refactoring name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns map that stores arguments of a refactoring.
     * @return the refactoring arguments
     */
    public Map<String, String> getArguments() {
        return argumentMap;
    }
    
    /**
     * Returns the value corresponding to a given key in the refactoring arguments.
     * @return the value string
     */
    public String getArgument(String key) {
        return argumentMap.get(key);
    }
    
    /**
     * Sets the starting point of the text that is contained the selection.
     * @param start the starting point of the text
     */
    public void setSelectionStart(int start) {
        selectionStart = start;
    }
    
    /**
     * Returns the starting point of the text that is contained the selection.
     * @return the starting point of the text
     */
    public int getSelectionStart() {
        return selectionStart;
    }
    
    /**
     * Returns the ending point of the text that is contained the selection.
     * @return the ending point of the text
     */
    public int getSelectionEnd() {
        return selectionStart + selectionText.length() - 1;
    }
    
    /**
     * Sets the text that is contained the selection.
     * @param text the selected text
     */
    public void setSelectionText(String text) {
        selectionText = text;
    }
    
    /**
     * Returns the text that is contained the selection.
     * @return text the selected text
     */
    public String getSelectionText() {
        return selectionText;
    }
    
    /**
     * Tests if this macro represents the beginning of refactoring.
     * @return <code>true</code> if this macro represents the beginning of refactoring, otherwise <code>false</code>
     */
    public boolean isBegin() {
        return action.equals(Action.ABOUT_TO_PERFORM.toString()) ||
               action.equals(Action.ABOUT_TO_UNDO.toString()) ||
               action.equals(Action.ABOUT_TO_REDO.toString());
    }
    
    /**
     * Tests if this macro represents the end of refactoring.
     * @return <code>true</code> if this macro represents the end of refactoring, otherwise <code>false</code>
     */
    public boolean isEnd() {
        return action.equals(Action.PERFORMED.toString()) ||
               action.equals(Action.UNDONE.toString()) ||
               action.equals(Action.REDONE.toString());
    }
    
    /**
     * Tests if this macro represents the undo of refactoring.
     * @return <code>true</code> if this macro represents the undo of refactoring, otherwise <code>false</code>
     */
    public boolean isUndo() {
        return action.equals(Action.ABOUT_TO_UNDO.toString()) ||
               action.equals(Action.UNDONE.toString());
    }
    
    /**
     * Tests if this macro represents the redo of refactoring.
     * @return <code>true</code> if this macro represents the redo of refactoring, otherwise <code>false</code>
     */
    public boolean isRedo() {
        return action.equals(Action.ABOUT_TO_REDO.toString()) ||
               action.equals(Action.REDONE.toString());
    }
    
    /**
     * Returns the textual description of this macro.
     * @return the textual description
     */
    @Override
    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.getDescription());
        
        buf.append(" name=[" + name + "]");
        if (selectionText.length() > 0) {
            buf.append(" range=[" + getSelectionStart() + "-" + getSelectionEnd() + "]");
            buf.append(" code=[" + getShortText(selectionText) + "]");
        }
        return buf.toString();
    }
    
    /**
     * Returns the JSON object of this macro.
     * @return the JSON object
     */
    @Override
    public JsonObject getJSON() {
        JsonObject json = super.getJSONObjectBuikderOfMacro()
          .add(MacroJSON.JSON_ATTR_REFACTORING_NAME, name)
          .add(MacroJSON.JSON_ATTR_REFACTORING_START, getSelectionStart())
          .add(MacroJSON.JSON_ATTR_REFACTORING_END, getSelectionEnd())
          .add(MacroJSON.JSON_ATTR_CODE, selectionText)
          .add(MacroJSON.JSON_ATTR_COPYED_TEXT, super.getJSONArrayBuilderOfMap(argumentMap))
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
