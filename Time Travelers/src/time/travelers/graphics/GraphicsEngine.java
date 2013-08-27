package time.travelers.graphics;

import java.util.ArrayList;
import java.util.Collection;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.FPSAnimator;

public class GraphicsEngine implements GLEventListener
{	
	private TextureBatch batch;
	//private TextureBatch pendingBatch;
	
	@SuppressWarnings("unused")
	private GLAutoDrawable currentSurface;
	private FPSAnimator animator;
	Camera2D camera;
	/**
	 * The only function that should set this flag is the {@code checkForAsyncUpdates()}. 
	 * This denotes that no updates can be done.
	 * This flag should, for optimization, not be set to true too long at a time.
	 * @see checkForAsyncUpdates()
	 */
	private boolean noUpdatesPossible = false;
	private long timeDeltaAnimations = 0;
	
	private ArrayList<ArrayList<Renderable>> renderLists;
	private ArrayList<ArrayList<Renderable>> pendingRenderList;
	
	/**
	 * Creates a new GraphicsEngine, drawing on the specified surface at a specified FPS, with the specified TextureBatch.
	 * @param canvas - The surface to draw on.
	 * @param FPS - The targeted FPS to render at. Please note that this is only a maximum limit that the engine will throttle towards.
	 * @param textures - The textures to be included as a start. 
	 * It is important that you in no way modify this object after passing it to the engine.
	 * <br>
	 * If passed null, the engine will auto-generate an empty TextureBatch. 
	 * Please note that this will most likely create strange in-game lag close to startup of the game.
	 */
	public GraphicsEngine(GLAutoDrawable canvas, int FPS, TextureBatch textures)
	{
		if(textures == null) {
			this.batch = new TextureBatch();
		} else {
			this.batch = textures;
		}
		this.currentSurface = canvas;
		this.animator = new FPSAnimator(canvas,FPS);
		this.camera = new Camera2D();
		canvas.addGLEventListener(this);
	}

	public void startRendering()
	{
		animator.start();
	}
	
	public void stopRendering()
	{
		animator.stop();
	}
	
	public TextureBatch getTextureBatch()
	{
		return this.batch;
	}
	
	/**
	 * Gets the pointer to the camera object of this GraphicsEngine. 
	 * Any change made to this object will reflect to the rendered graphics.
	 * @return
	 * The pointer to the camera.
	 */
	public Camera2D getCamera()
	{
		return this.camera;
	}
	
	/**
	 * Switches the render-list of this GraphicsEngine to the specified Renderables. 
	 * Changes will not be visible until the next render call after this function returns.
	 * <br><br>
	 * Please note that only ONE change to the render-list may be done at a time. 
	 * If such a change is already queued and waiting to take effect, this function fails.
	 * @param newRenderables - All Renderables to switch with.
	 * @return
	 * True if the switch succeeded, else false.
	 */
	public boolean switchRenderList(Collection<Renderable> newRenderables)
	{
		if(this.hasPendingRenderList()) {
			return false;
		}
		else {
			ArrayList<ArrayList<Renderable>> newList = new ArrayList<ArrayList<Renderable>>(batch.numberOfTextures());
			for(int i = 0; i < batch.numberOfTextures(); i++)
				newList.add(new ArrayList<Renderable>(40));
			for(Renderable r : newRenderables)
				addObjectToNewRenderList(newList, r);
			while(noUpdatesPossible); //Wait until updates are possible, this should only take a few ticks.
			this.pendingRenderList = newList;
			return true;
		}
	}
	
	/**
	 * Updates animations for all Renderables associated with this engine.
	 * @param timeDelta - The time since last update.
	 */
	public void updateAnimations(long timeDelta) {
		while(this.noUpdatesPossible); // wait until updating is possible. This may not be nessecary, and might be removed.
		
		this.timeDeltaAnimations += timeDelta;
	}
	
	//*************************************************************************************
	//
	// Private methods
	//
	//*************************************************************************************
	
	/**
	 * Gets the render-list for the specified texture.
	 * @param t - The TextureObject to get renderList for.
	 * @return
	 * The Render-list. If the Texture does not exist in the current batch, null is returned.
	 */
	private ArrayList<Renderable> getRenderListForTexture(TextureObject t)
	{
		if(batch.contains(t))
			return this.renderLists.get(batch.getIndexOf(t));
		else
			return null;
	}
	
	/**
	 * Adds a Renderable to the render list of choice. This will make sure the texture batch is up to date.
	 * @param newList - The list.
	 * @param r - the renderable.
	 */
	private void addObjectToNewRenderList(ArrayList<ArrayList<Renderable>> newList, Renderable r)
	{
		if(r == null || r.getTextureObject() == null)
			return;
		
		if(!batch.contains(r.getTextureObject()))
		{
			batch.addTexture(r.getTextureObject());
			newList.add(new ArrayList<Renderable>());
		}
		newList.get(batch.getIndexOf(r.getTextureObject())).add(r);
	}
	
	/**
	 * Checks if this GraphicsEngine has a pending renderlist update.
	 * @return
	 * True if it has, else false.
	 */
	private boolean hasPendingRenderList()
	{
		return this.pendingRenderList != null;
	}
	
	/**
	 * Updates the values used by the engine if any changes have been made to them.
	 */
	private void checkForAsyncUpdates()
	{
		//This is the only function which should set this flag.
		noUpdatesPossible = true;
		//Updates animations.
		updateAnimationsImpl(this.timeDeltaAnimations);
		if(this.hasPendingRenderList()) {
			//This order may or may not prove useful.
			ArrayList<ArrayList<Renderable>> foo = this.pendingRenderList;
			this.pendingRenderList = null;
			this.renderLists = foo;
		}
		//TODO may add changing of the texture batch, but it is not needed in this game.
		//Reset this flag to enable possible further pending requests.
		noUpdatesPossible = false;
	}
	
	private void updateAnimationsImpl(long timeDelta) {
		if(timeDelta == 0 || this.renderLists == null)
			return;
		for(ArrayList<Renderable> r : this.renderLists)
			for(Renderable rr : r)
				rr.updateAnimation(timeDelta);
		this.timeDeltaAnimations = 0;
	}
	
	private void setProjection(GL2 device)
	{
		device.glMatrixMode(GL2.GL_PROJECTION);
		device.glLoadIdentity();
		double w=camera.getTrueWidth();
		double h=camera.getTrueHeight();
		double x=camera.getMiddleX()-w/2;
		double y=camera.getMiddleY()-h/2;
		device.glOrtho(x, x+w, y+h, y, -10, 100);
	}
	
	private void setUpGL(GL2 device)
	{
		device.glClearColor(0, 0, 0, 0);
		device.glClear(GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT | GL2.GL_STENCIL_BUFFER_BIT);
		device.glEnable(GL2.GL_DEPTH_TEST);
		device.glDepthFunc(GL2.GL_LEQUAL);
		device.glEnable(GL2.GL_ALPHA_TEST);
		device.glAlphaFunc(GL2.GL_GREATER, 0);
		device.glEnable(GL2.GL_BLEND);
		device.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		device.glMatrixMode(GL2.GL_MODELVIEW);
		device.glLoadIdentity();
		device.glColor3f(1, 1, 1);
	}
	
	
	//*************************************************************************************
	//
	// GL methods
	//
	//*************************************************************************************

	@Override
	public void display(GLAutoDrawable a) 
	{
		GL2 device = a.getGL().getGL2();
		//Set projection.
		this.setProjection(device);
		//Set GL up for rendering a new scene.
		this.setUpGL(device);
		//Execute asynchronously requested updates.
		this.checkForAsyncUpdates();
		
		/*
		device.glBegin(GL2.GL_QUADS);
		device.glColor3b((byte)46, (byte)46, (byte)66);
		device.glVertex3d(1, 1, 0);
		device.glVertex3d(1, 2, 0);
		device.glVertex3d(2, 2, 0);
		device.glVertex3d(2, 1, 0);
		device.glEnd();
		*/
		
		//Render everything in the render list.
		for(int i = 0; i < batch.numberOfTextures(); i++) {
			batch.getTexture(i).enable(device);
			device.glBegin(GL2.GL_QUADS);
			ArrayList<Renderable> renderList = this.getRenderListForTexture(batch.getTexture(i));
			for(int obj = 0; obj < renderList.size(); obj++)
				renderList.get(obj).render(device); //Render all objects.
			device.glEnd();
			batch.getTexture(i).disable(device);
		}
	}

	@Override
	public void dispose(GLAutoDrawable a) 
	{
		GL2 device = a.getGL().getGL2();
		batch.dispose(device);
	}

	@Override
	public void init(GLAutoDrawable arg0) 
	{
		// Initialize all textures.
		for(int i=0; i < batch.numberOfTextures(); i++)
			batch.getTexture(i).getTexture();
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) 
	{
		//For now, we do nothing here.
	}
}
