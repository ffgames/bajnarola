package org.bajnarola.game.view;

import java.net.MalformedURLException;

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
	
	Font font;

	static final String labelUsername = "Insert Username";
	static final String labelLobby = "Insert Lobby Server";
	static final String errorGameStarted = "Too late, game has already started";
	static final String errorUsernameExists = "Your username already exists";
	static final String errorLobby = "Can't connect to the specified lobby";
	static final String errorLobbyNotFound = "Lobby not found";
	static final String errorMalformedLobby = "Malformed lobby URI";
	static final String infoJoining = "Joining, please wait...";
	
	static int labelUsernamePosX, labelLobbyPosX, 
	           labelUsernamePosY, labelLobbyPosY,
	           labelJoinPosY;
	
	int labelJoinPosX;
	String joinMessage = "";
	
	int textAreaWidth;
	
	public static enum JoinStatus {
		LOBBY_OK,
		USER_EXISTS,
		GAME_STARTED,
		LOBBY_NOT_FOUND,
		LOBBY_ERROR
	}
	
	public LobbyScene(Gui guiManager, Image background, 
	                  bg_type backgroundType, Font font) throws SlickException {
		super(guiManager, background, backgroundType);
		
		this.font = font;
		
		sceneType = scene_type.SCENE_LOBBY;
		textAreaWidth = guiManager.windowWidth/2;
		
		unameInputBox = new InputBox(textAreaWidth,
		                             font.getLineHeight() + 2, 
		                             guiManager.windowWidth/2,
		                             guiManager.windowHeight/6, 
		                             "Username",
		                             new Image("res/menu/inputbox.png"), font); 
		
		lobbyUriInputBox = new InputBox(textAreaWidth,
				font.getLineHeight() + 2, 
                guiManager.windowWidth/2,
                guiManager.windowHeight/6*2, 
                "localhost",
                new Image("res/menu/inputbox.png"), font);
		
		
		joinButton = new Button(guiManager.windowWidth/3,
		                        guiManager.windowHeight/9,
		                        guiManager.windowWidth/2,
		                        guiManager.windowHeight/6*4,
		                        new Image("res/menu/joinInactive.png"),
		                        new Image("res/menu/joinActive.png"),
		                        new Image("res/menu/joinDisabled.png"));
		
		backButton = new Button(guiManager.windowWidth/3,
		                         guiManager.windowHeight/9,
		                         guiManager.windowWidth/2,
		                         guiManager.windowHeight/6*5,
		                         new Image("res/menu/backInactive.png"),
		                         new Image("res/menu/backActive.png"));
		
		selectedInputBox = unameInputBox;
		selectedInputBox.selected = true;
		
		labelUsernamePosX = (guiManager.windowWidth/2) - (font.getWidth(labelUsername)/2);
		labelLobbyPosX = (guiManager.windowWidth/2) - (font.getWidth(labelLobby)/2);
		labelUsernamePosY = unameInputBox.hitbox.uly - font.getLineHeight() - 2;
		labelLobbyPosY = lobbyUriInputBox.hitbox.uly - font.getLineHeight() - 2;
		
		labelJoinPosY = guiManager.windowHeight/6*3;
		
		/* TODO: Add "create local lobby" button and correlated feature */
	}

	private void setJoinMessage(String msg) {
		joinMessage = msg;
		labelJoinPosX = (guiManager.windowWidth/2) - (font.getWidth(joinMessage)/2);
	}
	
	private void join() {
		String uname, lobbyURI;
		uname = unameInputBox.getText();
		lobbyURI = lobbyUriInputBox.getText();
		setJoinMessage(infoJoining);
		joinButton.disable();
		
		try {
			guiManager.controller.setGameOptions(uname, lobbyURI);
		} catch (MalformedURLException e) {
			joinButton.enable();
			setJoinMessage(errorMalformedLobby);
		}
	}
	
	@Override
	public void leftClick(int x, int y) {
		selectedInputBox = null;

		if (unameInputBox.isClicked(x, y))
			selectedInputBox = unameInputBox;
		if (lobbyUriInputBox.isClicked(x, y))
			selectedInputBox = lobbyUriInputBox;
		if (backButton.isClicked(x, y))
			guiManager.switchScene(scene_type.SCENE_MENU);
		if (joinButton.isClicked(x, y)) {
			join();
		}
	}

	
	
	public void joinCallback(JoinStatus status) {
		joinButton.enable();

		switch (status) {
		case GAME_STARTED:
			setJoinMessage(errorGameStarted);
			break;
		case USER_EXISTS:
			setJoinMessage(errorUsernameExists);
			break;
		case LOBBY_ERROR:
			setJoinMessage(errorLobby);
			break;
		case LOBBY_NOT_FOUND:
			setJoinMessage(errorLobbyNotFound);
			break;
		case LOBBY_OK:
			joinMessage = "";
			guiManager.switchScene(scene_type.SCENE_GAME);
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
			} else if (key == Input.KEY_TAB ) {
				if (selectedInputBox.equals(unameInputBox)) {
					selectedInputBox = lobbyUriInputBox;
					unameInputBox.selected = false;
				} else {
					selectedInputBox = unameInputBox;
					lobbyUriInputBox.selected = false;
				}
				
				selectedInputBox.initialize();
				selectedInputBox.selected = true;
			} else if (key == Input.KEY_ENTER || key == Input.KEY_NUMPADENTER) {
				join();
				leftRelease(guiManager.windowWidth/2 + 10,
				            guiManager.windowHeight/4*2 + 60);
			} else {
				if (!selectedInputBox.initialized)
					selectedInputBox.initialize();
				/* TODO: escape input for lobbyURI */
				selectedInputBox.putChar(c); 
			}
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		unameInputBox.draw(guiManager);
		lobbyUriInputBox.draw(guiManager);
		joinButton.draw();
		backButton.draw();
		guiManager.drawString(labelUsername, labelUsernamePosX, labelUsernamePosY);
		guiManager.drawString(labelLobby, labelLobbyPosX, labelLobbyPosY);
		guiManager.drawString(joinMessage, labelJoinPosX, labelJoinPosY);
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
	}

	@Override
	public void keyReleased(int key, char c) {
	}

}
