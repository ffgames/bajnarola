package org.bajnarola.game.view;

import java.util.ArrayList;
import java.util.List;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class OptionsScene extends IScene {

	public scene_type prevScene;
	
	List<Button> buttons;
	Button buttonFullScreen, buttonRes1, buttonRes2, buttonRes3;
	
	Image container;
	float containerWidth, containerHeight;
	float containerPosX, containerPosY;
	
	public OptionsScene(Gui guiManager, Image background, bg_type backgroundType) throws SlickException {
		super(guiManager, background, backgroundType);
		prevScene = null;
		
		buttons = new ArrayList();
		
		containerWidth = (float)((float)guiManager.windowWidth *(float) 0.7);
		containerHeight = (float)((float)guiManager.windowHeight * (float)0.8);
		containerPosX = (guiManager.windowWidth/2) - containerWidth/2;
		containerPosY = (guiManager.windowHeight/2) - containerHeight/2;
		
		
		buttonRes1 = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                (int) containerPosY + (int)(containerHeight / 5),
                new Image("res/menu/res1Inactive.png"),
                new Image("res/menu/res1Active.png"));
		buttons.add(buttonRes1);
		
		buttonRes2 = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                (int) containerPosY + (int)(containerHeight / 5 * 2),
                new Image("res/menu/res2Inactive.png"),
                new Image("res/menu/res2Active.png"));
		buttons.add(buttonRes2);

		
		buttonRes3 = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                (int) containerPosY + (int)(containerHeight / 5 * 3),
                new Image("res/menu/res3Inactive.png"),
                new Image("res/menu/res3Active.png"));
		buttons.add(buttonRes3);

		
		buttonFullScreen = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                (int) containerPosY + (int)(containerHeight / 5 * 4),
                new Image("res/menu/fullscreenInactive.png"),
                new Image("res/menu/fullscreenActive.png"));
		buttons.add(buttonFullScreen);

		container = new Image("res/menu/optionsContainer.png");
	}

	@Override
	public void leftClick(int x, int y) {
		for (Button b : this.buttons) {
			if (b.hits(x, y)) {
				b.activate();
				for (Button b2: this.buttons) {
					if (!b2.equals(b))
						b2.deactivate();
				}
				break;
			}
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
		
		for (Button b : this.buttons)
			b.draw();
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
