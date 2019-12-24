/*
 *  Copyright 2016-2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import javax.json.JsonObject;

/**
 * Stores a macro.
 * @author Katsuhisa Maruyama
 */
public class Macro {
    
    /**
     * The time when this macro was executed.
     * There is no change operations with the same time within the same file
     * (with the same path and the same branch).
     */
    protected ZonedDateTime time;
    
    /**
     * The action of this macro.
     */
    protected String action;
    
    /**
     * Information about the path a resource on which this macro was performed.
     */
    protected MacroPath macroPath;
    
    /**
     * The collection of raw macros that were recorded.
     */
    protected List<Macro> rawMacros;
    
    /**
     * Creates an object storing information about a macro.
     * @param time the time when this macro was performed
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     */
    protected Macro(ZonedDateTime time, String action, MacroPath mpath) {
        this.time = time;
        this.action = action;
        this.macroPath = mpath;
        
        delay();
    }
    
    /**
     * Creates an object storing information about a macro.
     * @param action the action of this macro
     * @param mpath the information about the path a resource on which this macro was performed
     */
    protected Macro(String action, MacroPath mpath) {
        this(ZonedDateTime.now(), action, mpath);
    }
    
    /**
     * Creates a clone of this macro.
     */
    @Override
    public Macro clone() {
        return new Macro(time, action, macroPath);
    }
    
    /**
     * Delay so as to prohibit macros with the same time stamp
     */
    private void delay() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }
    }
    
    /**
     * Returns the time when this macro was executed.
     * @return the time of this macro
     */
    public ZonedDateTime getTime() {
        return time;
    }
    
    /**
     * Returns the time when this macro was executed.
     * @return the time of this macro
     */
    public long getTimeAsLong() {
        return time.toInstant().toEpochMilli();
    }
    
    /**
     * Sets the action of this macro
     * @param action the action of the macro
     */
    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * Returns the action of this macro
     * @return the action of the macro
     */
    public String getAction() {
        return action;
    }
    
    /**
     * Returns the path of a resource on which this macro was performed.
     * @return the path
     */
    public String getPath() {
        return macroPath.getPath();
    }
    
    /**
     * Returns the branch of a resource on which this macro was performed.
     * @return the branch
     */
    public String getBranch() {
        return macroPath.getBranch();
    }
    
    /**
     * Returns the information about the path a resource on which this macro was performed.
     * @return the path information
     */
    public MacroPath getMacroPath() {
        return macroPath;
    }
    
    /**
     * Returns the name of a project containing a resource on which this macro was performed.
     * @return the project name
     */
    public String getProjectName() {
        return macroPath.getProjectName();
    }
    
    /**
     * Returns the name of a package containing a resource on which this macro was performed.
     * @return the package name
     */
    public String getPackageName() {
        return macroPath.getPackageName();
    }
    
    /**
     * Returns the name of a file containing a resource on which this macro was performed.
     * @return the file name
     */
    public String getFileName() {
        return macroPath.getFileName();
    }
    
    /**
     * Obtains the name of a user.
     * @return the user name
     */
    public String getUserName() {
        return System.getProperty("user.name");
    }
    
    /**
     * Sets the collection of raw macros that were recorded.
     * @param macros the raw macros to be stored
     */
    public void setRawMacros(List<Macro> macros) {
        rawMacros = new ArrayList<Macro>();
        for (Macro macro :  macros) {
            if (macro == this) {
                rawMacros.add(macro.clone());
            } else {
                rawMacros.add(macro);
            }
        }
    }
    
    /**
     * Returns the collection of raw macros that were recorded.
     * @return raw macros
     */
    public List<Macro> getRawMacros() {
        return rawMacros;
    }
    
    /**
     * Obtains the formated time information.
     * @param time the time information
     * @return the formatted string of the time
     */
    protected String getFormatedTime(ZonedDateTime t) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        return t.format(formatter);
    }
    
    /**
     * Returns the string corresponding time.
     * @param time the time information
     * @return the string corresponding the time
     */
    protected String getTimeAsISOString(ZonedDateTime t) {
        return t.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
    
    /**
     * Converts a text into its pretty one.
     * @param text the original text
     * @return the text consists of the first four characters not including the new line
     */
    protected String getShortText(String text) {
        if (text == null) {
            return "NULL";
        }
        
        final int LESS_LEN = 100;
        String text2;
        if (text.length() > LESS_LEN) {
            text2 = text.substring(0, LESS_LEN) + "...";
        } else {
            text2 = text;
        }
        return text2.replace('\n', '~');
    }
    
    /**
     * Obtains the name of this instance.
     * @return the name of the instance without its package name.
     */
    protected String getThisClassName() {
        String fqn = this.getClass().getName();
        int sep = fqn.lastIndexOf('.');
        if (sep != -1) {
            return fqn.substring(sep + 1, fqn.length() - 5);
        }
        return fqn;
    }
    
    /**
     * Obtains the textual description of this macro.
     * @return the textual description
     */
    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        buf.append("{" + getThisClassName() + "} ");
        buf.append(getFormatedTime(time));
        buf.append(" " + action);
        buf.append(" path=[" + getPath() + "]");
        buf.append(" resource=[" + getProjectName() + "/" + getPackageName() + "/" + getFileName() + "]");
        buf.append(" branch=[" + getBranch() + "]");
        return buf.toString();
    }
    
    /**
     * Obtains a JSON object that stores information on this macro.
     * @return the JSON object
     */
    public JsonObject getJSON() {
        JsonObject json = MacroJSON.getJSONObjectBuilder(this).build();
        return json;
    }
    
    /**
     * Obtains the string representation of a JSON object that stores information on this macro.
     * @return the string representation of the JSON object
     */
    public String getJSONString() {
        return MacroJSON.stringify(getJSON());
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
