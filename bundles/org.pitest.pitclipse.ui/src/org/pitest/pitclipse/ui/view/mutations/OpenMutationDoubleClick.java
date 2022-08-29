/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.ui.view.mutations;

import static org.eclipse.ui.ide.IDE.openEditor;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.pitest.pitclipse.runner.model.Mutation;
import org.pitest.pitclipse.runner.model.MutationsModelVisitorAdapter;
import org.pitest.pitclipse.runner.model.Visitable;

public enum OpenMutationDoubleClick implements IDoubleClickListener {
    
    LISTENER;
    
    /**
     * The family to which any Eclipse Job scheduled by this listener belongs to.
     * <p>
     * Especially useful during tests to ensure that any background processing
     * is over before moving on.
     */
    public static final Class<OpenMutationDoubleClick> JOB_FAMILY = OpenMutationDoubleClick.class; 

    @Override
    public void doubleClick(DoubleClickEvent event) {
        IStructuredSelection selection = selectionFrom(event);
        Object element = selection.getFirstElement();
        Visitable visitable = (Visitable) element;
        visitable.accept(MutationSource.VIEWER);
    }

    private IStructuredSelection selectionFrom(DoubleClickEvent event) {
        return (IStructuredSelection) event.getSelection();
    }

    private enum MutationSource implements MutationsModelVisitorAdapter<Void> {
        VIEWER;

        @Override
        public Void visitMutation(Mutation mutation) {
            final String projectName = mutation.getClassMutations().getPackageMutations().getProjectMutations()
                    .getProjectName();
            final String className = mutation.getClassMutations().getClassName();
            final int lineNumber = mutation.getLineNumber() - 1;

            new MutationSelectingJob(projectName, lineNumber, className).schedule();
            return null;
        }

        private static final class MutationSelectingJob extends UIJob {
            private final String projectName;
            private final int lineNumber;
            private final String className;

            private MutationSelectingJob(String projectName, int lineNumber, String className) {
                super("Opening mutated source");
                this.projectName = projectName;
                this.lineNumber = lineNumber;
                this.className = className;
            }

            @Override
            public IStatus runInUIThread(IProgressMonitor arg0) {
                return findClass(projectName, className)
                        .map(new OpenFileInEditorAtLine(lineNumber))
                        .orElse(org.eclipse.core.runtime.Status.OK_STATUS);
            }
            
            @Override
            public boolean belongsTo(Object family) {
                return JOB_FAMILY.equals(family);
            }

            private Optional<IFile> findClass(final String projectName, final String className) {
                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                for (IProject project : root.getProjects()) {
                    if (project.getName().equals(projectName) && project.isOpen()) {
                        IJavaProject javaProject = JavaCore.create(project);
                        if (javaProject != null) {
                            try {
                                IType type = javaProject.findType(className);
                                return Optional.ofNullable(root.getFile(type.getPath()));
                            } catch (JavaModelException e) {
                                // Maybe type no longer exists. Do nothing
                            }
                        }
                    }
                }
                return Optional.empty();
            }

            private static final class OpenFileInEditorAtLine implements Function<IFile, IStatus> {
                private final int lineNumber;

                public OpenFileInEditorAtLine(int lineNumber) {
                    this.lineNumber = lineNumber;
                }

                @Override
                public IStatus apply(final IFile file) {
                    IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    if (workbench != null) {
                        try {
                            tryToOpen(workbench, file);
                        } catch (CoreException e) {
                            return org.eclipse.core.runtime.Status.CANCEL_STATUS;
                        }
                    }
                    return org.eclipse.core.runtime.Status.OK_STATUS;
                }

                private void tryToOpen(IWorkbenchWindow workbench, final IFile file) throws CoreException {
                    IEditorPart editorPart = openEditor(workbench.getActivePage(), file);
                    if (editorPart instanceof ITextEditor && lineNumber >= 0) {
                        ITextEditor textEditor = (ITextEditor) editorPart;
                        IEditorInput editorInput = textEditor.getEditorInput();
                        openEditorAtLine(textEditor, editorInput);
                    }
                }

                private void openEditorAtLine(ITextEditor textEditor, IEditorInput editorInput) throws CoreException {
                    IDocumentProvider provider = textEditor.getDocumentProvider();
                    provider.connect(editorInput);
                    try {
                        IDocument document = provider.getDocument(editorInput);
                        IRegion line = document.getLineInformation(lineNumber);
                        textEditor.selectAndReveal(line.getOffset(), line.getLength());
                    } catch (BadLocationException e) {
                        // Invalid line number - perhaps file has since changed.  Do nothing
                    } finally {
                        provider.disconnect(editorInput);
                    }
                }
            }
        }
    }

}
