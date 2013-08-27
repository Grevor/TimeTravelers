package time.travelers.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class handling animations for a texture object.
 * @author Erik 
 * @version ALPHA - 0.1
 *
 */
public class Animation {
	public final int numFrames;
	public final int startFrameInTexture;
	public final long[] timestamps;
	public final int[] frames;
	public final long totalTime;
	public final String animationName;
	
	/**
	 * Creates a new Animation with the specified name, start frame and timestamps.
	 * @param name - The name of this animation.
	 * @param startFrame - The start frame.
	 * @param timestamps - The timestamps.
	 */
	public Animation(String name, int startFrame,long[] timestamps)
	{
		this.animationName = name;
		if(timestamps==null)
		{
			this.numFrames=0;
			this.startFrameInTexture=0;
			this.timestamps=new long[0];
			this.totalTime=0;
			this.frames=new int[0];
		}
		else
		{
			this.numFrames=timestamps.length;
			this.startFrameInTexture=startFrame;
			this.timestamps=timestamps.clone();
			long totTime=0;
			for(long i : timestamps)
				totTime+=i;
			this.totalTime=totTime;
			this.frames=new int[timestamps.length];
			for(int i=0;i<frames.length;i++)
				frames[i]=startFrame+i;
		}
	}
	
	/**
	 * Creates a new Animation with the specified name, start frame and timestamps.
	 * @param name - The name of this animation.
	 * @param startFrame - The frame indices.
	 * @param timestamps - The timestamps.
	 */
	public Animation(String name, int[] frames,long[] timestamps)
	{
		this.animationName = name;
		this.numFrames=timestamps.length;
		this.startFrameInTexture=0;
		this.timestamps=timestamps.clone();
		long totTime=0;
		for(long i : timestamps)
			totTime+=i;
		this.totalTime=totTime;
		this.frames=frames.clone();
	}
	
	/**
	 * Gets the next frame of this animation, given the specified time since the animation was started.
	 * @param timeSinceStart - The time since this animation was started.
	 * @return
	 * The now active frame.
	 */
	public int getNextFrame(long timeSinceStart)
	{
		long frameDec=timeSinceStart%this.totalTime;
		int i=0;
		for(;frameDec>this.timestamps[i];i++)
			frameDec-=this.timestamps[i];
		return frames[i];
	}

	

	
	
	
	public static Animation fromStream(InputStream s) throws IOException 
	{
		DataInputStream d=new DataInputStream(s);
		String name = d.readUTF();
		int num = d.readInt();
		long[] timestamps = new long[num];
		int[] frames = new int[num];
		for(int i = 0; i < num; i++)
		{
			timestamps[i] = d.readLong();
			frames[i] = d.readInt();
		}
		return new Animation(name, frames, timestamps);
	}
	
	public static void saveAnimation(Animation a, OutputStream s) throws IOException
	{
		DataOutputStream d = new DataOutputStream(s);
		d.writeUTF(a.animationName);
		d.writeInt(a.frames.length);
		for(int i=0; i < a.frames.length; i++)
		{
			d.writeLong(a.timestamps[i]);
			d.writeInt(a.frames[i]);
		}
	}
}
