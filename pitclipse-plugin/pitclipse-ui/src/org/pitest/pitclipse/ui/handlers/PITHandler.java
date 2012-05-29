package org.pitest.pitclipse.ui.handlers;

import javax.annotation.concurrent.ThreadSafe;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.pitest.pitclipse.pitrunner.PITRunner;

import com.google.common.annotations.VisibleForTesting;

@ThreadSafe
public class PITHandler extends AbstractHandler {

	private static final String JAVA_EDITOR_ID =
			"org.eclipse.jdt.ui.CompilationUnitEditor";
	
	private static final String PACKAGE_VIEW_ID =
			"org.eclipse.jdt.ui.PackageExplorer";
	
	private final PITRunner pitRunner ;
	
	public PITHandler() {
		this(new PITRunner());
	}
	
	@VisibleForTesting
	PITHandler(PITRunner pitRunner) {
		this.pitRunner = pitRunner;
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell activeShell = HandlerUtil.getActiveShell(event);
		  //get selected items or text
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		
		if (isMutatable(event)) {
		String activePartId = HandlerUtil.getActivePartId(event);
		  if (JAVA_EDITOR_ID.equals(activePartId)) {
		    //get edited file
		    IEditorInput input = HandlerUtil.getActiveEditorInput(event);
		    ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		    IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		    		    
		    IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		    for (IProject project : root.getProjects()) {
				try {
					if (project.hasNature(JavaCore.NATURE_ID)){
						IJavaProject javaProject = JavaCore.create(project);
						System.out.println(project.getName());
						
						
					//	JavaProject.
				//		IJavaProject javaProject ;
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		    
		    //currentSelection contains text selection inside input file
		    //... locate class selected in that file ...
		    System.out.println(input.getName());
		  }
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Pitclipse-ui",
				"Hello PIT");
		}
		return null;
	}

	private boolean isMutatable(ExecutionEvent event) {
		if (isPackageView(event) || isJavaView(event)) {
			return true ;
		}
		return false;
	}

	private boolean isJavaView(ExecutionEvent event) {
		return JAVA_EDITOR_ID.equals(HandlerUtil.getActivePartId(event)) ;
	}

	private boolean isPackageView(ExecutionEvent event) {
		return PACKAGE_VIEW_ID.equals(HandlerUtil.getActivePartId(event)) ;
	}

}
