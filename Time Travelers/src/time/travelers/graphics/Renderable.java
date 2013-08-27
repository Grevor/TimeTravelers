package time.travelers.graphics;

import javax.media.opengl.GL2;

import time.travelers.util.MathUtil;


/**
 * Class which handles all graphical things. Extend this if you want an object to be able to render itself.<br>
 * Please make sure to override methods if really needed. Also please note that all objects are thought to be quads, 
 * 
 * @author Erik Nyström
 * @version ALPHA - 0.1
 *
 */
public abstract class Renderable 
{
	/**
	 * These are the coordinates at which the upper left corner of the object is thought of to be at.
	 */
	private double x = 0, y = 0, z = 0;
	/**
	 * These are the width and height of the object.<br>
	 * INVARIANT: Must be non-negative.
	 */
	private double width, height;
	/**
	 * These are the current animation and it's frame.
	 * INVARIANT: If this object has a TextureObject, these must be within the bounds of this objects TextureObject.
	 * @see TextureObject
	 */
	private int animation = 0, frame = 0;
	
	/**
	 * The time since the current animation started.
	 */
	private long timeSinceAnimationStart = 0;
	
	/**
	 * These are the texture-coordinates which are current. This is here for acceleration purposes, and will have this layout:
	 * <br>
	 * {@code x, y, width, height}
	 * <br>
	 * where (x, y) specifies the upper left corner.
	 */
	private double[] textureCoordinates;
	/**
	 * The textureObject this Renderable is associated with.
	 */
	private TextureObject texture;
	
	private boolean visible;
	
	private boolean willAnimate = true;
	
	public Renderable()
	{
		this(null,0,0,0,0,0,false);
	}
	
	public Renderable(TextureObject tex, double x, double y, double z, double w, double h, boolean visible)
	{
		this.texture = tex;
		this.translatePosition(x, y, z);
		this.width = w;
		this.height = h;
		this.visible = visible;
		if(this.hasTexture())
			this.updateAnimation(0);
	}
	
	
	//********************************************************************************************
	//
	//								Public Final Methods
	//
	//********************************************************************************************
	
	/**
	 * Check if this Renderable is closer to the camera than another renderable.
	 * @param r - The Renderable to check against.
	 * @return
	 * True if this Renderable is closer than the specified Renderable, else false.
	 */
	public final boolean isCloserThan(Renderable r) {return this.z>r.z;}
	
	public final boolean isVisible(){return this.visible;}
	
	/**
	 * Translates the position of this object the specified length in all directions.
	 * @param x - The length to translate this object in the x-direction.
	 * @param y - The length to translate this object in the y-direction.
	 * @param z - The length to translate this object in the z-direction.
	 */
	public synchronized void translatePosition(double x,double y,double z)
	{
		this.x+=x;
		this.y+=y;
		this.z+=z;
	}
	
	/**
	 * Checks if this object has a texture.
	 * @return True if this object has a texture, else false.
	 */
	public final boolean hasTexture(){return this.texture!=null;}
	
	public TextureObject getTextureObject() { return this.texture;}
	
	/**
	 * Renders this object. The objects texture is thought to be bound and enabled, and the device is set to GL_QUADS.
	 * <br><br>
	 * PRE: It is imperative that the user must have fed the device 4*n number of vertices (where n is a non-negative integer),
	 * or results are unpredictable. This function guarantees that it will keep this pre-condition.
	 * @param device - The device on which to render.
	 */
	public synchronized void render(GL2 device)
	{
		if(this.visible)
		{
			double posXMax=x+width;
			double posYMax=y+height;
			
			double texXMax=getTexX()+getTexWidth();
			double texYMax=getTexY()+getTexHeight();
			
			device.glTexCoord2d(getTexX(), texYMax);
			device.glVertex3d(x, y, z);
			device.glTexCoord2d(texXMax, texYMax);
			device.glVertex3d(posXMax, y, z);
			device.glTexCoord2d(texXMax, getTexY());
			device.glVertex3d(posXMax, posYMax, z);
			device.glTexCoord2d(getTexX(), getTexY());
			device.glVertex3d(x, posYMax, z);
		}
	}
	
	/**
	 * Updates the animation of this object.
	 * @param timeDelta - the time (in an arbitrary unit) since the last call to this function. 
	 * Please note that this is an offset in time to advance the animation by.<br><br>
	 * 
	 * Also note that if this Renderable's willAnimate = false, this function does nothing.
	 */
	public synchronized final void updateAnimation(long timeDelta)
	{
		if(this.willAnimate && this.hasTexture())
		{
			frame = texture.getNextFrame(this.timeSinceAnimationStart += timeDelta, animation);
			this.textureCoordinates = texture.getTextureCoords(frame);
		}
	}
	
	/**
	 * Sets the animation of this Renderable to the specified animation. If the animation bounds are disrespected, 
	 * it will set the animation to the closest value.
	 * @param newAnimation - The index of the animation to set it to.
	 * @return
	 * The actual set animation. If this Renderable has no texture or the texture has no animations, returns -1.
	 */
	public synchronized final int setAnimation(int newAnimation)
	{
		if(this.hasTexture())
		{
			newAnimation = MathUtil.getValueFittingBounds(newAnimation, this.texture.getNumAnimations() - 1, 0);
			
			this.animation = newAnimation;
			this.timeSinceAnimationStart = 0;
			this.updateAnimation(0);
			return newAnimation;
		}
		return -1;
	}
	
	/**
	 * Sets the animation of this Renderable to the specified animation. If the animation has more than one animation with the 
	 * specified name, the first one will be chosen.
	 * @param name - The name of the animation to set it to.
	 * @return
	 * The actual set animation. If this Renderable has no texture, the animation is not found, or the texture has no 
	 * animations, returns -1.
	 */
	public final int setAnimation(String name)
	{
		if(this.hasTexture())
		{
			int newAni = this.texture.getAnimationIndex(name);
			if(newAni == -1)
				return -1;
			else
				return this.setAnimation(newAni);
		}
		return -1;
	}
	
	
	
	
	
	
	public double getRenderX()
	{
		return x;
	}
	
	public double getRenderY()
	{
		return y;
	}
	
	public double getRenderZ()
	{
		return z;
	}
	
	public double getRenderWidth()
	{
		return width;
	}
	
	public double getRenderHeight()
	{
		return height;
	}
	
	private double getTexX()
	{
		return this.textureCoordinates[0];
	}
	
	private double getTexY()
	{
		return this.textureCoordinates[1];
	}
	
	private double getTexWidth()
	{
		return this.textureCoordinates[2];
	}
	
	private double getTexHeight()
	{
		return this.textureCoordinates[3];
	}
}
