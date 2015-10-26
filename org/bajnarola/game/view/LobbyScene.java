package org.bajnarola.game.view;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class LobbyScene extends IScene {

	InputBox unameInputBox;
	
	int textAreaWidth;
	
	public LobbyScene(Gui guiManager, Image background, bg_type backgroundType, int fontHeight) throws SlickException {
		super(guiManager, background, backgroundType);
		
		sceneType = scene_type.SCENE_LOBBY;
		
		textAreaWidth = guiManager.windowWidth/2;
		
		unameInputBox = new InputBox(textAreaWidth,
		                             fontHeight + 2, 
		                             guiManager.windowWidth/2,
		                             guiManager.windowHeight/4, 
		                             new Image("res/menu/inputbox.png")); 
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
		// TODO Auto-generated method stub

	}

	@Override
	public void backspacePressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		unameInputBox.draw(g);
	}

}
