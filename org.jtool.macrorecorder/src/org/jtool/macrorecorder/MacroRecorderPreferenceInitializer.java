/*
 *  Copyright 2017-2019
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Initializes the preference values.
 * @author Katsuhisa Maruyama
 */
public class MacroRecorderPreferenceInitializer extends AbstractPreferenceInitializer {
    
    /**
     * The default URL of a server which macros are posted to.
     */
    private static final String DEFAULT_URL_FOR_POST = "http://localhost:1337/post";
    
    /**
     * Stores initial preference values.
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getPlugin().getPreferenceStore();
        store.setDefault(MacroRecorderPreferencePage.DISPLAY_MACROS, false);
        store.setDefault(MacroRecorderPreferencePage.DISPLAY_RAW_MACROS, false);
        store.setDefault(MacroRecorderPreferencePage.POST_MACROS, false);
        store.setDefault(MacroRecorderPreferencePage.URL_FOR_POST, DEFAULT_URL_FOR_POST);
    }
}
