package org.bajnarola.game.view;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class OptionsScene extends IScene {

	public scene_type prevScene;
	
	Button buttonFullScreen, buttonRes1, buttonRes2, buttonRes3;
	
	Image container;
	float containerWidth, containerHeight;
	float containerPosX, containerPosY;
	
	public OptionsScene(Gui guiManager, Image background, bg_type backgroundType) throws SlickException {
		super(guiManager, background, backgroundType);
		prevScene = null;
		
		containerWidth = (float)((float)guiManager.windowWidth *(float) 0.7);
		containerHeight = (float)((float)guiManager.windowHeight * (float)0.8);
		containerPosX = (guiManager.windowWidth/2) - containerWidth/2;
		containerPosY = (guiManager.windowHeight/2) - containerHeight/2;
		
		
		buttonRes1 = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                (int)containerPosX + 20,
                new Image("res/menu/res1Inactive.png"),
                new Image("res/menu/res1Active.png"));
		
		buttonRes2 = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                (int)containerPosX + 120,
                new Image("res/menu/res2Inactive.png"),
                new Image("res/menu/res2Active.png"));
		
		buttonRes3 = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                (int)containerPosX + 220,
                new Image("res/menu/res3Inactive.png"),
                new Image("res/menu/res3Active.png"));
		
		buttonFullScreen = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                (int)containerPosX + 320,
                new Image("res/menu/fullscreenInactive.png"),
                new Image("res/menu/fullscreenActive.png"));
		
		container = new Image("res/menu/optionsContainer.png");
	}

	@Override
	public void leftClick(int x, int y) {
		if (buttonFullScreen.hits(x, y)) {
			if (buttonFullScreen.isActive())
				buttonFullScreen.deactivate();
			else
				buttonFullScreen.activate();
		}
		if (buttonRes1.hits(x, y)) {
			if (buttonRes1.isActive())
				buttonRes1.deactivate();
			else
				buttonRes1.activate();
		}
		if (buttonRes2.hits(x, y)) {
			if (buttonRes2.isActive())
				buttonRes2.deactivate();
			else
				buttonRes2.activate();
		}
		
		if (buttonRes3.hits(x, y)) {
			if (buttonRes3.isActive())
				buttonRes3.deactivate();
			else
				buttonRes3.activate();
		}
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
		guiManager.switchScene(prevScene);
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
		container.draw(containerPosX, containerPosY,
		               containerWidth, containerHeight);
		
		buttonRes1.draw();
		buttonRes2.draw();
		buttonRes3.draw();
		buttonFullScreen.draw();		
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
