package time.travelers.core;

import java.util.ArrayList;

import time.travelers.graphics.Renderable;
import time.travelers.graphics.TextureObject;

public class Terrain extends Renderable {
	public static final double gridWidth = 1;
	public static final double gridHeight = 1;
	public static final double gridZFactor = .001;
	
	//All objects in this one is thought of to have the position (0,0,0).
	private static ArrayList<TerrainTemplate> terrainTemplates = new ArrayList<TerrainTemplate>();
	
	private Traversal trav;
	private double traversalCost;
	
	public Terrain(TextureObject tex, Traversal t, double traversalCost) {
		this(tex, t, 0, traversalCost);
	}
	
	public Terrain(TextureObject tex, Traversal t, int randomType, double traversalCost) {
		super(tex, 0, 0, 0, gridWidth, gridHeight, true);
		this.setAnimation(randomType);
		this.traversalCost = traversalCost;
		this.trav = t;
	}
	
	/**
	 * Checks if the specified movement type can cross this terrain.
	 * @param movementType - The movement type.
	 * @return
	 * True if it can, else false.
	 */
	public boolean canTraverse(Traversal movementType) {
		return trav.canTraverse(movementType);
	}
	
	/**
	 * Gets the cost of traversing this terrain.
	 * @return
	 * The cost.
	 */
	public double getTraversalCost() {
		return this.traversalCost;
	}
	
	/**
	 * Creates a copy of this Terrain.
	 * @return
	 * The copy.
	 */
	public Terrain copy() {
		return new Terrain(this.getTextureObject(), this.trav.copy(), this.traversalCost);
	}
	
	/**
	 * Constructs a new, random terrain of the specified type.
	 * @param terrainID - The id of the TerrainTemplate to use.
	 * @param x - The grid position x of this terrain.
	 * @param y - The grid position y of this terrain.
	 * @return
	 * The new Terrain.
	 */
	public static Terrain fromTemplate(int terrainID, int x, int y) {
		//TODO implement real code for this shit.
		if(terrainID < 0 || terrainID >= terrainTemplates.size())
			throw new IllegalArgumentException("The requested ID does not exist.");
		Terrain ret = terrainTemplates.get(terrainID).getRandomTerrainOfThisType();
		ret.translatePosition(x * gridWidth, y * gridHeight, y * gridZFactor);
		return ret;
	}
	
	/**
	 * Constructs a new, random terrain of the specified type.
	 * @param name - The name of the TerrainTemplate to use.
	 * @param x - The grid position x of this terrain.
	 * @param y - The grid position y of this terrain.
	 * @return
	 * The new Terrain. If no match with the name was found, null.
	 */
	public static Terrain fromTemplate(String name, int x, int y) {
		for(int id = 0; id < terrainTemplates.size(); id++)
			if(terrainTemplates.get(id).name.toLowerCase() == name.toLowerCase())
				return fromTemplate(id, x, y);
		return null;
	}
	
	/**
	 * Adds a TerrainTemplate to the template list.
	 * @param t - The template to add.
	 * @return
	 * The textureID of the added template. If the adding failed, -1.
	 */
	public static int addTerrainTemplate(TerrainTemplate t) {
		if(t == null || terrainTemplates.contains(t))
			return -1;
		
		terrainTemplates.add(t);
		return terrainTemplates.size() - 1;
	}
}
