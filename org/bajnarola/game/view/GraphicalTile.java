package org.bajnarola.game.view;

import org.newdawn.slick.SlickException;

public class GraphicalTile extends GraphicalElement {
	static final String GTILE_EXTENSION = ".png";
	static final String GTILE_PATH = "res/tiles/";
	public static final int TILE_SIZE = 256;
		
	public GraphicalTile(GameScene scene, String name, String coordinates, int direction, int globalCenterX, int globalCenterY, int size) throws SlickException{
		super(scene, GTILE_PATH + name + GTILE_EXTENSION, coordinates, direction, globalCenterX, globalCenterY, size);
	}
}
