package org.pitest.pitclipse.ui.view.mutations;

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
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.pitest.pitclipse.pitrunner.model.ClassMutations;
import org.pitest.pitclipse.pitrunner.model.Mutation;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;
import org.pitest.pitclipse.pitrunner.model.MutationsModelVisitor;
import org.pitest.pitclipse.pitrunner.model.PackageMutations;
import org.pitest.pitclipse.pitrunner.model.ProjectMutations;
import org.pitest.pitclipse.pitrunner.model.Status;
import org.pitest.pitclipse.pitrunner.model.Visitable;
import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.base.Optional;

public enum OpenMutationDoubleClick implements IDoubleClickListener {
	LISTENER;

	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection selection = selectionFrom(event);
		Object element = selection.getFirstElement();
		if (element instanceof Visitable) {
			Visitable visitable = (Visitable) element;
			visitable.accept(MutationSource.VIEWER);
		}
	}

	private IStructuredSelection selectionFrom(DoubleClickEvent event) {
		return (IStructuredSelection) event.getSelection();
	}

	private enum MutationSource implements MutationsModelVisitor<Void> {
		VIEWER;

		@Override
		public Void visitModel(MutationsModel mutationsModel) {
			return null;
		}

		@Override
		public Void visitProject(ProjectMutations projectMutations) {
			return null;
		}

		@Override
		public Void visitPackage(PackageMutations packageMutations) {
			return null;
		}

		@Override
		public Void visitClass(ClassMutations classMutations) {
			return null;
		}

		@Override
		public Void visitMutation(Mutation mutation) {
			final String projectName = mutation.getClassMutations().getPackageMutations().getProjectMutations()
					.getProjectName();
			final String className = mutation.getClassMutations().getClassName();
			final int lineNumber = mutation.getLineNumber() - 1;

			UIJob job = new UIJob("Opening mutated source") {
				@Override
				public IStatus runInUIThread(IProgressMonitor arg0) {
					return findClass(projectName, className).transform(new Function<IFile, IStatus>() {
						@Override
						public IStatus apply(final IFile file) {
							Optional<IWorkbenchWindow> workbench = Optional.fromNullable(PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow());
							if (workbench.isPresent()) {
								try {
									IEditorPart editorPart = IDE.openEditor(workbench.get().getActivePage(), file);
									if (editorPart instanceof ITextEditor && lineNumber >= 0) {
										ITextEditor textEditor = (ITextEditor) editorPart;
										IEditorInput editorInput = textEditor.getEditorInput();
										IDocumentProvider provider = textEditor.getDocumentProvider();
										provider.connect(editorInput);
										try {
											IDocument document = provider.getDocument(editorInput);
											IRegion line = document.getLineInformation(lineNumber);
											textEditor.selectAndReveal(line.getOffset(), line.getLength());
										} catch (BadLocationException e) {
										} finally {
											provider.disconnect(editorInput);
										}
									}
								} catch (CoreException e) {
									return org.eclipse.core.runtime.Status.CANCEL_STATUS;
								}
							}
							return org.eclipse.core.runtime.Status.OK_STATUS;
						}
					}).or(org.eclipse.core.runtime.Status.OK_STATUS);
				}

				private Optional<IFile> findClass(final String projectName, final String className) {
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					for (IProject project : root.getProjects()) {
						if (project.getName().equals(projectName) && project.isOpen()) {
							Optional<IJavaProject> javaProject = Optional.fromNullable(JavaCore.create(project));
							if (javaProject.isPresent()) {
								try {
									IType type = javaProject.get().findType(className);
									return Optional.fromNullable(root.getFile(type.getPath()));
								} catch (JavaModelException e) {
								}
							}
						}
					}
					return Optional.absent();
				}
			};
			job.schedule();
			return null;
		}

		@Override
		public Void visitStatus(Status status) {
			return null;
		}

	}

}
