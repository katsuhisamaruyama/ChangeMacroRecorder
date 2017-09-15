/*
 *  Copyright 2016-2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.sample;

import org.jtool.macrorecorder.recorder.MacroRecorder;
import org.jtool.macrorecorder.recorder.IMacroRecorder;
import org.jtool.macrorecorder.recorder.IMacroListener;
import org.jtool.macrorecorder.recorder.IMacroCompressor;
import org.jtool.macrorecorder.recorder.MacroEvent;
import org.jtool.macrorecorder.recorder.MacroConsole;
import org.jtool.macrorecorder.macro.Macro;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;


/**
 * Records macros that were performed on Eclipse.
 * @author Katsuhisa Maruyama
 */
public class SampleMacroPost implements IMacroListener {
    
    public SampleMacroPost() {
    }
    
    public void start() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        recorder.addMacroListener(this);
        recorder.start();
    }
    
    public void stop() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        recorder.removeMacroListener(this);
        recorder.stop();
    }
    
    @Override
    public void macroAdded(MacroEvent evt) {
        Macro macro = evt.getMacro();
        executePost(macro.getJSON());
    }
    
    @Override
    public void rawMacroAdded(MacroEvent evt) {
    }
    
    private void executePost(String jsonString) {
        try {
            URL url = new URL("http://localhost:1337/post");
            HttpURLConnection connection = null;
            
            try {
                connection = (HttpURLConnection)url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                
                BufferedWriter writer = new BufferedWriter(
                  new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
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
