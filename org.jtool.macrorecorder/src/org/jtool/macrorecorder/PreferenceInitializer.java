/*
 *  Copyright 2017
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
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    
    /**
     * Stores initial preference values.
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getPlugin().getPreferenceStore();
        store.setDefault(PreferencePage.DISPLAY_MACROS, false);
        store.setDefault(PreferencePage.DISPLAY_RAW_MACROS, false);
    }
}
