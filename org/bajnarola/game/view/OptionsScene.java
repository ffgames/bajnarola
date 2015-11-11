package org.bajnarola.game.view;

import java.util.ArrayList;
import java.util.List;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

public class OptionsScene extends IScene {

	public scene_type prevScene;
	
	List<Button> buttons;
	Button buttonFullScreen, buttonRes1, buttonRes2, buttonRes3;
	Button backButton;
	Image container;
	float containerWidth, containerHeight;
	float containerPosX, containerPosY;
	
	static final String restartLabel = "Restart game to make changes effective";
	
	public OptionsScene(Gui guiManager, Image background, bg_type backgroundType, List<Music> soundtrack) throws SlickException {
		super(guiManager, background, backgroundType, soundtrack);
		prevScene = null;
		sceneType = scene_type.SCENE_OPTIONS;
		
		buttons = new ArrayList<Button>();
		
		containerWidth = (float)((float)guiManager.windowWidth *(float) 0.7);
		containerHeight = (float)((float)guiManager.windowHeight * (float)0.8);
		containerPosX = (guiManager.windowWidth/2) - containerWidth/2;
		containerPosY = (guiManager.windowHeight/2) - containerHeight/2 - 50;
		
		buttonRes1 = new Button(guiManager.windowWidth/5,
                guiManager.windowHeight/11,
                guiManager.windowWidth/2,
                (int) containerPosY + (int)(containerHeight / 6),
                "800x600", Gui.buttonFont, null, null, null);
		buttons.add(buttonRes1);
		
		buttonRes2 = new Button(guiManager.windowWidth/5,
                guiManager.windowHeight/11,
                guiManager.windowWidth/2,
                (int) containerPosY + (int)(containerHeight / 6 * 2),
                "1280x720", Gui.buttonFont, null, null, null);
		buttons.add(buttonRes2);
		
		if (Display.getDesktopDisplayMode().getHeight() < 720)
			buttonRes2.disable();

		buttonRes3 = new Button(guiManager.windowWidth/5,
                guiManager.windowHeight/11,
                guiManager.windowWidth/2,
                (int) containerPosY + (int)(containerHeight / 6 * 3),
                "1920x1080", Gui.buttonFont, null, null, null);
		buttons.add(buttonRes3);
		
		if (Display.getDesktopDisplayMode().getHeight() < 1080)
			buttonRes3.disable();
		
		buttonFullScreen = new Button(guiManager.windowWidth/5,
                guiManager.windowHeight/11,
                guiManager.windowWidth/2,
                (int) containerPosY + (int)(containerHeight / 6 * 4),
                "Fullscreen", Gui.buttonFont, null, null, null);
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
                "Back");
	}

	@Override
	public void leftClick(int x, int y) {
		Button b = null;
		//AppGameContainer gc = (AppGameContainer) guiManager.container;
		int resx = 0, resy = 0;
		boolean fullscreen = false;
		
		for (int i = 0; i < this.buttons.size(); i++) {
			b = this.buttons.get(i);
			
			if (b.hits(x, y) && b.isEnabled()) {
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
						resx = 0;
						resy = 0;
						fullscreen = true;
						break;
					}
				}
				for (Button b2: this.buttons) {
					if (!b2.equals(b))
						b2.deactivate();
				}
				guiManager.setResolutionOptions(resx, resy, fullscreen);
				break;
			}
		}
		
		if (backButton.isClicked(x, y))
			guiManager.switchScene(prevScene);
	}
	
	@Override
	public void rightClick(int x, int y) {
	}

	@Override
	public void wheelMoved(boolean up) {
	}

	@Override
	public void enterPressed() {
	}

	@Override
	public void escPressed() {
		guiManager.switchScene(prevScene);
	}

	@Override
	public void backspacePressed() {
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
		
		guiManager.drawString(restartLabel, 
		                      guiManager.windowWidth/2 -  Gui.mainFont.getWidth(restartLabel)/2,
		                      (int) containerPosY + (int)(containerHeight/ 6 * 5) - Gui.mainFont.getHeight(restartLabel), 
		                      Button.inactiveColor);
		
		for (Button b : this.buttons)
			b.draw();
		backButton.draw();
	}

	@Override
	public void leftRelease(int x, int y) {
	}

	@Override
	public void rightRelease(int x, int y) {
	}

	@Override
	public void keyPressed(int key, char c) {
	}

	@Override
	public void keyReleased(int key, char c) {
	}

}
