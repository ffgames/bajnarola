package org.bajnarola.game.view;

import java.util.List;

import org.bajnarola.game.controller.ViewController;
import org.bajnarola.game.controller.ViewUpdate;
import org.bajnarola.game.model.Tile;
import org.bajnarola.game.view.LobbyScene.JoinStatus;
import org.bajnarola.game.view.RelativeSizes.Resolutions;
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
	
	private IScene currentScene;
	private MenuScene menuScene;
	private GameScene gameScene;
	private PauseScene pauseScene;
	private OptionsScene optionsScene;
	private EndgameScene endgameScene;
	private LobbyScene lobbyScene;
	
	public ViewUpdate currentUpdate;
	
	private GameContainer container;
	
	private boolean myTurn = false, landscapeGlowOn = false, meepleRemovalOn = false, showScoreOn = false;
	private List<String> holes;
	private Tile newTile;
	
	public Gui(ViewController controller){
		super(GAMENAME);
		this.controller = controller;
	}
	
	private int playerId = -1;
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		 provider = new InputProvider(gc.getInput());
		 provider.addListener(this);
		 
		 provider.bindCommand(new KeyControl(Input.KEY_SPACE), rotateComm);
		 provider.bindCommand(new KeyControl(Input.KEY_BACK), backComm);
		 provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), escComm);
		 provider.bindCommand(new KeyControl(Input.KEY_ENTER), enterComm);
		 
		 //TODO: if fixed resolution is set through options avoid using fullscreen here
		 RelativeSizes.getInstance().setResolution(Resolutions.R_FULLSCREEN, gc.getWidth(), gc.getHeight());
		 
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
		 gc.setShowFPS(false);
		 gc.setTargetFrameRate(30);
		 gc.setUpdateOnlyWhenVisible(false);
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
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		if(myTurn){
			myTurn = false;
			gameScene.beginTurn(holes, newTile);
		}
		if(playerId > -1){
			gameScene.initPlayerMeeple(playerId);
			playerId = -1;
		}
		if(currentUpdate == null && controller != null && currentScene.sceneType == scene_type.SCENE_GAME){
			if((currentUpdate = controller.dequeueViewUpdate()) != null){
				if(currentUpdate.points == null && currentUpdate.placedTile == null){
					currentUpdate = null;
					updateEndgameScene();
					return; //XXX: right??
				} else {
					animator.enableTilePlacement();
					if(gameScene.placeGraphicalTile(currentUpdate.placedTile, currentUpdate.placedTile.getX()+";"+currentUpdate.placedTile.getY()))
						animator.enableMeeplePlacement();
					currentUpdate.placedTile = null;
				}
			}
		}
		if(currentUpdate != null && !animator.isTilePlacementOn() && !animator.isMeeplePlacementOn()){
			if(currentUpdate.points != null){
				gameScene.tilePlaced();
				gameScene.meeplePlaced();
				if(!currentUpdate.points.isEmpty()){
					animator.enableLandscapeGlow();
					landscapeGlowOn = true;
					if(gameScene.setCurrentLandscape(currentUpdate.points)){
						animator.enableMeepleRemoval();
						meepleRemovalOn = true;
					}
				}
				currentUpdate.points = null;
			}
			if(showScoreOn && !animator.isShowScoreOn()){
				gameScene.scoreDrawed();
				showScoreOn = false;
			}
			if(landscapeGlowOn && !animator.isLandscapeGlowOn()){
				gameScene.landscapesSet();
				landscapeGlowOn = false;
			}
			if(meepleRemovalOn && !animator.isMeepleRemovalOn()){
				gameScene.meeplesRemoved(controller.getMeeplesInHand());
				meepleRemovalOn = false;
			}
			

			if(!showScoreOn && currentUpdate.scores != null && !currentUpdate.scores.isEmpty()){
				gameScene.drawScoreUpdate(currentUpdate.scores.remove(0));
				animator.enableShowScore();
				showScoreOn = true;
			}
			
			if(animator.automaticAnimationsEnded())
				currentUpdate = null;
		}
	}

	public void viewPlayTurn(List<String> holes, Tile newTile){
		myTurn = true;
		this.holes = holes;
		this.newTile = newTile;
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
		if(button == 0){
			currentScene.leftClick(x, y);
		}
		else if(button == 1){
			currentScene.rightClick(x, y);
		}
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		super.mouseReleased(button, x, y);
		if(button == 0){
			currentScene.leftRelease(x, y);
		}
		else if(button == 1){
			currentScene.rightRelease(x, y);
		}
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
	
	public void setPlayerMeepleColor(int playerId){
		this.playerId = playerId;
	}
	
	@Override
	public void controlReleased(Command command) {
		
	}

	public void joinSignalLobbyScene(JoinStatus status) {
		lobbyScene.joinCallback(status);
	}
	
	public void exit(){
		container.exit();
	}
}
