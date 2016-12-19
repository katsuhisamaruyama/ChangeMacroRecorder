/*
 *  Copyright 2016
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.recorder;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import java.io.ByteArrayInputStream;

/**
 * Listens code completion events (quick assist or content assist).
 * @author Katsuhisa Maruyama
 */
class PackageExplorerListener implements ISelectionChangedListener {
    
    /**
     * A recorder that records global macros.
     */
    private GlobalMacroRecorder globalRecorder;
    
    /**
     * A listener that manages the part of a package explorer.
     */
    private PartListener partListener;
    
    /**
     * An active page in the workbench.
     */
    private IWorkbenchPage activePage;
    
    /**
     * Creates an object that records package explorer events.
     * @param recorder a recorder that records global macros
     */
    PackageExplorerListener(GlobalMacroRecorder recorder) {
        this.globalRecorder = recorder;
    }
    
    /**
     * Registers a package explorer listener.
     */
    void register() {
        partListener = new PartListener(this);
        activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        activePage.addPartListener(partListener);
        
        IPackagesViewPart part = (IPackagesViewPart)activePage.findView(JavaUI.ID_PACKAGES);
        if (part != null) {
            part.getTreeViewer().addSelectionChangedListener(this);
        }
    }
    
    /**
     * Unregisters a package explorer listener.
     */
    void unregister() {
        IPackagesViewPart part = (IPackagesViewPart)activePage.findView(JavaUI.ID_PACKAGES);
        if (part != null) {
            part.getTreeViewer().removeSelectionChangedListener(this);
        }
        
        activePage.removePartListener(partListener);
    }
    
    /**
     * Receives an event when the selection has changed.
     * @param event the selection change event
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        if(selection instanceof ITreeSelection) {
            
            Object elem = ((ITreeSelection)selection).getFirstElement();
            if (elem instanceof ICompilationUnit) {
                ICompilationUnit compilationUnit = (ICompilationUnit)elem;
                String path = compilationUnit.getPath().toString();
                
                try {
                    DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
                    if (docRecorder == null) {
                        IFile file = (IFile)compilationUnit.getCorrespondingResource();
                        globalRecorder.getRecorder().off(file);
                    }
                } catch (JavaModelException e) {
                }
                globalRecorder.setSelectedPath(path);
                
                DocMacroRecorder docRecorder = globalRecorder.getDocMacroRecorder(path);
                if (docRecorder != null) {
                    docRecorder.applyDiff(false);
                }
                
            } else if (elem instanceof IPackageFragment) {
                IPackageFragment javaPackage = (IPackageFragment)elem;
                String path = javaPackage.getPath().toString();
                globalRecorder.setSelectedPath(path);
                
            } else if (elem instanceof IJavaProject) {
                IJavaProject javaProject = (IJavaProject)elem;
                String path = javaProject.getPath().toString();
                globalRecorder.setSelectedPath(path);
            }
        }
    }
    
    /**
     * Touches all compilation units within a project.
     * @param javaProject the project
     */
    void touch(IJavaProject javaProject) {
        try {
            IPackageFragment[] packages = javaProject.getPackageFragments();
            for (int i = 0; i < packages.length; i++) {
                touch(packages[i]);
            }
        } catch (JavaModelException e) {
        }
    }
    
    /**
     * Touches all compilation units within a package.
     * @param javaPackage the package
     */
    void touch(IPackageFragment javaPackage) {
        try {
            ICompilationUnit[] compilationUnits = javaPackage.getCompilationUnits();
            for (int i = 0; i < compilationUnits.length; i++) {
                touch(compilationUnits[i]);
            }
        } catch (JavaModelException e) {
        }
    }
    
    /**
     * Touches a compilation unit.
     * @param compilationUnit the compilation unit
     */
    void touch(ICompilationUnit compilationUnit) {
        try {
            IFile file = (IFile)compilationUnit.getCorrespondingResource();
            IDocument doc = EditorUtilities.getDocument(file);
            if (doc != null) {
                file.setContents(new ByteArrayInputStream(doc.get().getBytes()), true, true, null);
            }
        } catch (CoreException e) {
        }
    }
}

/**
 * Listens part events.
 * @author Katsuhisa Maruyama
 */
class PartListener implements IPartListener {
    
    /**
     * A package rxplorer listener.
     */
    private PackageExplorerListener packageListener;
    
    /**
     * Creates an object that records package explorer events.
     * @param recorder a recorder that records global macros
     */
    PartListener(PackageExplorerListener pl) {
        packageListener = pl;
    }
    
    /**
     * Receives a part when it has been activated.
     * @param part the part that was activated
     */
    @Override
    public void partOpened(IWorkbenchPart part) {
        if (part instanceof IPackagesViewPart) {
            IPackagesViewPart packagePart = (IPackagesViewPart)part;
            packagePart.getTreeViewer().addSelectionChangedListener(packageListener);
        }
    }
    
    /**
     * Receives a part when it has been activated.
     * @param part the part that was activated
     */
    @Override
    public void partClosed(IWorkbenchPart part) {
        if (part instanceof IPackagesViewPart) {
            IPackagesViewPart packagePart = (IPackagesViewPart)part;
            packagePart.getTreeViewer().removeSelectionChangedListener(packageListener);
        }
    }
    
    /**
     * Receives a part when it has been activated.
     * @param part the part that was activated
     */
    @Override
    public void partActivated(IWorkbenchPart part) {
    }
    
    /**
     * Receives a part when it has been deactivated.
     * @param part the part that was deactivated
     */
    @Override
    public void partDeactivated(IWorkbenchPart part) {
    }
    
    /**
     * Receives a part when it has been brought to the top.
     * @param part the part that was surfaced
     */
    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
    }
}
