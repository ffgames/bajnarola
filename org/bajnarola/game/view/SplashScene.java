package org.bajnarola.game.view;

import java.util.List;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;

public class SplashScene extends IScene {

	public SplashScene(Gui guiManager, Image background, bg_type backgroundType, List<Music> soundtrack) {
		super(guiManager, background, backgroundType, soundtrack);
		sceneType = scene_type.SCENE_SPLASH;
	}


	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
	}
	
	@Override
	public void leftClick(int x, int y) {}

	@Override
	public void rightClick(int x, int y) {}

	@Override
	public void leftRelease(int x, int y) {}

	@Override
	public void rightRelease(int x, int y) {}

	@Override
	public void wheelMoved(boolean up) {}

	@Override
	public void enterPressed() {}

	@Override
	public void escPressed() {}

	@Override
	public void backspacePressed() {}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {}

	@Override
	public void keyPressed(int key, char c) {}

	@Override
	public void keyReleased(int key, char c) {}

}
