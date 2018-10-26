/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.macro;

import java.util.List;
import java.util.Map;
import java.io.StringReader;
import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonStructure;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;

/**
 * The constants of JSON attributes for representing information about macros.
 * @author Katsuhisa Maruyama
 */
public class MacroJSON {
    
    protected static final String JSON_MACRO = "macro";
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
        String path = "";
        if (macro.getPath() != null) {
            path = macro.getPath();
        }
        
        JsonObjectBuilder builder = Json.createObjectBuilder()
          .add(MacroJSON.JSON_MACRO, macro.getThisClassName())
          .add(MacroJSON.JSON_MACRO_TIME, macro.getTimeAsISOString(macro.getTime()))
          .add(MacroJSON.JSON_MACRO_ACTION, macro.getAction())
          .add(MacroJSON.JSON_MACRO_PATH, path)
          .add(MacroJSON.JSON_MACRO_PROJECT_NAME, macro.getProjectName())
          .add(MacroJSON.JSON_MACRO_PACKAGE_NAME, macro.getPackageName())
          .add(MacroJSON.JSON_MACRO_FILE_NAME, macro.getFileName());
        JsonArrayBuilder getJSONArray = getJSONArrayBuilder(macro.getRawMacros());
        if (getJSONArray != null) {
            builder.add(MacroJSON.JSON_RAW_MACROS, getJSONArray);
        }
        return builder;
    }
    
    /**
     * Creates a JSON array builder of macros.
     * @param macros the collection of the macros
     * @return the created JSON array builder, or <code>null</code> if the array builder is not required
     */
    protected static JsonArrayBuilder getJSONArrayBuilder(List<Macro> macros) {
        if (macros == null || macros.size() == 0) {
            return null;
        }
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Macro macro : macros) {
            builder.add(macro.getJSON());
        }
        return builder;
    }
    
    /**
     * Creates a JSON array builder of a map.
     * @param map the map
     * @return the created JSON array builder, or <code>null</code> if the array builder is not required
     */
    protected static JsonArrayBuilder getJSONArrayBuilder(Map<String, String> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (String key : map.keySet()) {
            builder.add(Json.createObjectBuilder().add(key, map.get(key)).build());
        }
        return builder;
    }
    
    /**
     * Creates a string that represents a JSON object or array.
     * @param json the JSON object or array
     * @return the created string
     */
    protected static String stringify(JsonStructure json) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.write(json);
        jsonWriter.close();
        return stringWriter.toString();
    }
    
    /**
     * Restores a JSON object from its string representation.
     * @param jsonString the string representation
     * @return the restored JSON object
     */
    protected static JsonObject parseObject(String jsonString) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObject = jsonReader.readObject();
        return jsonObject;
    }
    
    /**
     * Restores a JSON array from its string representation.
     * @param jsonString the string representation
     * @return the restored JSON array
     */
    protected static JsonArray parseArray(String jsonString) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonArray jsonArray = jsonReader.readArray();
        return jsonArray;
    }
}
