/*
 *  Copyright 2018
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder;

import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelFactory;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.PluginReference;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import java.util.ResourceBundle;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

/**
 * Generating templates of a macro handler that receives change macros.
 * @author Katsuhisa Maruyama
 */
public class MacroHandlerTemplateSection extends OptionTemplateSection {
    
    /**
     * The default package name.
     */
    private static final String DEFAULT_PACKAGE_NAME = "org.jtool.macrorecorder.sample";
    
    /**
     * The default class name.
     */
    private static final String DEFAULT_CLASS_NAME = "SampleMacroHandler";
    
    /**
     * The default processor name.
     */
    private static final String DEFAULT_COMBINATOR_NAME = "";
    
    /**
     * The default prefix string of the command ID.
     */
    private static final String COMMAND_ID_PREFIX = "org.eclipse.macrorecorder.handler";
    
    /**
     * A key that indicates the package name in the option page.
     */
    private static final String KEY_PACKAGE_NAME = "packageName";
    
    /**
     * A key that indicates the class name for a handler in the option page.
     */
    private static final String KEY_CLASS_NAME = "className";
    
    /**
     * A key that indicates the class name of a combinator in the option page.
     */
    private static final String KEY_COMBINATOR_NAME = "combinatorName";
    
    /**
     * The identifier for this section.
     */
    private static final String SECTION_ID = "macroHandler";
    
    /**
     * Creates this template session.
     */
    public MacroHandlerTemplateSection() {
        setPageCount(1);
        createOptions();
    }
    
    /**
     * Creates a options.
     */
    private void createOptions() {
        addOption(KEY_PACKAGE_NAME, "Package Name", DEFAULT_PACKAGE_NAME, 0);
        addOption(KEY_CLASS_NAME, "Handler Class Name ", DEFAULT_CLASS_NAME, 0);
        addOption(KEY_COMBINATOR_NAME , "Processor Class Name ", DEFAULT_COMBINATOR_NAME, 0);
    }
    
    /**
     * Adds pages to the wizard, which prompt nedded information.
     * @param wizard the host wizard to add pages into
     */
    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0);
        page.setTitle("Sample Macro Handler");
        page.setDescription("Provide the options to generate a sample macro handler.");
        wizard.addPage(page);
        
        markPagesAdded();
    }
    
    /**
     * Returns the identifier of the extension point used in this section.
     * @return extension point identifier
     */
    @Override
    public String getUsedExtensionPoint() {
        return MacroHandlerLoader.EXTENSION_POINT_ID;
    }
    
    /**
     * Returns the directory where all the templates are located in the contributing plug-in.
     * @return the constant string Eclipse 3.0
     */
    @Override
    protected String getTemplateDirectory() { 
        return "templates_3.0"; 
    }
    
    /**
     * Returns the unique name of this section.
     * @return the unique section identifier
     */
    @Override
    public String getSectionId() {
        return SECTION_ID;
    }
    
    /**
     * Returns an array of tokens representing new files and folders created by this template section.
     * @return the empty array since no new files or folders are needed to be added
     */
    @Override
    public String[] getNewFiles() {
        return new String[0];
    }
    
    /**
     * Returns the resource bundle.
     * @return <code>null </code> since if no resource bundle file is used
     */
    @Override
    protected ResourceBundle getPluginResourceBundle() {
        return null;
    }
    
    /**
     * Returns the install URL of the plug-in that contributes this template.
     * @return the install URL of the contributing plug-in
     */
    @Override
    protected URL getInstallURL() {
        return Activator.getPlugin().getBundle().getEntry("/"); 
    }
    
    /**
     * Provides the list of template dependencies.
     * @param schemaVersion version of the target manifest
     * @return the array of template dependencies
     */
    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        List<IPluginReference> result = new ArrayList<IPluginReference>();
        if (schemaVersion != null) {
            result.add(new PluginReference("org.eclipse.core.runtime", null, 0));
        }
        result.add(new PluginReference("org.eclipse.ui", null, 0));
        result.add(new PluginReference("org.jtool.macrorecorder", null, 0));
        return (IPluginReference[]) result.toArray(new IPluginReference[result.size()]);
    }
    
    /**
     * Updates the plug-in configuration.
     * @param monitor the progress monitor to be used
     */
    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        IPluginModelFactory factory = model.getPluginFactory();
        
        String fqn = getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_CLASS_NAME);
        String combinatorName = getStringOption(KEY_COMBINATOR_NAME);
        if (combinatorName.length() > 0) {
            combinatorName = getStringOption(KEY_PACKAGE_NAME) + "." + combinatorName;
        }
        String commandId = COMMAND_ID_PREFIX + "." + getStringOption(KEY_CLASS_NAME);
        
        IPluginExtension extension = createExtension(getUsedExtensionPoint(), true);
        IPluginElement element = factory.createElement(extension);
        element.setName(MacroHandlerLoader.ELEMENT_NAME);
        element.setAttribute(MacroHandlerLoader.ATTRIBUTE_CLASS, fqn);
        if (combinatorName.length() > 0) {
            element.setAttribute(MacroHandlerLoader.ATTRIBUTE_COMBINATOR, combinatorName);
        }
        element.setAttribute(MacroHandlerLoader.ATTRIBUTE_COMMAND_ID, commandId);
        extension.add(element);
        
        plugin.add(extension);
    }
}
