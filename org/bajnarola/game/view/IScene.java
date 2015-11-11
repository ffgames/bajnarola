package org.bajnarola.game.view;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;

public abstract class IScene {
	Gui guiManager;

	public Image background;
	Gui.bg_type backgroundType;
	public List<Music> soundtrack;
	public int currentSong;
	
	public Gui.scene_type sceneType;
	
	public IScene(Gui guiManager, Image background, Gui.bg_type backgroundType, List<Music> soundtrack){
		this.guiManager = guiManager;
		this.background = background;
		this.backgroundType = backgroundType;
		this.soundtrack = soundtrack;
		this.currentSong = 0;
	}
	
	public abstract void leftClick(int x, int y);
	public abstract void rightClick(int x, int y);
	public abstract void leftRelease(int x, int y);
	public abstract void rightRelease(int x, int y);
	public abstract void wheelMoved(boolean up);
	public abstract void enterPressed();
	public abstract void escPressed();
	public abstract void backspacePressed();
	public abstract void mouseMoved(int oldx, int oldy, int newx, int newy);
	
	public abstract void keyPressed(int key, char c);
	public abstract void keyReleased(int key, char c);
	
	public abstract void render(GameContainer gc, Graphics g);
}
