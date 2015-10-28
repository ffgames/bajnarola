package org.bajnarola.game.view;

import org.bajnarola.game.controller.ViewController;
import org.bajnarola.game.controller.ViewUpdate;
import org.bajnarola.game.view.LobbyScene.UnlockCause;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;

public class Gui extends BasicGame implements InputProviderListener {
	
	static final String GAMENAME = "Bajnarola";
	
	private InputProvider provider;
	private Input rawInput;
	
	public int windowWidth, windowHeight;
	
	Animator animator;
	
	public enum bg_type {
		BG_TILED,
		BG_CENTERED
	};
	
	public enum scene_type {
		SCENE_GAME,
		SCENE_MENU,
		SCENE_PAUSE,
		SCENE_OPTIONS,
		SCENE_LOBBY,
		SCENE_ENDGAME
	}
	
	public ViewController controller;
	
	private Command rotateComm = new BasicCommand("rotate");
	private Command backComm = new BasicCommand("back");
	private Command escComm = new BasicCommand("esc");
	private Command enterComm = new BasicCommand("enter");
	
	private String message = "Press something..";
	
	private IScene currentScene;
	private MenuScene menuScene;
	private GameScene gameScene;
	private PauseScene pauseScene;
	private OptionsScene optionsScene;
	private EndgameScene endgameScene;
	private LobbyScene lobbyScene;
	
	public ViewUpdate currentUpdate;
	
	private GameContainer container;
	
	public Gui(ViewController controller){
		super(GAMENAME);
		this.controller = controller;
	}
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		 provider = new InputProvider(gc.getInput());
		 provider.addListener(this);
		 
		 provider.bindCommand(new KeyControl(Input.KEY_SPACE), rotateComm);
		 provider.bindCommand(new KeyControl(Input.KEY_BACK), backComm);
		 provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), escComm);
		 provider.bindCommand(new KeyControl(Input.KEY_ENTER), enterComm);
		 
		 rawInput = new Input(gc.getScreenHeight());
		 
		 animator = new Animator();
		 
		 windowHeight = gc.getHeight();
		 windowWidth = gc.getWidth();
		 
		 menuScene = new MenuScene(this, new Image("res/backgrounds/Medieval_village.jpg"), bg_type.BG_CENTERED);
		 
		 Image boardBackground;
		 if(gc.getHeight() > 800){
			 boardBackground = new Image("res/backgrounds/Craggy_Rock_1024.jpg");
		 } else if(gc.getHeight() > 500){
			 boardBackground = new Image("res/backgrounds/Craggy_Rock_512.jpg");
		 } else
			 boardBackground = new Image("res/backgrounds/Craggy_Rock_256.jpg");
		 gameScene = new GameScene(this, boardBackground, bg_type.BG_TILED);

		 pauseScene = new PauseScene(this, new Image(windowWidth, windowHeight, Image.FILTER_LINEAR), bg_type.BG_CENTERED);

		 optionsScene = new OptionsScene(this, null, null);
		 
		 endgameScene = new EndgameScene(this, new Image(windowWidth, windowHeight, Image.FILTER_LINEAR), bg_type.BG_CENTERED);
		 
		 //TODO: wooden table should be lobby screen background
		 lobbyScene = new LobbyScene(this, menuScene.background, menuScene.backgroundType, gc.getGraphics().getFont());
		 
		 currentScene = menuScene;
		 
		 currentUpdate = null;
		 
		 container = gc;
	}
	
	public void drawBackground(Image background, bg_type backgroundType){
		switch(backgroundType){
			case BG_TILED:
				drawBgTiled(background, background.getWidth(), background.getHeight());
				break;
			case BG_CENTERED:
				drawBgCentered(background, background.getWidth(), background.getHeight());
				break;
			default:
				break;
		}
	}
	
	private void drawBgTiled(Image background, int backgroundWidth, int backgroundHeight){
		float tx, ty;
		int sw;
		int sh = windowHeight;
		ty = 0;
		while(sh > 0){
			sw = windowWidth;
			tx = 0;
			while(sw > 0){
				background.draw(tx, ty);
				tx += backgroundWidth;
				sw -= backgroundWidth;
			}
			ty += backgroundHeight;
			sh -= backgroundHeight;
		}
	}
	
	private void drawBgCentered(Image background, int backgroundWidth, int backgroundHeight){
		float tx, ty;
		tx = (windowWidth > backgroundWidth ? (windowWidth - backgroundWidth) / 2 : 0);
		ty = (windowHeight > backgroundHeight ? (windowHeight - backgroundHeight) / 2 : 0);
		background.draw(tx, ty, (windowWidth < backgroundWidth ? windowWidth : backgroundWidth), (windowHeight < backgroundHeight ? windowHeight : backgroundHeight));
	}
	
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		currentScene.render(gc, g);
		
		if(currentScene.sceneType == scene_type.SCENE_GAME && ((gc.isUpdatingOnlyWhenVisible() && !gc.isPaused()) || !gc.isUpdatingOnlyWhenVisible()))
			animator.step();
		
		g.drawString(message, 10, 20);
		g.drawString(animator.printStep(), 10, 40);
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		if(currentUpdate == null && controller != null && currentScene.sceneType == scene_type.SCENE_GAME){
			if((currentUpdate = controller.dequeueViewUpdate()) != null){
				if(currentUpdate.points == null && currentUpdate.placedTile == null){
					updateEndgameScene();
				} else {
					gameScene.placeGraphicalTile(currentUpdate.placedTile, currentUpdate.placedTile.getX()+";"+currentUpdate.placedTile.getY());
					animator.enableTilePlacement();
					if(currentUpdate.placedTile.hasMeeple())
						animator.enableMeeplePlacement();
					currentUpdate.placedTile = null;
				}
			}
		}
		if(currentUpdate != null){
			if(!animator.isTilePlacementOn() && !animator.isMeeplePlacementOn() && currentUpdate.points != null && !currentUpdate.points.isEmpty()){
				gameScene.tilePlaced();
				gameScene.meeplePlaced();
				animator.enableLandscapeGlow();
				if(gameScene.setCurrentLandscape(currentUpdate.points))
					animator.enableMeepleRemoval();
				currentUpdate.points = null;
			}
			if(animator.automaticAnimationsEnded()){
				gameScene.landscapesSet();
				gameScene.meeplesRemoved();
				currentUpdate = null;
			}
		}
	}

	private void updateEndgameScene(){
		//TODO: get endgame stats from controller and set engame scene
	}
	
	@Override
	public void controlPressed(Command command) {
		if(command.equals(rotateComm)){
			if(rawInput.isKeyDown(Input.KEY_LSHIFT) || rawInput.isKeyDown(Input.KEY_RSHIFT))
				currentScene.wheelMoved(true);
			else
				currentScene.wheelMoved(false);
		} else if(command.equals(backComm)){
			currentScene.backspacePressed();
		} else if(command.equals(escComm)){
			currentScene.escPressed();
		} else if(command.equals(enterComm)){
			currentScene.enterPressed();
		}
	}

	@Override
	public void keyPressed(int key, char c) {
		if(currentScene.sceneType == scene_type.SCENE_LOBBY)
			lobbyScene.keyPressed(key, c);
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		super.mouseClicked(button, x, y, clickCount);
		message = "mouse ";
		if(button == 0){
			currentScene.leftClick(x, y);
			message += "left";
		}
		else if(button == 1){
			currentScene.rightClick(x, y);
			message += "right";
		}
		message += " clicked x: "+x+", y: "+y;
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		super.mouseReleased(button, x, y);
		message = "mouse ";
		if(button == 0){
			currentScene.leftRelease(x, y);
			message += "left";
		}
		else if(button == 1){
			currentScene.rightRelease(x, y);
			message += "right";
		}
		message += " released x: "+x+", y: "+y;
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		super.mouseWheelMoved(change);
		if(change > 0)
			currentScene.wheelMoved(true);
		else if(change < 0)
			currentScene.wheelMoved(false);
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy){
		currentScene.mouseMoved(oldx, oldy, newx, newy);
	}
	
	private void screencapBackground(Image targetBackground){
		container.getGraphics().copyArea(targetBackground, 0, 0);
		targetBackground.setColor(Image.BOTTOM_LEFT, 0.5f, 0.5f, 0.5f);
		targetBackground.setColor(Image.BOTTOM_RIGHT, 0.5f, 0.5f, 0.5f);
		targetBackground.setColor(Image.TOP_LEFT, 0.5f, 0.5f, 0.5f);
		targetBackground.setColor(Image.TOP_RIGHT, 0.5f, 0.5f, 0.5f);
	}
	
	public void switchScene(scene_type newScene){
		switch(newScene){
			case SCENE_PAUSE:
				if(currentScene.equals(gameScene))
					screencapBackground(pauseScene.background);
				currentScene = pauseScene;
				break;
			case SCENE_GAME:
				currentScene = gameScene;
				break;
			case SCENE_MENU:
				currentScene = menuScene;
				break;
			case SCENE_OPTIONS:
				optionsScene.background = currentScene.background;
				optionsScene.backgroundType = currentScene.backgroundType;
				optionsScene.prevScene = currentScene.sceneType;
				currentScene = optionsScene;
				break;
			case SCENE_LOBBY:
				currentScene = lobbyScene;
				break;
			case SCENE_ENDGAME:
				screencapBackground(endgameScene.background);
				currentScene = endgameScene;
				break;
		}
	}
	
	@Override
	public void controlReleased(Command command) {
		
	}

	public void unlockLobbyScene(UnlockCause cause) {
		lobbyScene.unlock(cause);
	}
	
	public void exit(){
		container.exit();
	}
}
