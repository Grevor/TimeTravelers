package time.travelers.core;

import time.travelers.graphics.Renderable;
import time.travelers.graphics.TextureObject;

public class GameObject extends Renderable 
{
	public enum Direction{Up, Down, Left,Right};
	double positionX, positionY;
	
	public GameObject(TextureObject texture, double x, double y, double z, double w, double h, boolean visibility, double positionX, double positionY) 
	{
		super(texture, x, y, z, w, h, visibility);
		this.positionX = positionX;
		this.positionY = positionY;
	}
	/**
	 * 
	 * @param texture - The texture of the object.
	 * @param visibility - visibility true or false.
	 * @param positionX - Starting position X of the GameObject.
	 * @param positionY - Starting position Y of the GameObject.
	 */
	public GameObject(TextureObject texture, boolean visibility, double positionX, double positionY) // Light
	{
		this(texture, 0,0,0,1,1,visibility,positionX,positionY);
	}
	/**
	 * Moves the object one tile (map cell) in the directions (Up, Down, Left, Right)
	 * @param direction
	 */
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
