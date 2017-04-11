/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jtool.macrorecorder.recorder.MacroRecorder;
import org.eclipse.ui.IWorkbench;

/**
 * Manages the preference page.
 * @author Katsuhisa Maruyama
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    
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
    public PreferencePage() {
        super(GRID);
        
        IPreferenceStore store = Activator.getPlugin().getPreferenceStore();
        setPreferenceStore(store);
        store.addPropertyChangeListener(new IPropertyChangeListener() {
            
            /**
             * Receives a property changed event.
             * @param event the property change event describing which property was changed
             */
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                MacroRecorder macroRecorder = (MacroRecorder)MacroRecorder.getInstance();
                macroRecorder.displayMacrosOnConsole(displayMacros(), displayRawMacros());
            }
        });
    }
    
    /**
     * Creates the field editors for preference settings.
     */
    @Override
    public void createFieldEditors() {
        addField(new BooleanFieldEditor(DISPLAY_MACROS,
                "Displays recorded macros on the console", getFieldEditorParent()));
        addField(new BooleanFieldEditor(DISPLAY_RAW_MACROS,
                "Displays recorded raw macros on the console", getFieldEditorParent()));
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
    private boolean displayMacros() {
        IPreferenceStore store = Activator.getPlugin().getPreferenceStore();
        return store.getBoolean(DISPLAY_MACROS);
    }
    
    /**
     * Tests if recorded raw macros will be displayed.
     * @return <code>true</code> if the displaying is required, otherwise <code>false</code>
     */
    private boolean displayRawMacros() {
        IPreferenceStore store = Activator.getPlugin().getPreferenceStore();
        return store.getBoolean(DISPLAY_RAW_MACROS);
    }
}
