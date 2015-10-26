package org.bajnarola.game.view;

import org.newdawn.slick.SlickException;

public class GraphicalTile extends GraphicalElement {
	static final String GTILE_EXTENSION = ".jpg";
	static final String GTILE_PATH = "res/tiles/";
	public static final int TILE_SIZE = 512;
		
	public GraphicalTile(String name, String coordinates, int direction, int globalCenterX, int globalCenterY, int size) throws SlickException{
		super(GTILE_PATH + name + GTILE_EXTENSION, coordinates, direction, globalCenterX, globalCenterY, size);
	}
}
