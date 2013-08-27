package time.travelers.graphics;

import java.util.ArrayList;

import javax.media.opengl.GL2;

/**
 * Class handling textures.
 * @author Erik
 * @version ALPHA - 0.1
 */
public class TextureBatch 
{
	private ArrayList<TextureObject> batch = new ArrayList<TextureObject>(100);
	private ArrayList<ArrayList<Renderable>> renderLists = new ArrayList<ArrayList<Renderable>>(100);

	public TextureBatch() { }
	
	public void addTexture(TextureObject t)
	{
		if(t != null && !batch.contains(t))
		{
			batch.add(t);
			renderLists.add(new ArrayList<Renderable>());
		}
	}
	
	public TextureObject getTexture(int index)
	{
		if(index < 0 && index >= batch.size())
			return null;
		else
			return batch.get(index);
	}
	
	public int numberOfTextures()
	{
		return batch.size();
	}
	
	public boolean contains(TextureObject t)
	{
		return batch.contains(t);
	}
	
	public int getIndexOf(TextureObject t)
	{
		return batch.indexOf(t);
	}
	
	

	public void dispose(GL2 device)
	{
		for(TextureObject t : batch)
			t.dispose(device);
	}
}
