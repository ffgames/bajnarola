package org.bajnarola.game.controller;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bajnarola.game.GameOptions;
import org.bajnarola.game.GuiThread;
import org.bajnarola.game.controller.GameController.endGameCause;
import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.Meeple;
import org.bajnarola.game.model.Player;
import org.bajnarola.game.model.Tile;
import org.bajnarola.game.view.Gui;
import org.bajnarola.game.view.LobbyScene.JoinStatus;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import sun.misc.Lock;

public class ViewController {

	static final int defaultResX = 1200;
	static final int defaultResY = 700;
	static final boolean defaultFullscreen = false; 
	static final String optFileName = ".bajnarola.conf";
	static String optFilePath;
	
	/* A queue of updates. Each update contains: 
	 *     - a set of points of all the landscape that 
	 *       have been completed at its corresponding turn. 
	 *     - information about the new placed tile. */
	List<ViewUpdate> viewUpdatesQueue;
	
	
	AppGameContainer appgc;
	Gui bajnarolaGui;
	Lock guiLock;
	Board board;
	Player player;
	GameController gameCtl;
	
	/* The tile drawn by the local play at the current turn */
	Tile drawnTile;
	
	
	public ViewController(Board board, GameController gameCtl) {
		super();
		viewUpdatesQueue = new ArrayList<>();
		this.gameCtl = gameCtl;
		this.drawnTile = null;
		this.guiLock = new Lock();
		this.board = board;
		this.player = null;
		
		try {
			this.guiLock.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			bajnarolaGui = new Gui(this);
			
			int resx = defaultResX, resy = defaultResY;
			boolean fullscreen = defaultFullscreen;
			
			try {
				File cwd = new File(System.getProperty("user.home"));
				File optFile = new File(cwd, optFileName);
				optFilePath = optFile.getAbsolutePath();
				if (!optFile.exists()) {
					System.out.println("Config file not found. Creating it...");
					optFile.createNewFile();
				}
			} catch (IOException e) {
				System.err.println("Can't create the config file");
			} 
			
			Properties prop = new Properties();
			
			try {
				prop.load(new FileInputStream(optFilePath));
				if (prop.isEmpty()) {
					prop.setProperty("resx", Integer.toString(resx));
					prop.setProperty("resy", Integer.toString(resy));
					prop.setProperty("fullscreen", Boolean.toString(fullscreen));
					prop.store(new FileOutputStream(optFilePath), "game options");
				} else {
					String tmp;
					
					if ((tmp = prop.getProperty("resx")) != null)
							resx = Integer.parseInt(tmp);
					if ((tmp = prop.getProperty("resy")) != null)
						resy = Integer.parseInt(tmp);
					if ((tmp = prop.getProperty("fullscreen")) != null)
						fullscreen = Boolean.parseBoolean(tmp);
				}
			} catch (IOException|NullPointerException e) {
				e.printStackTrace();
				System.err.println("Can't access to the config file");
			}
			
			Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			if (fullscreen) {
				resx = screenSize.width;
				resy =  screenSize.height;
			}
			
			appgc = new AppGameContainer(bajnarolaGui, resx, resy, fullscreen);
			
			GuiThread guiThread = new GuiThread(appgc);
			
			Thread thread = new Thread(guiThread);
			
			thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

		        public void uncaughtException(Thread t, Throwable e) {
		            System.out.println("exception " + e + " from thread " + t);
		            System.exit(1);
		        }
		    });
			
			thread.start();

		} catch (SlickException e) {
			
			e.printStackTrace();
		}
	}
	
	public int getMeeplesInHand(){
		return player.getMeepleCount();
	}
	
	public ViewUpdate dequeueViewUpdate() {
		if (this.viewUpdatesQueue.size() > 0)
			return this.viewUpdatesQueue.remove(0);
		else 
			return null;
	}
	
	public String getCurrentPlayerScore(){
		if(player != null)
			return player.getId()+"-"+player.getName() + ": "+player.getScore();
		return "";
	}
	
	public List<String> getScores(){
		List<String> scores = new ArrayList<String>();
		if(gameCtl.board.getPlayers() != null)
			for(Player pl : gameCtl.board.getPlayers()){
				scores.add(pl.getId()+"-"+pl.getName()+": "+pl.getScore());
			}
		return scores;
	}
	
	public void enqueueViewUpdate(ViewUpdate update) {
		this.viewUpdatesQueue.add(update);
	}
	
	/* Try to probe the current drawn tile on the board */
	public boolean probe(int x, int y) {
		return board.probe(x, y, this.drawnTile);
	}
	
	/* Try to probe a meeple of the current player on the current drawn tile */
	public boolean[] place(int x, int y) {
		Meeple meeple = this.player.getMeeple();
		boolean[] probeSides = new boolean[Tile.SIDE_COUNT];
		for (int i = 0; i < Tile.SIDE_COUNT; i++)
			probeSides[i] = false;
		
		board.place(x, y, drawnTile);
		
		if (meeple == null)
			return probeSides;
		
		for (short i = 0; i < Tile.SIDE_COUNT-1; i++) {
			meeple.setTileSide(i);
			probeSides[i] = board.probeMeeple(drawnTile, meeple);
		}
		probeSides[Tile.SIDE_CENTER] = (drawnTile.getElements()[Tile.SIDE_CENTER] == Tile.ELTYPE_CLOISTER ? true : false);
		
		player.giveMeepleBack(meeple);
		
		return probeSides;
	}
	
	public void rotate(boolean clockwise) {
		drawnTile.rotate(clockwise);
	}
	
	public void placeMeeple(short meepleTileSide) {
		if (meepleTileSide > -1) {
			Meeple meeple = this.player.getMeeple();
			meeple.setTileSide(meepleTileSide);
			this.board.placeMeeple(drawnTile, meeple);
		}
		
		guiLock.unlock();
	}

	
	public Tile waitViewChange(Tile drawnTile) {
		this.drawnTile = drawnTile;
		
		this.bajnarolaGui.viewPlayTurn(board.getHoles(), drawnTile);
		
		try {
			guiLock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return this.drawnTile;
	}
	
	public endGameCause getEndCause() {
		return gameCtl.getEndCause();
	}

	public Map<String, Integer> getFinalScores() {
		return gameCtl.getFinalScores();
	}

	public boolean amIWinner() {
		return gameCtl.amIWinner();
	}
	
	public void waitOptionsFromView() {
		try {
			guiLock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setPlayer(String playerName) {
		this.player = board.getPlayerByName(playerName);
		bajnarolaGui.setPlayerMeepleColor(player.getId());
	}
	
	public void setGameOptions(String playerName, String lobbyURI) throws MalformedURLException {
		this.gameCtl.setGameOptions(new GameOptions(playerName, lobbyURI));
		guiLock.unlock();
	}
	
	public void joinSignalView(JoinStatus cause) {
		this.bajnarolaGui.joinSignalLobbyScene(cause);
	}
}
