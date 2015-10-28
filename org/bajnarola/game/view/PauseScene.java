package org.bajnarola.game.view;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class PauseScene extends IScene {
	
	Button continueButton, optionButton, exitButton;

	public PauseScene(Gui guiManager, Image background, bg_type backgroundType) throws SlickException {
		super(guiManager, background, backgroundType);
		sceneType = scene_type.SCENE_PAUSE;
		
		continueButton = new Button(guiManager.windowWidth/3, guiManager.windowHeight/9, guiManager.windowWidth/2, guiManager.windowHeight/4, 
				new Image("res/menu/continueInactive.png"), new Image("res/menu/continueActive.png"));
		
		optionButton = new Button(guiManager.windowWidth/3, guiManager.windowHeight/9, guiManager.windowWidth/2, guiManager.windowHeight/4*2,
				new Image("res/menu/optionsInactive.png"), new Image("res/menu/optionsActive.png"));
		
		exitButton = new Button(guiManager.windowWidth/3, guiManager.windowHeight/9, guiManager.windowWidth/2, guiManager.windowHeight/4*3,
				new Image("res/menu/exitInactive.png"), new Image("res/menu/exitActive.png"));
	}
	
	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		continueButton.draw();
		optionButton.draw();
		exitButton.draw();
	}


	@Override
	public void leftClick(int x, int y) {
		if(continueButton.isClicked(x, y))
			escPressed();
		else if(optionButton.isClicked(x, y))
			guiManager.switchScene(scene_type.SCENE_OPTIONS);
		else if(exitButton.isClicked(x, y))
			guiManager.switchScene(scene_type.SCENE_MENU);
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
		guiManager.switchScene(scene_type.SCENE_GAME);
	}


	@Override
	public void backspacePressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		continueButton.isClicked(newx, newy);
		optionButton.isClicked(newx, newy);
		exitButton.isClicked(newx, newy);
	}

	@Override
	public void leftRelease(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rightRelease(int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
