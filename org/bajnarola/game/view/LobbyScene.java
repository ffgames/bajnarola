package org.bajnarola.game.view;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class LobbyScene extends IScene {

	InputBox unameInputBox;
	InputBox lobbyUriInputBox;
	InputBox selectedInputBox;
	
	Button joinButton;
	Button backButton;

	
	int textAreaWidth;
	
	public LobbyScene(Gui guiManager, Image background, 
	                  bg_type backgroundType, Font font) throws SlickException {
		super(guiManager, background, backgroundType);
		
		selectedInputBox = null;
		sceneType = scene_type.SCENE_LOBBY;
		textAreaWidth = guiManager.windowWidth/2;
		
		unameInputBox = new InputBox(textAreaWidth,
		                             font.getLineHeight() + 2, 
		                             guiManager.windowWidth/2,
		                             guiManager.windowHeight/4, 
		                             "Inserisci nome utente",
		                             new Image("res/menu/inputbox.png"), font); 
		
		lobbyUriInputBox = new InputBox(textAreaWidth,
				font.getLineHeight() + 2, 
                guiManager.windowWidth/2,
                guiManager.windowHeight/4 + 40, 
                "Inserisci lobby URI",
                new Image("res/menu/inputbox.png"), font);
		
		
		joinButton = new Button(guiManager.windowWidth/3,
		                        guiManager.windowHeight/9,
		                        guiManager.windowWidth/2,
		                        guiManager.windowHeight/4*2,
		                        new Image("res/menu/optionsInactive.png"),
		                        new Image("res/menu/optionsActive.png"));
		
		backButton = new Button(guiManager.windowWidth/5,
		                         guiManager.windowHeight/11,
		                         guiManager.windowWidth/2,
		                         guiManager.windowHeight/4*3,
		                         new Image("res/menu/exitInactive.png"),
		                         new Image("res/menu/exitActive.png"));
	}

	@Override
	public void leftClick(int x, int y) {
		selectedInputBox = null;

		if (unameInputBox.isClicked(x, y))
			selectedInputBox = unameInputBox;
		else if (lobbyUriInputBox.isClicked(x, y))
			selectedInputBox = lobbyUriInputBox;
		else  if (backButton.isClicked(x, y))
			guiManager.switchScene(scene_type.SCENE_MENU);
		else if (joinButton.isClicked(x, y))
			/* TODO: join first and check if the username is free */
			guiManager.switchScene(scene_type.SCENE_GAME); 
			
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
		guiManager.switchScene(scene_type.SCENE_MENU);
	}

	@Override
	public void backspacePressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		joinButton.isClicked(newx, newy);
		backButton.isClicked(newx, newy);
	}
	
	public void keyPressed(int key, char c) {
		if (selectedInputBox != null) {
			if (key == Input.KEY_BACK) {
				selectedInputBox.delChar();
			} else if (key == Input.KEY_TAB && selectedInputBox.equals(unameInputBox)) {
				selectedInputBox = lobbyUriInputBox;
				selectedInputBox.initialize();
			} else {
				selectedInputBox.putChar(c);
			}
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		unameInputBox.draw(g);
		lobbyUriInputBox.draw(g);
		joinButton.draw();
		backButton.draw();
	}

}
