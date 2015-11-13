package org.bajnarola.game.view;

import org.newdawn.slick.SlickException;

public class GraphicalMeeple extends GraphicalElement {
	static final String GMEEPLE_EXTENSION = ".png";
	static final String GMEEPLE_PATH = "res/meeples/";
	public static final int MEEPLE_SIZE = 128;
	private short tilePos; 
	
	public GraphicalMeeple(GameScene scene, int name, String coordinates, short tilePos, int globalCenterX, int globalCenterY, int size) throws SlickException {
		super(scene, GMEEPLE_PATH+name+GMEEPLE_EXTENSION, coordinates, 0, globalCenterX, globalCenterY, size, false);
		this.tilePos = tilePos;
	}

	@Override
	protected void setScaledVals(float smallScaleFactor){
		int tileSize = (int)(scene.tileSize * smallScaleFactor);
		int windowCenterX = (int)((float)scene.guiManager.windowWidth/2);
		int windowCenterY = (int)((float)(scene.guiManager.windowHeight-scene.turnTileSize+tileSize)/2);
		int tileCenterX = windowCenterX + (int)(((float)(GameScene.getLogicalX(coords)-scene.logicalCenterX))*tileSize);
		int tileCenterY = windowCenterY - (int)(((float)(GameScene.getLogicalY(coords)-scene.logicalCenterY))*tileSize);
		scaledSize = (int)(size * smallScaleFactor);
		scaledX = scene.getMeepleCoordX(tileCenterX, tileSize, tilePos)-scaledSize/2;
		scaledY = scene.getMeepleCoordY(tileCenterY, tileSize, tilePos)-scaledSize/2;
	}
}
