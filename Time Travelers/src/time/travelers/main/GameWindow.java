package time.travelers.main;

import java.util.ArrayList;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import time.travelers.core.GameObject;
import time.travelers.core.GameObject.Direction;
import time.travelers.core.Map;
import time.travelers.core.Terrain;
import time.travelers.core.Unit;
import time.travelers.graphics.Camera2D;
import time.travelers.graphics.GraphicsEngine;
import time.travelers.graphics.Renderable;
import time.travelers.util.Loader;

@SuppressWarnings("serial")
public class GameWindow extends JFrame {
	
	private static final String title = "ALPHA - Time Travelers";
	private int tickCheck = 0;
	
	private GLCanvas canvas;
	private GraphicsEngine graphics;
	private Map currentMap;
	private Camera2D camera;
	private GameObject gameObject;
	
	public GameWindow() {
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(title);
		
		canvas = new GLCanvas();
		graphics = new GraphicsEngine(canvas, 60, Loader.getBaseBatch());
		camera = graphics.getCamera();
		camera.setHeight(10);
		camera.setWidth(10);
		camera.translateCamera(0, 0);
		camera.setZoom(1);
		
		currentMap = new Map(10,10);
		for(int x = 0; x < 10; x++)
			for(int y = 0; y < 10; y++)
				currentMap.recreateGridSpot(x, y, Terrain.fromTemplate(0, x, y));
		this.setSize(900, 600);
		this.setResizable(false);
		this.add(canvas);
		
		//graphics.switchRenderList(currentMap.getRenderables(2, 2, 5, 5));
		gameObject = new GameObject(graphics.getTextureBatch().getTexture(0), true, 0, 0);
		ArrayList<Renderable> test = new ArrayList<Renderable>();
		test.add(gameObject);
		graphics.switchRenderList(test);
		graphics.startRendering();
		this.setVisible(true);
	}

	public void tick(int timeDelta) {
		graphics.updateAnimations(timeDelta);
		tickCheck++;
		if(tickCheck % 50 == 0)
		{
			gameObject.Move(Direction.Down);
			gameObject.Move(Direction.Right);
		}
			//graphics.switchRenderList(currentMap.getRenderables(0, 0, Math.min(10, tickCheck/50), Math.min(10, tickCheck/50)));
			
	}
}
