/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.sample3;

import org.jtool.macrorecorder.macro.Macro;
import org.jtool.macrorecorder.recorder.IMacroHandler;
import org.jtool.macrorecorder.recorder.MacroEvent;
import org.jtool.macrorecorder.recorder.MacroConsole;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * SampleMacroPrintCommand sample handler that post change macros to an HTTP server.
 * <p>
 * This is intended to be specified in the extension point of <code>org.jtool.macrorecorder.handlers</code>.
 * <pre><code>
 * <extension point="org.jtool.macrorecorder.handlers">
 *     <handler class="org.jtool.macrorecorder.sample3.SampleMacroPostHandler">
 *     </handler> -->
 *  </extension>
 * </code></pre>
 * </p>
 */
public class SampleMacroPostHandler implements IMacroHandler {
    
    private static final String URL_FOR_POST = "http://localhost:1337/post";
    
    public SampleMacroPostHandler() {
    }
    
    @Override
    public boolean recordingAllowed() {
        return true;
    }
    
    @Override
    public void initialize() {
    }
    
    @Override
    public void terminate() {
    }
    
    @Override
    public void macroAdded(MacroEvent evt) {
        Macro macro = evt.getMacro();
        executePost(macro.getJSONString());
    }
    
    @Override
    public void rawMacroAdded(MacroEvent evt) {
    }
    
    private void executePost(String jsonString) {
        try {
            URL url = new URL(URL_FOR_POST);
            HttpURLConnection connection = null;
            
            try {
                connection = (HttpURLConnection)url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setRequestProperty("Content-Length", String.valueOf(jsonString.length()));
                
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
                writer.write(jsonString);
                writer.flush();
                
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    MacroConsole.println("POST FAILURE: " + jsonString);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            
        } catch (IOException e) {
            MacroConsole.println("POST FAILURE: " + jsonString);
        }
    }
}
