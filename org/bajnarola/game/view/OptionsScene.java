package org.bajnarola.game.view;

import java.util.ArrayList;
import java.util.List;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class OptionsScene extends IScene {

	public scene_type prevScene;
	
	List<Button> buttons;
	Button buttonFullScreen, buttonRes1, buttonRes2, buttonRes3;
	Button backButton;
	Image container;
	float containerWidth, containerHeight;
	float containerPosX, containerPosY;

	
	public OptionsScene(Gui guiManager, Image background, bg_type backgroundType) throws SlickException {
		super(guiManager, background, backgroundType);
		prevScene = null;
		
		buttons = new ArrayList<Button>();
		
		containerWidth = (float)((float)guiManager.windowWidth *(float) 0.7);
		containerHeight = (float)((float)guiManager.windowHeight * (float)0.8);
		containerPosX = (guiManager.windowWidth/2) - containerWidth/2;
		containerPosY = (guiManager.windowHeight/2) - containerHeight/2 - 50;
		
		
		buttonRes1 = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                (int) containerPosY + (int)(containerHeight / 5),
                new Image("res/menu/res1Inactive.png"),
                new Image("res/menu/res1Active.png"));
		buttons.add(buttonRes1);
		
		if (Display.getDesktopDisplayMode().getHeight() >= 720) {

			buttonRes2 = new Button(guiManager.windowWidth/4,
	                guiManager.windowHeight/10,
	                guiManager.windowWidth/2,
	                (int) containerPosY + (int)(containerHeight / 5 * 2),
	                new Image("res/menu/res2Inactive.png"),
	                new Image("res/menu/res2Active.png"));
			buttons.add(buttonRes2);
		}

		if (Display.getDesktopDisplayMode().getHeight() >= 1080) {
			buttonRes3 = new Button(guiManager.windowWidth/4,
	                guiManager.windowHeight/10,
	                guiManager.windowWidth/2,
	                (int) containerPosY + (int)(containerHeight / 5 * 3),
	                new Image("res/menu/res3Inactive.png"),
	                new Image("res/menu/res3Active.png"));
			buttons.add(buttonRes3);
		}

		
		buttonFullScreen = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                (int) containerPosY + (int)(containerHeight / 5 * 4),
                new Image("res/menu/fullscreenInactive.png"),
                new Image("res/menu/fullscreenActive.png"));
		buttons.add(buttonFullScreen);

		container = new Image("res/menu/optionsContainer.png");
		
		int resx = guiManager.getResolutionOptions().getResx();
		int resy = guiManager.getResolutionOptions().getResy();
		boolean fullscreen = guiManager.getResolutionOptions().isFullscreen();
		
		if (fullscreen)
			buttonFullScreen.activate();
		else if (resx == 800 && resy == 600)
			buttonRes1.activate();
		else if (resx == 1280 && resy == 720)
			buttonRes2.activate();
		else if (resx == 1920 && resy == 1080)
			buttonRes3.activate();
		
		backButton = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                guiManager.windowHeight - 75,
                new Image("res/menu/backInactive.png"),
                new Image("res/menu/backActive.png"));
	}

	@Override
	public void leftClick(int x, int y) {
		Button b = null;
		AppGameContainer gc = (AppGameContainer) guiManager.container;
		int resx = 0, resy = 0;
		boolean fullscreen = false;
		
		for (int i = 0; i < this.buttons.size(); i++) {
			b = this.buttons.get(i);
			
			try {
			if (b.hits(x, y)) {
				b.activate();
				
				switch (i) {
					case 0:
						resx = 800;
						resy = 600;
						fullscreen = false;
						break;
					case 1:
						resx = 1280;
						resy = 720;
						fullscreen = false;
						break;
					case 2:
						resx = 1920;
						resy = 1080;
						fullscreen = false;						
						break;
					case 3: {
						resx = Display.getAvailableDisplayModes()[0].getWidth();
						resy = Display.getAvailableDisplayModes()[0].getHeight();
						fullscreen = true;
						break;
					}
				}
				for (Button b2: this.buttons) {
					if (!b2.equals(b))
						b2.deactivate();
				}
								
				guiManager.setResolutionOptions(resx, resy, fullscreen);
				gc.setDisplayMode(resx, resy, fullscreen);
				gc.reinit();
				
				break;
			}
			} catch (SlickException | LWJGLException e) {
				e.printStackTrace();
			}
		}
		
		if (backButton.isClicked(x, y))
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
		guiManager.switchScene(prevScene);
	}

	@Override
	public void backspacePressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		backButton.isClicked(newx, newy);
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		container.draw(containerPosX, containerPosY,
		               containerWidth, containerHeight);
		
		for (Button b : this.buttons)
			b.draw();
		backButton.draw();
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
