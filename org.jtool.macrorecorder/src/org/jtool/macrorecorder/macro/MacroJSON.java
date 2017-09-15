/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

/**
 * The constants of JSON attributes for representing information about macros.
 * @author Katsuhisa Maruyama
 */
public interface MacroJSON {
    
    public static final String JSON_MACRO_CLASS = "class";
    public static final String JSON_MACRO_TIME = "time";
    public static final String JSON_MACRO_ACTION = "action";
    public static final String JSON_MACRO_PATH = "path";
    public static final String JSON_MACRO_BRANCH = "branch";
    public static final String JSON_MACRO_PROJECT_NAME = "project";
    public static final String JSON_MACRO_PACKAGE_NAME = "package";
    public static final String JSON_MACRO_FILE_NAME = "file";
    public static final String JSON_RAW_MACROS = "rawMacros";
    
    public static final String JSON_ATTR_COMMAND = "commandId";
    public static final String JSON_ATTR_OFFSET = "offset";
    public static final String JSON_ATTR_INSERTED_TEXT = "itext";
    public static final String JSON_ATTR_DELETED_TEXT = "dtext";
    public static final String JSON_ATTR_COPYED_TEXT = "ctext";
    public static final String JSON_ATTR_CODE = "code";
    public static final String JSON_ATTR_CHARSET = "charset";
    public static final String JSON_ATTR_SRD_DST_PATH = "sdpath";
    public static final String JSON_ATTR_REFACTORING_NAME = "refname";
    public static final String JSON_ATTR_REFACTORING_START = "refstart";
    public static final String JSON_ATTR_REFACTORING_END = "refend";
    public static final String JSON_ATTR_RESOURCE_TARGET = "target";
    public static final String JSON_ATTR_TIMING = "timing";
    
    public static final String JSON_ATTR_NUMBER = "num";
    public static final String JSON_PRIMITIVE_MACROS = "rawMacros";
}
