package time.travelers.graphics;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class GraphicsDetails 
{
	public GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	public GraphicsDevice device = env.getDefaultScreenDevice();
	public DisplayMode[] modes = device.getDisplayModes();
	public DisplayMode currentMode = device.getDisplayMode();

	public boolean supportsFullScreen()
	{
		return device.isFullScreenSupported();
	}
}
