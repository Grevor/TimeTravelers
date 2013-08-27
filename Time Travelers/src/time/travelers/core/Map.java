package time.travelers.core;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import time.travelers.graphics.Renderable;

public class Map {

	private GridSpot[][] mapGrid;
	
	public Map(int x, int y) {
		mapGrid = new GridSpot[x][y];
	}
	
	public void recreateGridSpot(int x, int y, Terrain t) {
		if(x >= mapGrid.length || y >= mapGrid[0].length)
			return;
		else
			mapGrid[x][y] = new GridSpot(t);
	}
	
	public ArrayList<Renderable> getRenderables(int x, int y, int w, int h) {
		ArrayList<Renderable> ret = new ArrayList<Renderable>();
		//Fix values. TODO more fixing.
		if(x < 0)
			x = 0;
		if(w >= mapGrid.length)
			w = mapGrid.length;
		if(x + w >= mapGrid.length)
			w = mapGrid.length - x;
		
		for(int i = x; i < w + x; i++)
			for(int ii = y; ii < h + y; ii++) {
				ret.add(mapGrid[i][ii].terrain);
				if(mapGrid[i][ii].hasTerrainObject())
					ret.add(mapGrid[i][ii].terrainObj);
				if(mapGrid[i][ii].hasEntity())
					ret.add(mapGrid[i][ii].entity);
			}
		
		return ret;
	}
	
	public void renderVisible(GL2 device, int x, int y, int w, int h) {
		int endx = x + w;
		int endy = y + h;
		for(; x < endx; x++)
			for(; y < endy; y++)
			{
				mapGrid[x][y].terrain.render(device);
				mapGrid[x][y].terrainObj.render(device);
				mapGrid[x][y].entity.render(device);
			}
	}
}
