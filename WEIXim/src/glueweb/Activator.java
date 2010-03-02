package glueweb;

import glueweb.util.ImageCache;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "WEIXim";

	private static Activator plugin;
	public static Activator getPlugin() {
		return plugin;
	}
	
	private ImageCache imageCache;

	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		imageCache = new ImageCache(PLUGIN_ID);
	}

	@Override
    public void stop(BundleContext context) throws Exception {
		plugin = null;
		imageCache.dispose();
		imageCache = null;
		super.stop(context);
	}
	

	public ImageCache getImageCache() {
		return imageCache;
	}
}
