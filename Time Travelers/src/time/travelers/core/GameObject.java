package time.travelers.core;

import time.travelers.graphics.Renderable;
import time.travelers.graphics.TextureObject;

public class GameObject extends Renderable 
{
	public enum Direction{Up, Down, Left,Right};
	double positionX, positionY;
	
	public GameObject(TextureObject texture, double x, double y, double z, double w, double h, boolean visiblety, double positionX, double positionY) 
	{
		super(texture, x, y, z, w, h, visiblety);
		this.positionX = positionX;
		this.positionY = positionY;
	}
	/**
	 * 	
	 * @param texture
	 * @param visibilety
	 */
	public GameObject(TextureObject texture, boolean visibilety, double positionX, double positionY) // Light
	{
		this(texture, 0,0,0,1,1,visibilety,positionX,positionY);
	}
	
	public void Move(Direction direction)
	{	
		switch(direction)
		{
		case Down:
			super.translatePosition(0, +1, 0);
			break;
		case Left:
			super.translatePosition(0, -1, 0);
			break;
		case Right:
			super.translatePosition(+1, 0, 0);
			break;
		case Up:
			super.translatePosition(-1, 0, 0);
			break;
		default:
			break;
			
		}

	}

		// TODO Auto-generated constructor stub

}
