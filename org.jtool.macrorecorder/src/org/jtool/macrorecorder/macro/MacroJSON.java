/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

/**
 * The constants of JSON attributes for representing information about macros.
 * @author Katsuhisa Maruyama
 */
public class MacroJSON {
    
    protected static final String JSON_MACRO_CLASS = "class";
    protected static final String JSON_MACRO_TIME = "time";
    protected static final String JSON_MACRO_ACTION = "action";
    protected static final String JSON_MACRO_PATH = "path";
    protected static final String JSON_MACRO_BRANCH = "branch";
    protected static final String JSON_MACRO_PROJECT_NAME = "project";
    protected static final String JSON_MACRO_PACKAGE_NAME = "package";
    protected static final String JSON_MACRO_FILE_NAME = "file";
    protected static final String JSON_RAW_MACROS = "rawMacros";
    
    protected static final String JSON_ATTR_COMMAND = "commandId";
    protected static final String JSON_ATTR_OFFSET = "offset";
    protected static final String JSON_ATTR_INSERTED_TEXT = "itext";
    protected static final String JSON_ATTR_DELETED_TEXT = "dtext";
    protected static final String JSON_ATTR_COPYED_TEXT = "ctext";
    protected static final String JSON_ATTR_CODE = "code";
    protected static final String JSON_ATTR_CHARSET = "charset";
    protected static final String JSON_ATTR_SRD_DST_PATH = "sdpath";
    protected static final String JSON_ATTR_REFACTORING_NAME = "refname";
    protected static final String JSON_ATTR_REFACTORING_START = "refstart";
    protected static final String JSON_ATTR_REFACTORING_END = "refend";
    protected static final String JSON_ATTR_RESOURCE_TARGET = "target";
    protected static final String JSON_ATTR_TIMING = "timing";
    
    protected static final String JSON_ATTR_NUMBER = "num";
    protected static final String JSON_PRIMITIVE_MACROS = "rawMacros";
    
    /**
     * Creates a JSON object builder of a macro.
     * @param macro the macro
     * @return the created JSON object builder
     */
    protected static JsonObjectBuilder getJSONObjectBuikder(Macro macro) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
          .add(MacroJSON.JSON_MACRO_CLASS, macro.getThisClassName())
          .add(MacroJSON.JSON_MACRO_TIME, macro.getTimeAsISOString(macro.getTime()))
          .add(MacroJSON.JSON_MACRO_ACTION, macro.getAction())
          .add(MacroJSON.JSON_MACRO_PATH, macro.getPath())
          .add(MacroJSON.JSON_MACRO_PROJECT_NAME, macro.getProjectName())
          .add(MacroJSON.JSON_MACRO_PACKAGE_NAME, macro.getPackageName())
          .add(MacroJSON.JSON_MACRO_FILE_NAME, macro.getFileName())
          .add(MacroJSON.JSON_MACRO_PATH, getJSONArrayBuilder(macro.getRawMacros()));
        return builder;
    }
    
    /**
     * Creates a JSON array builder of macros.
     * @param macros the collection of the macros
     * @return the created JSON array builder
     */
    protected static JsonArrayBuilder getJSONArrayBuilder(List<Macro> macros) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Macro macro : macros) {
            builder.add(macro.getJSON());
        }
        return builder;
    }
    
    /**
     * Creates a JSON array builder of a map.
     * @param macros the collection of the map
     * @return the created JSON array builder
     */
    protected static JsonArrayBuilder getJSONArrayBuilder(Map<String, String> maps) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (String key : maps.keySet()) {
            builder.add(Json.createObjectBuilder().add(key, maps.get(key)).build());
        }
        return builder;
    }
}
