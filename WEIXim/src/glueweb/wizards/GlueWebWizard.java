/**
 * 
 */
package glueweb.wizards;

import glueweb.editors.MultiPageEditor;
import glueweb.util.Copiar;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * Creates a new project containing folders and files for use with an
 * Example.com enterprise web site.
 * 
 * @author Nathan A. Good &lt;mail@nathanagood.com&gt;
 * 
 */
public class GlueWebWizard extends Wizard implements INewWizard,
		IExecutableExtension {

	/*
	 * Use the WizardNewProjectCreationPage, which is provided by the Eclipse
	 * framework.
	 */
	private GlueWebWizardPage wizardPage;

	private IConfigurationElement config;

	private IWorkbench workbench;
	private IProject project;

	private String projectName;

	/**
	 * Constructor
	 */
	public GlueWebWizard() {
		super();
	}

	public void addPages() {
		/*
		 * Unlike the custom new wizard, we just add the pre-defined one and
		 * don't necessarily define our own.
		 */
		wizardPage = new GlueWebWizardPage("NewWEIXimProject");
		wizardPage.setDescription("Create a new WEIXim Project.");
		wizardPage.setTitle("New WEIXim Project");
		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {

		if (project != null) {
			return true;
		}

		final IProject projectHandle = wizardPage.getProjectHandle();

		IPath projectURI = (!wizardPage.useDefaults()) ? wizardPage
				.getLocationPath() : null;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final IProjectDescription desc = workspace
				.newProjectDescription(projectHandle.getName());

		projectName = projectHandle.getName().toString();

		desc.setLocation(projectURI);

		try {
			write404();
			copyImage();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/*
		 * Just like the NewFileWizard, but this time with an operation object
		 * that modifies workspaces.
		 */
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				createProject(desc, projectHandle, monitor);
			}
		};

		/*
		 * This isn't as robust as the code in the BasicNewProjectResourceWizard
		 * class. Consider beefing this up to improve error handling.
		 */
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
		}

		project = projectHandle;

		if (project == null) {
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(config);
		BasicNewProjectResourceWizard.selectAndReveal(project, workbench
				.getActiveWorkbenchWindow());

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			IFile file = project.getFile(projectName + ".weixim");

			MultiPageEditor editor = (MultiPageEditor) IDE.openEditor(page,
					file, true);
			if (wizardPage.getCreateButton())
				editor.setActivePage(0);
			else if (wizardPage.getGlueButton())
				editor.setActivePage(1);
		} catch (PartInitException e) {
		}

		return true;
	}

	/**
	 * This creates the project in the workspace.
	 * 
	 * @param description
	 * @param projectHandle
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	void createProject(IProjectDescription description, IProject proj,
			IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {
		try {

			monitor.beginTask("", 2000);

			proj.create(description, new SubProgressMonitor(monitor, 1000));

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
					monitor, 1000));

			/*
			 * Okay, now we have the project and we can do more things with it
			 * before updating the perspective.
			 */
			IContainer container = (IContainer) proj;

			Path location = new Path(projectName + ".weixim");

			addFileToProject(container, location, GlueWebWizard
					.openContentStream(), monitor);

			final IFolder uiFolder = container.getFolder(new Path(
					"User Interface"));
			uiFolder.create(true, true, monitor);

			final IFolder lnFolder = container.getFolder(new Path(
					"Bussiness Logic"));
			lnFolder.create(true, true, monitor);

			final IFolder dFolder = container.getFolder(new Path("Data"));
			dFolder.create(true, true, monitor);

			final IFolder cFolder = container.getFolder(new Path(
					"Correspondences"));
			cFolder.create(true, true, monitor);

			InputStream resourceStream = this.getClass().getResourceAsStream(
					"templates/site-css-template.resource");

			// IFile file = project.getFile(location.lastSegment());
			// file.createLink(location, IResource.NONE, null);

			addFileToProject(container, location, resourceStream, monitor);

			resourceStream.close();

			IFolder imageFolder = container.getFolder(new Path("images"));
			imageFolder.create(true, true, monitor);
		} catch (IOException ioe) {
			IStatus status = new Status(IStatus.ERROR, "ExampleWizard",
					IStatus.OK, ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		} finally {
			monitor.done();
		}
	}

	/**
	 * Initialize file contents with a sample text.
	 */

	private static InputStream openContentStream() {
		String contents = "";
		return new ByteArrayInputStream(contents.getBytes());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}

	/**
	 * Sets the initialization data for the wizard.
	 */
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
	}

	/**
	 * Adds a new file to the project.
	 * 
	 * @param container
	 * @param path
	 * @param contentStream
	 * @param monitor
	 * @throws CoreException
	 */
	private void addFileToProject(IContainer container, Path path,
			InputStream contentStream, IProgressMonitor monitor)
			throws CoreException {
		final IFile file = container.getFile(path);

		if (file.exists()) {
			file.setContents(contentStream, true, true, monitor);
		} else {
			file.create(contentStream, true, monitor);
		}
	}

	private static void write404() throws IOException {
		String code = "<html><head><title>User Interface misses</title></head><body><h2>Not loaded yet any UI</h2> <h2> -- WEIXim</h2></body></html>";
		String fileName = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/" + "404.html";
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write(code);
		out.close();
	}

	private static void copyImage() throws IOException {
		String path = System.getProperty("osgi.syspath");
		glueweb.util.Copy.unZipFileToDirectory(path.toString()
				+ "/WEIXim_1.0.0.jar", ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/tmp");
		glueweb.util.Copiar.copy(new File(ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/tmp/icons/image.jpg"), new File(ResourcesPlugin.getWorkspace().getRoot()
						.getLocation().toString()
						+ "/icons/image.jpg"));
		deleteDir(new File(ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/tmp/"));
	}
	
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
}