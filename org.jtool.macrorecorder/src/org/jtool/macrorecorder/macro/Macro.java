/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Stores a macro.
 * @author Katsuhisa Maruyama
 */
public class Macro {
    
    /**
     * The time when this macro was executed.
     */
    protected ZonedDateTime time;
    
    /**
     * The action of this macro.
     */
    protected String action;
    
    /**
     * The path of a resource on which this macro was performed.
     */
    protected MacroPath path;
    
    /**
     * The branch name of a resource on which this macro was performed.
     */
    protected String branch;
    
    /**
     * The collection of raw macros that were recorded.
     */
    protected List<Macro> rawMacros;
    
    /**
     * Creates an object storing information about a macro.
     * @param time the time when this macro was performed
     * @param action the action of this macro
     * @param path the path of a resource on which this macro was performed
     * @param branch the branch of a resource on which this macro was performed
     */
    public Macro(ZonedDateTime time, String action, MacroPath path, String branch) {
        this.time = time;
        this.action = action;
        this.path = path;
        this.branch = branch;
        
        delay();
    }
    
    /**
     * Creates an object storing information about a macro.
     * @param action the action of this macro
     * @param path the path of a resource on which this macro was performed
     * @param branch the branch of a resource on which this macro was performed
     */
    public Macro(String action, MacroPath path, String branch) {
        this.time = ZonedDateTime.now();
        this.action = action;
        this.path = path;
        this.branch = branch;
        
        delay();
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
    public MacroPath getMacroPath() {
        return path;
    }
    
    /**
     * Returns the path name of a resource on which this macro was performed.
     * @return the path name
     */
    public String getPath() {
        return path.getPath();
    }
    
    /**
     * Returns the name of a project containing a resource on which this macro was performed.
     * @return the project name
     */
    public String getProjectName() {
        return path.getProjectName();
    }
    
    /**
     * Returns the path of a source folder containing a resource on which this macro was performed.
     * @return the path of the source holder
     */
    public String getSourcePath() {
        return path.getSourcePath();
    }
    
    /**
     * Returns the branch of a resource on which this macro was performed.
     * @return the branch
     */
    public String getBranch() {
        return branch;
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
        rawMacros = macros;
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
     * Converts a text into its pretty one.
     * @param text the original text
     * @return the text consists of the first four characters not including the new line
     */
    protected String getShortText(String text) {
        if (text == null) {
            return "NULL";
        }
        
        final int LESS_LEN = 20;
        String text2;
        if (text.length() > LESS_LEN) {
            text2 = text.substring(0, LESS_LEN) + "...";
        } else {
            text2 = text;
        }
        return text2.replace('\n', '~');
    }
    
    /**
     * Returns the name of this instance.
     * @return the name of the instance without its package name.
     */
    protected String getClassName() {
        String fqn = this.getClass().getName();
        int sep = fqn.lastIndexOf('.');
        if (sep != -1) {
            return fqn.substring(sep + 1, fqn.length());
        }
        return fqn;
    }
    
    /**
     * Returns the string for printing.
     * @return the string for printing
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{" + getClassName() + "} ");
        buf.append(getFormatedTime(time));
        buf.append(" " + action);
        buf.append(" path=[" + path.getPath() + "]");
        buf.append(" branch=[" + branch + "]");
        return buf.toString();
    }
}
