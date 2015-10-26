package org.bajnarola.game.view;

import org.bajnarola.game.view.Gui.bg_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class LobbyScene extends IScene {

	public LobbyScene(Gui guiManager, Image background, bg_type backgroundType) {
		super(guiManager, background, backgroundType);
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
	}

}
