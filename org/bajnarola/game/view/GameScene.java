package org.bajnarola.game.view;

import org.bajnarola.game.model.Board;
import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class GameScene extends IScene {
	
	static final float SCROLL_AREA_RATEO = (float)0.05;
	
	private int leftBorderX, rightBorderX, upperBorderY, lowerBorderY;
	public int xOff, yOff, maxX, maxY;
	
	String message2 = "";

	public GameScene(Gui guiManager, Image background, bg_type backgroundType) {
		super(guiManager, background, backgroundType);
		sceneType = scene_type.SCENE_GAME;
		
		leftBorderX = (int)((float)guiManager.windowWidth * SCROLL_AREA_RATEO);
		rightBorderX = guiManager.windowWidth - leftBorderX;
		upperBorderY= (int)((float)guiManager.windowHeight * SCROLL_AREA_RATEO);
		lowerBorderY = guiManager.windowHeight - upperBorderY;
		
		xOff = yOff = 0;
		// TODO: check if tiles have to be resized in "zoom in" mode
		maxX = maxY = GraphicalTile.TILE_SIZE * Board.TOTAL_TILES_COUNT;
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		
		g.drawString(message2, 10, 50);
	}

	@Override
	public void leftClick(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rightClick(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wheelMoved(boolean up) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void escPressed() {
		guiManager.switchScene(scene_type.SCENE_PAUSE);
	}

	@Override
	public void backspacePressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		message2 = "";
		if(newx < leftBorderX && newx > 2){
			message2 += "Left ";
		} else if (newx > rightBorderX && newx < guiManager.windowWidth-3){
			message2 += "Right ";
		}
		if(newy < upperBorderY && newy > 2){
			message2 += "Up";
		} else if (newy > lowerBorderY && newy < guiManager.windowHeight-3){
			message2 += "Down";
		}
	}

}
