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
	
	public static enum UnlockCause {
		userOk,
		userExists,
		gameStarted,
		lobbyError
	}
	
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
		                             "Username",
		                             new Image("res/menu/inputbox.png"), font); 
		
		lobbyUriInputBox = new InputBox(textAreaWidth,
				font.getLineHeight() + 2, 
                guiManager.windowWidth/2,
                guiManager.windowHeight/4 + 40, 
                "localhost",
                new Image("res/menu/inputbox.png"), font);
		
		
		joinButton = new Button(guiManager.windowWidth/3,
		                        guiManager.windowHeight/9,
		                        guiManager.windowWidth/2,
		                        guiManager.windowHeight/4*2+50,
		                        new Image("res/menu/joinInactive.png"),
		                        new Image("res/menu/joinActive.png"),
		                        new Image("res/menu/joinDisabled.png"));
		
		backButton = new Button(guiManager.windowWidth/3,
		                         guiManager.windowHeight/9,
		                         guiManager.windowWidth/2,
		                         guiManager.windowHeight/4*3,
		                         new Image("res/menu/backInactive.png"),
		                         new Image("res/menu/backActive.png"));
		
		/* TODO: Add "create local lobby" button and correlated feature */
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
		else if (joinButton.isClicked(x, y)) {
			String uname, lobbyURI;
			uname = unameInputBox.getText();
			lobbyURI = lobbyUriInputBox.getText();
			System.out.println("Joining...");
			joinButton.disable();
			
			/* TODO: check strings, join and check if the username is free */
			guiManager.controller.setGameOptions(uname, lobbyURI);
		}
	}

	
	public void unlock(UnlockCause cause) {
		joinButton.enable();

		switch (cause) {
		case gameStarted:
			System.out.println("Game already started: lobby full or timed out");
			break;
		case userExists:
			System.out.println("User exists");
			break;
		case userOk:
			guiManager.switchScene(scene_type.SCENE_GAME);
			break;
		case lobbyError:
			System.out.println("Can't connect to the specified lobby");
			break;
		default:
			System.err.println("grrrrrr...");
		}
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
			} else if (key != Input.KEY_TAB) {
				selectedInputBox.putChar(c); /* TODO: escape input for lobbyURI */
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

	@Override
	public void leftRelease(int x, int y) {
		if (joinButton.isClicked(x, y))
			joinButton.deactivate();
		if (backButton.isClicked(x, y))
			backButton.deactivate();
	}

	@Override
	public void rightRelease(int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
