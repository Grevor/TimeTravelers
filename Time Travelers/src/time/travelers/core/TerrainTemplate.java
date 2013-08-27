package time.travelers.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

public class TerrainTemplate {
	private static Random r = new Random();
	private final Terrain t;
	public final String name;
	
	public TerrainTemplate(Terrain t, String name) {
		this.t = t;
		this.name = name;
	}
	
	public Terrain getRandomTerrainOfThisType() {
		int rng = r.nextInt(t.getTextureObject().getNumAnimations());
		Terrain ret = t.copy();
		ret.setAnimation(rng);
		return ret;
	}
	
	
	
	public static TerrainTemplate fromStream(DataInputStream s) {
		try {
			s.readUTF();
			//TODO add loading of terrain.
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void toStream(TerrainTemplate t, DataOutputStream s) {
		try {
			s.writeUTF(t.name);
			//TODO add saving of the terrain object itself.
		} catch (Exception e) {
			
		}
	}
}
