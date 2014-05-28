package org.pitest.pitclipse.ui.view.mutations;

import static org.eclipse.ui.ide.IDE.openEditor;

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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
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

			new MutationSelectingJob(projectName, lineNumber, className).schedule();
			return null;
		}

		@Override
		public Void visitStatus(Status status) {
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
				return findClass(projectName, className).transform(new OpenFileInEditorAtLine(lineNumber)).or(
						org.eclipse.core.runtime.Status.OK_STATUS);
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

			private static final class OpenFileInEditorAtLine implements Function<IFile, IStatus> {
				private final int lineNumber;

				public OpenFileInEditorAtLine(int lineNumber) {
					this.lineNumber = lineNumber;
				}

				@Override
				public IStatus apply(final IFile file) {
					Optional<IWorkbenchWindow> workbench = Optional.fromNullable(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow());
					if (workbench.isPresent()) {
						IWorkbenchWindow wb = workbench.get();
						try {
							tryToOpen(wb, file);
						} catch (CoreException e) {
							return org.eclipse.core.runtime.Status.CANCEL_STATUS;
						}
					}
					return org.eclipse.core.runtime.Status.OK_STATUS;
				}

				private void tryToOpen(IWorkbenchWindow workbench, final IFile file) throws PartInitException,
						CoreException {
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
					} finally {
						provider.disconnect(editorInput);
					}
				}
			}
		}
	}

}
