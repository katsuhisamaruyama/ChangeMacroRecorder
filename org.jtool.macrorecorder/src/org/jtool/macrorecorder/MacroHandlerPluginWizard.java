/*
 *  Copyright 2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder;

import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;

/**
 * A wizard that generates a plug-in content.
 * @author Katsuhisa Maruyama
 */
public class MacroHandlerPluginWizard extends NewPluginTemplateWizard {
    
    /**
     * Creates templates that will appear in this wizard.
     * @return an array of template sections that will appear in this wizard
     */
    @Override
    public ITemplateSection[] createTemplateSections() {
        return new ITemplateSection[]{ new MacroHandlerTemplateSection() };
    }
}
