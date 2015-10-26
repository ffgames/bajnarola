package org.bajnarola.game.view;

import org.newdawn.slick.SlickException;

public class GraphicalMeeple extends GraphicalElement {
	static final String GMEEPLE_EXTENSION = ".png";
	static final String GMEEPLE_PATH = "res/meeples/";
	public static final int MEEPLE_SIZE = 128;
	
	public GraphicalMeeple(int name, String coordinates, int globalCenterX, int globalCenterY, int size) throws SlickException {
		super(GMEEPLE_PATH+name+GMEEPLE_EXTENSION, coordinates, 0, globalCenterX, globalCenterY, size);
	}
}
