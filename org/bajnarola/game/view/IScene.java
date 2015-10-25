package org.bajnarola.game.view;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public abstract class IScene {
	Gui guiManager;

	Image background;
	Gui.bg_type backgroundType;
	
	public Gui.scene_type sceneType;
	
	public IScene(Gui guiManager, Image background, bg_type backgroundType){
		this.guiManager = guiManager;
		this.background = background;
		this.backgroundType = backgroundType;
		this.sceneType = sceneType;
	}
	
	public abstract HitBox click(int x, int y);
	
	public abstract void render(GameContainer gc, Graphics g);
}
