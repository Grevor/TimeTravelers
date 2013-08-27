package time.travelers.core;

import java.util.ArrayList;

import time.travelers.event.Event;

public class GridSpot {
	Terrain terrain;
	GameObject terrainObj;
	GameObject entity;
	ArrayList<Event> e;
	
	public GridSpot(Terrain t) {
		this.terrain = t;
	}
	
	/*public boolean canTraverse(Traversal t) {
		boolean ret = t.canTraverse(t);
		if(this.hasTerrainObject())
			ret &= terrainObj.canTraverse(t);
		if(this.hasEntity())
			ret &= this.entity.canTraverse(t);
		return ret;
	}*/
	
	public double getTraversalCost() {
		return terrain.getTraversalCost();
	}
	
	public void triggerEvents(Map m) {
		for(int i = 0; i < e.size(); i++) {
			
		}
	}
	
	public boolean hasTerrainObject() {
		return terrainObj != null;
	}
	
	public boolean hasEntity() {
		return entity != null;
	}
}
