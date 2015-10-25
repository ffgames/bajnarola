package org.bajnarola.game.view;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class MenuScene extends IScene {
	
	Button startButton, optionButton, exitButton;
	
	public MenuScene(Gui guiManager, Image background, bg_type backgroundType) throws SlickException{
		super(guiManager, background, backgroundType);
		sceneType = scene_type.SCENE_MENU;
		
		startButton = new Button(guiManager.windowWidth/4, guiManager.windowHeight/10, guiManager.windowWidth/2, guiManager.windowHeight/4, 
				new Image("res/menu/startInactive.png"), new Image("res/menu/startActive.png"));
		
		optionButton = new Button(guiManager.windowWidth/3, guiManager.windowHeight/9, guiManager.windowWidth/2, guiManager.windowHeight/4*2,
				new Image("res/menu/optionsInactive.png"), new Image("res/menu/optionsActive.png"));
		
		exitButton = new Button(guiManager.windowWidth/5, guiManager.windowHeight/11, guiManager.windowWidth/2, guiManager.windowHeight/4*3,
				new Image("res/menu/exitInactive.png"), new Image("res/menu/exitActive.png"));
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		startButton.draw();
		optionButton.draw();
		exitButton.draw();
	}

	@Override
	public void leftClick(int x, int y) {
		if(startButton.isClicked(x, y))
			guiManager.switchScene(scene_type.SCENE_GAME);
		else if(optionButton.isClicked(x, y))
			guiManager.switchScene(scene_type.SCENE_OPTIONS);
		else if(exitButton.isClicked(x, y))
			guiManager.exit();
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
		startButton.isClicked(newx, newy);
		optionButton.isClicked(newx, newy);
		exitButton.isClicked(newx, newy);
	}

}
