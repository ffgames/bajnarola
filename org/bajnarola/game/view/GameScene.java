package org.bajnarola.game.view;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class GameScene extends IScene {

	public GameScene(Gui guiManager, Image background, bg_type backgroundType) {
		super(guiManager, background, backgroundType);
		sceneType = scene_type.SCENE_GAME;
	}

	@Override
	public HitBox click(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		
	}

}
