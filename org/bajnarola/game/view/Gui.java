package org.bajnarola.game.view;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bajnarola.game.GameOptions.LobbyOptions;
import org.bajnarola.game.GameOptions.ViewOptions;
import org.bajnarola.game.controller.ViewController;
import org.bajnarola.game.controller.ViewUpdate;
import org.bajnarola.game.model.Tile;
import org.bajnarola.game.view.LobbyScene.JoinStatus;
import org.bajnarola.game.view.RelativeSizes.Resolutions;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;

public class Gui extends BasicGame implements InputProviderListener {
	
	static final String GAMENAME = "Bajnarola";
	static final String MUSIC_EXT = ".ogg";
	
	static final float MAX_AUDIO_VOLUME = 1.0f;
	
	static final int MENU_SONG_COUNT = 2;
	static final int GAME_SONG_COUNT = 3;
	static final int PAUSE_SONG_COUNT = 0;
	static final int OPTIONS_SONG_COUNT = 0;
	static final int LOBBY_SONG_COUNT = 0;
	static final int ENDGAME_SONG_COUNT = 0;
	
	private InputProvider provider;
	private Input rawInput;
	
	public int windowWidth, windowHeight;
	
	Animator animator;
	
	public enum bg_type {
		BG_TILED,
		BG_CENTERED,
		BG_STRETCHED,
		BG_CROPPED
	};
	
	public enum scene_type {
		SCENE_GAME,
		SCENE_MENU,
		SCENE_PAUSE,
		SCENE_OPTIONS,
		SCENE_LOBBY,
		SCENE_ENDGAME,
		SCENE_SPLASH
	}
	
	public ViewController controller;
	
	private Command rotateComm = new BasicCommand("rotate");
	private Command backComm = new BasicCommand("back");
	private Command escComm = new BasicCommand("esc");
	private Command enterComm = new BasicCommand("enter");
	
	public IScene currentScene = null;
	private MenuScene menuScene;
	public GameScene gameScene;
	private PauseScene pauseScene;
	private OptionsScene optionsScene;
	private EndgameScene endgameScene;
	private LobbyScene lobbyScene;
	private SplashScene splashScene;
	
	public ViewUpdate currentUpdate;
	
	public GameContainer container;
	
	public java.awt.Font trueTypeFont;
	public static Color defaultTextColor;

	public static Image buttonActiveBg, buttonInactiveBg, buttonDisabledBg;
	public static TrueTypeFont buttonFont, mainFont;
	
	private boolean myTurn, landscapeGlowOn, meepleRemovalOn, showScoreOn, notificationOn;
	private List<String> holes;
	private Tile newTile;
	private List<String> meeplesToRemove;
	private List<String> leavingPlayers;
	
	private Music currentSong = null;
	private IScene soundtrackScene;
	
	private boolean soundOn;
	private boolean initialized, resourcesSet;
	
	public Gui(ViewController controller){
		super(GAMENAME);
		this.controller = controller;
		initialized = resourcesSet = false;
		initInternal();
	}
	
	private int playerId = -1;
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		container = gc;
		toggleSound(controller.getSoundOnOption());

		RelativeSizes.getInstance().setResolution(Resolutions.R_FULLSCREEN, gc.getWidth(), gc.getHeight());
		
		container.setShowFPS(false);
		container.setTargetFrameRate(60);
		container.setUpdateOnlyWhenVisible(false);
		
		windowHeight = container.getHeight();
		windowWidth = container.getWidth();
		
		splashScene = new SplashScene(this, new Image("res/misc/splash.png"), bg_type.BG_CENTERED, null);
		currentScene = splashScene;
		initialized = true;
	}
	
	private void initResources() throws SlickException {
		buttonActiveBg = new Image("res/menu/buttonActive.png");
		buttonInactiveBg = new Image("res/menu/buttonInactive.png");
		buttonDisabledBg = new Image("res/menu/buttonDisabled.png");
		
		defaultTextColor = new Color(0xE1DCD1);
		try {
			trueTypeFont = java.awt.Font.createFont(Font.TRUETYPE_FONT, new File("res/font/font.ttf"));
		} catch (FontFormatException | IOException e) {
			throw new SlickException(e.getMessage());
		}

		mainFont = new TrueTypeFont(trueTypeFont.deriveFont(RelativeSizes.getInstance().mainFontSize()), true);
		buttonFont = new TrueTypeFont(trueTypeFont.deriveFont(RelativeSizes.getInstance().buttonsFontSize()), true);
		
		rawInput = new Input(container.getScreenHeight());
		
		provider = new InputProvider(container.getInput());
		provider.addListener(this);

		provider.bindCommand(new KeyControl(Input.KEY_SPACE), rotateComm);
		provider.bindCommand(new KeyControl(Input.KEY_BACK), backComm);
		provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), escComm);
		provider.bindCommand(new KeyControl(Input.KEY_ENTER), enterComm);


		animator = new Animator();

		menuScene = new MenuScene(this, new Image("res/backgrounds/Medieval_village.jpg"), bg_type.BG_CROPPED, genSountrack("menu", MENU_SONG_COUNT));
		
		Image boardBackground;
		if(container.getHeight() > 800){
			boardBackground = new Image("res/backgrounds/Craggy_Rock_1024.jpg");
		} else if(container.getHeight() > 500){
			boardBackground = new Image("res/backgrounds/Craggy_Rock_512.jpg");
		} else
			boardBackground = new Image("res/backgrounds/Craggy_Rock_256.jpg");

		gameScene = new GameScene(this, boardBackground, bg_type.BG_TILED, genSountrack("game", GAME_SONG_COUNT), controller.getScores());

		pauseScene = new PauseScene(this, new Image(windowWidth, windowHeight, Image.FILTER_LINEAR), bg_type.BG_STRETCHED, genSountrack("pause", PAUSE_SONG_COUNT));

		optionsScene = new OptionsScene(this, null, null, genSountrack("options", OPTIONS_SONG_COUNT));

		endgameScene = new EndgameScene(this, new Image(windowWidth, windowHeight, Image.FILTER_LINEAR), bg_type.BG_STRETCHED, genSountrack("endgame", ENDGAME_SONG_COUNT));

		lobbyScene = new LobbyScene(this, menuScene.background, menuScene.backgroundType, genSountrack("lobby", LOBBY_SONG_COUNT), mainFont);

		currentUpdate = null;
		meeplesToRemove = new ArrayList<String>();
		leavingPlayers = new ArrayList<String>();
		
		//gc.setMouseGrabbed(true);
		container.setMouseCursor("res/misc/pointer.gif", 6, 5);
		
		resourcesSet = true;
		
		if (currentScene == null || currentScene.sceneType == scene_type.SCENE_SPLASH){
			switchScene(scene_type.SCENE_MENU);
		}
		else 
			switchScene(currentScene.sceneType);
		
		Thread musicThread = (new Thread() {
			public void run() {
				
				try {
					loadSountrack(menuScene.soundtrack, "menu", MENU_SONG_COUNT);
					loadSountrack(gameScene.soundtrack, "game", GAME_SONG_COUNT);
					loadSountrack(pauseScene.soundtrack, "pause", PAUSE_SONG_COUNT);
					loadSountrack(optionsScene.soundtrack, "options", OPTIONS_SONG_COUNT);
					loadSountrack(lobbyScene.soundtrack, "lobby", LOBBY_SONG_COUNT);
					loadSountrack(endgameScene.soundtrack, "endgame", ENDGAME_SONG_COUNT);
				} catch (SlickException e) {
					e.printStackTrace();
				}
			}
		});
		
		musicThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
	        public void uncaughtException(Thread t, Throwable e) {
	            e.printStackTrace();
	        }
	    });
		
		musicThread.start();
		
	}
	
	private void initInternal(){
		myTurn = false;
		landscapeGlowOn = false;
		meepleRemovalOn = false;
		showScoreOn = false;
		notificationOn = false;
		currentUpdate = null;
		holes = null;
		newTile = null;
	}
	
	public void removeDeadMeeples(List<String> coords, String playername){
		meeplesToRemove.addAll(coords);
		leavingPlayers.add(playername);
	}
	
	private void loadSountrack(List<Music> soundtrack, String prefix, int count) throws SlickException {
		
		if (count > 1) {
			for(int i = 1; i < count; i++)
				soundtrack.add(new Music("res/music/"+prefix+i+MUSIC_EXT));
		}
	}
	
	private List<Music> genSountrack(String prefix, int count) throws SlickException{
		if(count <= 0)
			return null;
		List<Music> soundtrack = new ArrayList<Music>();
		
		soundtrack.add(new Music("res/music/"+prefix+0+MUSIC_EXT));
		
		return soundtrack;
	}
	
	public boolean getSoundOn(){
		return soundOn;
	}
	
	public void toggleSound(boolean state){
		soundOn = state;
		container.setMusicOn(state);
		container.setSoundOn(state);
		if(state){
			container.setMusicVolume(MAX_AUDIO_VOLUME);
			container.setSoundVolume(MAX_AUDIO_VOLUME);
		} else {
			container.setMusicVolume(0);
			container.setSoundVolume(0);
		}
		if(state && currentSong != null)
			currentSong.play();
		controller.setSoundOnOption(state);
	}
	
	public void drawBackground(Image background, bg_type backgroundType){
		switch(backgroundType){
			case BG_TILED:
				drawBgTiled(background, background.getWidth(), background.getHeight());
				break;
			case BG_CENTERED:
				drawBgCentered(background, background.getWidth(), background.getHeight());
				break;
			case BG_STRETCHED:
				drawBgStretched(background, background.getWidth(), background.getHeight());
			case BG_CROPPED:
				drawBgCropped(background, background.getWidth(), background.getHeight());
		}
	}
	
	private void drawBgCropped(Image background, int backgroundWidth, int backgroundHeight){
		float tx, ty;
		float twid, thei;
		if(backgroundWidth < windowWidth){
			if(backgroundHeight < windowHeight){
				if((windowHeight - backgroundHeight) > (windowWidth - backgroundWidth)){
					thei = windowHeight;
					twid = (float)backgroundWidth * ((float)windowHeight/ (float)backgroundHeight);
					ty = 0;
					tx = (windowWidth - twid) / 2;
				} else {
					thei = (float)backgroundHeight * ((float)windowWidth / (float)backgroundWidth);
					twid = windowWidth;
					ty = (windowHeight - thei) / 2;
					tx = 0;
				}
			} else { //backgroundHeight >= windowHeight
				thei = (float)backgroundHeight * ((float)windowWidth / (float)backgroundWidth);
				twid = windowWidth;
				ty = (windowHeight - thei) / 2;
				tx = 0;
			} 
		} else { //backgroundWidth >= windowWidth
			if(backgroundHeight < windowHeight){
				thei = windowHeight;
				twid = (float)backgroundWidth * ((float)windowHeight / (float)backgroundHeight);
				ty = 0;
				tx = (windowWidth - twid) / 2;
			} else { //backgroundHeight >= windowHeight
				if((backgroundHeight - windowHeight) < (backgroundWidth - windowWidth)){
					thei = windowHeight;
					twid = (float)backgroundWidth * ((float)windowHeight / (float)backgroundHeight);
					ty = 0;
					tx = (windowWidth - twid) / 2;
				} else {
					thei = (float)backgroundHeight * ((float)windowWidth / (float)backgroundWidth);
					twid = windowWidth;
					ty = (windowHeight - thei) / 2;
					tx = 0;
				}
			}
		}
		background.draw(tx, ty, twid, thei);
	}
	
	private void drawBgStretched(Image background, int backgroundWidth, int backgroundHeight){
		background.draw(0, 0, windowWidth, windowHeight);
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
		if(initialized && !resourcesSet){
			initResources();
		}
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
				if(currentUpdate.points == null && currentUpdate.placedTile == null && currentUpdate.scores == null && animator.allAnimationsEnded()){ //end game
					currentUpdate = null;
					updateEndgameScene();
				} else if(currentUpdate.placedTile != null){	//regular turn
					gameScene.setScores(controller.getScores());
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
				gameScene.drawScoreUpdate(currentUpdate.scores.remove(0), controller.getScores());
				animator.enableShowScore();
				showScoreOn = true;
			}
			
			if(animator.automaticAnimationsEnded())
				currentUpdate = null;
		}
		if(currentScene.sceneType == scene_type.SCENE_GAME && !animator.isMeepleRemovalOn() && (!meeplesToRemove.isEmpty() || !leavingPlayers.isEmpty())){
			gameScene.removeMeeples(meeplesToRemove);
			animator.enableMeepleRemoval();
			meeplesToRemove.clear();
			meepleRemovalOn = true;
			
			String text = leavingPlayers.remove(0);
			if(leavingPlayers.size() == 1){
				text += " and "+leavingPlayers.remove(0);
			} else for(String pl : leavingPlayers){
					text += ", "+pl;
			}
			text += " left the game";
			leavingPlayers.clear();
			gameScene.notify(text);
			animator.enableNotification();
			notificationOn = true;
			
			gameScene.setScores(controller.getScores());
		}
		if(notificationOn && !animator.isNotificationOn()){
			gameScene.notified();
			notificationOn = false;
		}
	}

	public void viewPlayTurn(List<String> holes, Tile newTile){
		myTurn = true;
		this.holes = holes;
		this.newTile = newTile;
	}
	
	private void updateEndgameScene(){
		endgameScene.setWinner(controller.amIWinner());
		switch(controller.getEndCause()){
			case deckEmpty:
				endgameScene.setCause("Last Tile has been placed");
				break;
			case lastPlayer:
				endgameScene.setCause("All other players left the game");
				break;
			case notEnded:
				//XXX: we should never get here
				break;
		}
		Map<String, Integer> finalScores = controller.getFinalScores();
		List<String> scoreStrings = new ArrayList<String>();
		
		for(String s : finalScores.keySet()){
			scoreStrings.add(s+": "+finalScores.get(s));
		}
		endgameScene.setScores(scoreStrings);
		
		switchScene(scene_type.SCENE_ENDGAME);
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
		if(currentScene != null)
			currentScene.keyPressed(key, c);
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
				if(currentScene != null && (currentScene.sceneType == scene_type.SCENE_PAUSE || currentScene.sceneType == scene_type.SCENE_ENDGAME))
					controller.requestReinit();
				currentScene = menuScene;
				break;
			case SCENE_OPTIONS:
				optionsScene.background = currentScene.background;
				optionsScene.backgroundType = currentScene.backgroundType;
				if (currentScene.sceneType != scene_type.SCENE_OPTIONS) {
					optionsScene.prevScene = currentScene.sceneType;
				} else {
					optionsScene.prevScene = ((OptionsScene)currentScene).prevScene;
				}
				currentScene = optionsScene;
				break;
			case SCENE_LOBBY:
				currentScene = lobbyScene;
				break;
			case SCENE_ENDGAME:
				screencapBackground(endgameScene.background);
				currentScene = endgameScene;
				break;
			default:
				break;
		}
		if(currentScene.soundtrack != null && !currentScene.soundtrack.isEmpty()){
			soundtrackScene = currentScene;
			playMusic(currentScene.soundtrack.get(currentScene.currentSong));
		}
	}
	
	public void playMusic(Music song){
		if(currentSong != song || !currentSong.playing()){
			currentSong = song;
			song.addListener(new MusicListener() {
				@Override
				public void musicSwapped(Music music, Music newMusic) {}
				
				@Override
				public void musicEnded(Music music) {
					if(soundtrackScene.soundtrack != null){
						soundtrackScene.currentSong = (soundtrackScene.currentSong + 1) % soundtrackScene.soundtrack.size();
						playMusic(soundtrackScene.soundtrack.get(soundtrackScene.currentSong));
					} else {
						playMusic(currentSong);
					}
				}
			});
			if(soundOn)
				song.play();
		}
	}
	
	public void setPlayerMeepleColor(int playerId){
		this.playerId = playerId;
	}
	
	public void setScores(List<String> scores){
		gameScene.setScores(scores);
	}
	
	@Override
	public void controlReleased(Command command) {
		
	}

	@Override
	public void keyReleased(int key, char c) {
		if(currentScene != null)
			currentScene.keyReleased(key, c);
	}
	
	public void joinSignalLobbyScene(JoinStatus status) {
		lobbyScene.joinCallback(status);
	}
	
	public void setResolutionOptions(int resx, int resy, boolean fullscreen) {
		this.controller.setViewOptions(resx, resy, fullscreen);
	}
	
	public ViewOptions getResolutionOptions() {
		return this.controller.getViewOptions();
	}
	
	public void drawString(String str, int x, int y, Color color, float alpha){
		Color nc = new Color(color);
		nc.a = alpha;
		mainFont.drawString(x, y, str, nc);
	}
	
	public void drawString(String str, int x, int y){
		drawString(str, x, y, defaultTextColor, 1f);
	}
	
	public void drawString(String str, int x, int y, Color color){
		drawString(str, x, y, color, 1f);
	}
	
	public void drawString(String str, int x, int y, float alpha){
		drawString(str, x, y, defaultTextColor, alpha);
	}

	public LobbyOptions getLobbyOptions() {
		return this.controller.getLobbyOptions();
	}
	
	public void reinit() throws SlickException {
		initInternal();
		gameScene = gameScene.reinit(controller.getScores());
		lobbyScene = lobbyScene.reinit();
	}
	public void exit(){
		System.out.println("Exit");
		controller.cleanRegistry();
		container.exit();
	}
}
