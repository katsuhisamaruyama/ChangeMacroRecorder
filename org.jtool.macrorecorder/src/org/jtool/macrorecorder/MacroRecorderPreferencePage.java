/*
 *  Copyright 2017-2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder;

import org.jtool.macrorecorder.recorder.MacroRecorder;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

/**
 * Manages the preference page.
 * @author Katsuhisa Maruyama
 */
public class MacroRecorderPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    
    /**
     * Displays recorded macros on the console for debugging.
     */
    static final String DISPLAY_MACROS = "display.macros";
    
    /**
     * Displays recorded raw macros on the console for debugging.
     */
    static final String DISPLAY_RAW_MACROS = "display.rawmacros";
    
    /**
     * Posts recorded macros for debugging.
     */
    static final String POST_MACROS = "post.macros";
    
    /**
     * The URL of a server which macros are posted to.
     */
    static final String URL_FOR_POST = "url.post.macros";
    
    /**
     * The boolean editor that specifies whether macros are posted. 
     */
    private BooleanFieldEditor postBooleanEditor;
    
    /**
     * The field editor that specifies the URL of a server. 
     */
    private StringFieldEditor postFieldEditor;
    
    /**
     * Creates an object for a preference page.
     */
    public MacroRecorderPreferencePage() {
        super(GRID);
        IPreferenceStore store = Activator.getPlugin().getPreferenceStore();
        setPreferenceStore(store);
    }
    
    /**
     * Creates the field editors for preference settings.
     */
    @Override
    public void createFieldEditors() {
        addField(new BooleanFieldEditor(DISPLAY_MACROS,
                "Displays recorded macros on the console", getFieldEditorParent()) {
            
            /**
             * Stores the preference value from this field editor into the preference store.
             */
            @Override
            protected void doStore() {
                super.doStore();
                MacroRecorder macroRecorder = (MacroRecorder)MacroRecorder.getInstance();
                macroRecorder.displayMacrosOnConsole(getBooleanValue());
            }
        });
        
        addField(new BooleanFieldEditor(DISPLAY_RAW_MACROS,
                "Displays recorded raw macros on the console", getFieldEditorParent()) {
            
            /**
             * Stores the preference value from this field editor into the preference store.
             */
            @Override
            protected void doStore() {
                super.doStore();
                MacroRecorder macroRecorder = (MacroRecorder)MacroRecorder.getInstance();
                macroRecorder.displayRawMacrosOnConsole(getBooleanValue());
            }
        });
        
        postBooleanEditor = new BooleanFieldEditor(POST_MACROS,
                "Posts recorded macros to", getFieldEditorParent()) {
            
            /**
             * Stores the preference value from this field editor into the preference store.
             */
            @Override
            protected void doStore() {
                super.doStore();
                postMacros(postBooleanEditor.getBooleanValue(), postFieldEditor.getStringValue());
            }
        };
        addField(postBooleanEditor);
        
        postFieldEditor = new StringFieldEditor(URL_FOR_POST, "", getFieldEditorParent()) {
            
            /**
             * Stores the preference value from this field editor into the preference store.
             */
            @Override
            protected void doStore() {
                super.doStore();
                postMacros(postBooleanEditor.getBooleanValue(), postFieldEditor.getStringValue());
            }
        };
        postFieldEditor.setEmptyStringAllowed(false);
        addField(postFieldEditor);
    }
    
    /**
     * Initializes a preference page for a given workbench.
     */
    @Override
    public void init(IWorkbench workbench) {
    }
    
    /**
     * Initializes the values of the stored preference.
     */
    static void init() {
        IPreferenceStore store = Activator.getPlugin().getPreferenceStore();
        MacroRecorder macroRecorder = (MacroRecorder)MacroRecorder.getInstance();
        macroRecorder.displayMacrosOnConsole(store.getBoolean(DISPLAY_MACROS));
        macroRecorder.displayRawMacrosOnConsole(store.getBoolean(DISPLAY_RAW_MACROS));
        postMacros(false, store.getString(URL_FOR_POST));
    }
    
    /**
     * Sets the URL of a server which macros are posted to.
     * @param post <code>true</code> if the posting is required, otherwise <code>false</code>
     * @param url the URL of the server
     */
    private static void postMacros(boolean post, String url) {
        MacroRecorder macroRecorder = (MacroRecorder)MacroRecorder.getInstance();
        if (post) {
            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }
            macroRecorder.postMacroOnURL(url);
        } else {
            macroRecorder.postMacroOnURL(null);
        }
    }
}
