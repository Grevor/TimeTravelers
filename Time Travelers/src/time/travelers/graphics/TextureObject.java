package time.travelers.graphics;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
/**
 * Class encapsulating a Texture in the game. This class is responsible for animation handling and the like.
 * @author Dargoon
 * @version Alpha - 0.1
 */
public class TextureObject {
	//The number of vertices per object. for this 2d package, everything is thought of to be circles.
	private static final int verticesPerObject=4;
	//Contains all initialized textures.
	private static Hashtable<String,Texture> textureTable=new Hashtable<String,Texture>(10);

	private boolean hasTriedInit=false;
	private Texture tex;
	private final String filename;
	private ArrayList<Animation> animations = new ArrayList<Animation>(10);
	private final int framesX, framesY;
	private final double frameWidth,frameHeight;

	/**
	 * Creates a new TextureObject, with data from the specified file.
	 * @param file - The file from which to get the texture.
	 * @param framesX - Number of frames per row.
	 * @param framesY - Number of frame-rows.
	 */
	public TextureObject(String file, int framesX, int framesY)
	{
		filename = file;
		this.framesX = framesX;
		this.framesY = framesY;
		this.frameWidth = 1.0 / framesX;
		this.frameHeight = 1.0 / framesY;
	}

	/**
	 * Adds an Animation to this texture, using given timestamps.
	 * <br><br>
	 * This function makes sure that the animation keeps within the boundaries of the texture.
	 * @param name - The name of the Animation.
	 * @param firstFrame - The zero-based index of the first frame in this Animation. 
	 * Each timestamp is then thought to be the next frame's timestamp.
	 * @param timestamps - The timestamps for all frames in the Animation.
	 * @return
	 * True if the Animation was successfully added, else false.
	 */
	public boolean addAnimation(String name,int firstFrame, long[] timestamps)
	{
		int firstFreeFrame = firstFrame;

		if(timestamps.length + firstFreeFrame > framesX * framesY) {
			System.out.println(
					"Could not add Animation \"" + name + "\" to the texture, as that would cause the "
							+ "Animation to point to a non-existing frame.");
			return false; //To indicate failure.
		} else {
			animations.add(new Animation(name, firstFreeFrame, timestamps));
			return true;
		}
	}
	
	/**
	 * Adds an animation to this TextureObject.
	 * @param a - The animation to add.
	 * @return
	 * True if the Animation was successfully added, else false.
	 */
	public boolean addAnimation(Animation a)
	{
		if(a!=null)
		{
			animations.add(a);
			return true;
		}
		return false;
	}

	/**
	 * Gets the currently displayed frame, given the current animation and time since it was started.
	 * @param timeSinceAnimationStart - The time since the animation was started.
	 * @param animation - The index of the current animation.
	 * @return
	 * The frame in the animation
	 */
	public int getNextFrame(long timeSinceAnimationStart,int animation)
	{
		return animations.get(animation).getNextFrame(timeSinceAnimationStart);
	}

	/**
	 * Gets all vertex coordinates for this texture, given the specified frame.
	 * @param frame - The frame to get coordinates for.
	 * @return
	 * The TextureCoordinates corresponding to the specified frame in this TextureObject, given the frame exists.
	 * Else, the first frame of this TextureObject is returned.
	 */
	public double[] getTextureCoords(int frame)
	{
		//Gets the texture-frame.
		int xFrame = frame % this.framesX;
		int yFrame = frame / this.framesX;
		
		double startx=frameWidth * xFrame;
		double starty=frameHeight * yFrame;
		
		double[] texCoords = new double[] 
				{
					startx, 	starty,
					frameWidth, frameHeight
				};


		return texCoords;
	}

	/**
	 * Gets the texture of this TextureObject.
	 * @return
	 */
	public Texture getTexture()
	{
		if(!hasTriedInit && tex == null)
		{
			hasTriedInit = true;
			tex = getTextureImpl();
		}
		return this.tex;
	}
	
	/**
	 * Enables and binds this texture.
	 * @param device - The GL device to use.
	 */
	public void enable(GL device)
	{
		Texture t = this.getTexture();
		t.enable(device);
		t.bind(device);
	}
	
	/**
	 * Disables this texture.
	 * @param device - The GL device to use.
	 */
	public void disable(GL device)
	{
		Texture t = getTexture();
		t.disable(device);
	}
	
	/**
	 * Gets or otherwise initializes the texture of this TextureObject, if possible.
	 * @return
	 */
	private Texture getTextureImpl()
	{
		if(!textureTable.contains(this.filename))
		{
			if(textureTable.contains(this.filename))
				return textureTable.get(this.filename);
			else
				return getAndAddTextureToTable(new File(this.filename), this.filename);
		}
		return this.tex;
	}
	
	/**
	 * Adds the texture to the Texture-table.
	 * @param textureFile - The file.
	 * @param filename - The filename of the file.
	 * @return
	 * The Texture that was added to the table. If an exception occurred and no texture was added, returns null.
	 */
	private static Texture getAndAddTextureToTable(File textureFile,String filename)
	{
		try
		{
			Texture ret = TextureIO.newTexture(textureFile, true);
			TextureObject.textureTable.put(filename, ret);
			return ret;
		} 
		catch (GLException | IOException e) 
		{
			System.out.println("Error while reading TextureData from file: Cannot read data.");
			e.printStackTrace();
			return null;
		}
	}

	
	//**************************************************************************************
	//
	// Getters and setters
	//
	//**************************************************************************************
	
	
	/**
	 * Gets the number of frames per row in this texture.
	 * @return - The number of frames per row.
	 */
	public int getFramesX()
	{
		return this.framesX;
	}

	/**
	 * Gets the number of frames per column in this texture.
	 * @return - The number of frames per column.
	 */
	public int getFramesY()
	{
		return this.framesY;
	}

	/**
	 * Gets the number of vertices in this TextureObjects vertex-map.
	 * @return - The number of vertices in the objects described by this TextureObject.
	 */
	public int getVertexCount()
	{
		return TextureObject.verticesPerObject;
	}

	/**
	 * Gets the texel frame width of a frame in the texture.
	 * @return
	 * The width, if the texture is successfully initialized. Else, -1.
	 */
	public int getTexelFrameWidth()
	{
		if(tex != null)
			return this.tex.getWidth() / this.framesX;
		else
			return -1;
	}

	/**
	 * Gets the texel frame height of a frame in the texture.
	 * @return
	 * The height, if the texture is successfully initialized. Else, -1.
	 */
	public int getTexelFrameHeight()
	{
		if(tex != null)
			return this.tex.getHeight() / this.framesY;
		else
			return -1;
	}
	
	/**
	 * Gets the number of animations tied with this TextureObject.
	 * @return
	 * The total number of animations this TextureObject has.
	 */
	public int getNumAnimations()
	{
		return this.animations.size();
	}
	
	/**
	 * Gets the index of the animation with the specified name.
	 * @param name - The name of the animation.
	 * @return
	 * The animation's index, if it exists. if not, -1.
	 */
	public int getAnimationIndex(String name)
	{
		for(int i = 0; i < this.animations.size(); i++)
		{
			if(this.animations.get(i).animationName == name)
				return i;
		}
		return -1;
	}
	
	
	
	//**************************************************************************************
	//
	// Static methods
	//
	//**************************************************************************************
	
	/**
	 * Constructs a TextureObject from the specified stream.
	 * <br>
	 * The stream must contain valid TextureObject data starting at the current position. Should the function err in any way,
	 * the stream will be restored to it's incoming state.
	 * @param s - The stream to get data from.
	 * @return
	 * The TextureObject described in the stream. If the stream contains invalid data, returns null.
	 */
	public static TextureObject fromStream(InputStream s)
	{
		DataInputStream d=new DataInputStream(new BufferedInputStream(s));
		d.mark(Integer.MAX_VALUE);
		try 
		{
			TextureObject ret = new TextureObject(d.readUTF(), d.readInt(), d.readInt());
			int animations = d.readInt();
			for(;animations > 0; animations--)
				ret.addAnimation(Animation.fromStream(s));
			return ret;
		}
		catch (IOException e) 
		{
			System.out.println("Could not extract TextureObject data from stream.");
			try {
				d.reset();
			} catch (IOException e1) { } //This will never happen.
			return null;
		}
	}

	/**
	 * Disposes this TextureObjects native resources, and releases all pointers to it.
	 * @param device - The device that "owns" this TextureObject.
	 */
	public void dispose(GL2 device) {
		if(this.tex != null)
		{
			tex.destroy(device);
			tex = null;
		}
	}
}
