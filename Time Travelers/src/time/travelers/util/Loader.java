package time.travelers.util;

import time.travelers.core.Terrain;
import time.travelers.core.TerrainTemplate;
import time.travelers.core.Traversal;
import time.travelers.graphics.TextureBatch;
import time.travelers.graphics.TextureObject;

public class Loader {

	/**
	 * Loads the basic texture batch.
	 * <br><br>
	 * Please add all texture loading code in here for now.
	 * <br>
	 * Also add all TerrainTemplate code here for now.
	 * @return
	 * The basic texture batch for the game.
	 */
	public static TextureBatch getBaseBatch() {
		TextureBatch ret = new TextureBatch();
		
		getTerrainTextures(ret);
		
		return ret;
	}
	
	private static void getTerrainTextures(TextureBatch ret) {
		
		TextureObject test = new TextureObject("resources/textures/Gravity Down.png", 8, 1);
		test.addAnimation("std animation", 0, new long[] {900, 300, 200, 100, 100, 100, 200, 200});
		
		Terrain.addTerrainTemplate(new TerrainTemplate(new Terrain(test, new Traversal(true, false, true), 0), "test"));
		ret.addTexture(test);
	}
}
