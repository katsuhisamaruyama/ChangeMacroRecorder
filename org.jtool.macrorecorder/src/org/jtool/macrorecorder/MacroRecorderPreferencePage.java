/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.jtool.macrorecorder.recorder.MacroRecorder;

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
    }
    
    /**
     * Initializes a preference page for a given workbench.
     */
    @Override
    public void init(IWorkbench workbench) {
    }
    
    /**
     * Tests if recorded macros will be displayed.
     * @return <code>true</code> if the displaying is required, otherwise <code>false</code>
     */
    static boolean displayMacros() {
        IPreferenceStore store = Activator.getPlugin().getPreferenceStore();
        return store.getBoolean(DISPLAY_MACROS);
    }
    
    /**
     * Tests if recorded raw macros will be displayed.
     * @return <code>true</code> if the displaying is required, otherwise <code>false</code>
     */
    static boolean displayRawMacros() {
        IPreferenceStore store = Activator.getPlugin().getPreferenceStore();
        return store.getBoolean(DISPLAY_RAW_MACROS);
    }
}
