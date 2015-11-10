package org.bajnarola.game.controller;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bajnarola.game.GameOptions;
import org.bajnarola.game.GameOptions.LobbyOptions;
import org.bajnarola.game.GuiThread;
import org.bajnarola.game.GameOptions.ViewOptions;
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

	
	
	/* A queue of updates. Each update contains: 
	 *     - a set of points of all the landscape that 
	 *       have been completed at its corresponding turn. 
	 *     - information about the new placed tile. */
	List<ViewUpdate> viewUpdatesQueue;
	
	
	AppGameContainer appgc;
	Gui gui;
	Lock guiLock;
	Board board;
	Player player;
	GameController gameCtl;
	GameOptions gameOpt;
	
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
			gui = new Gui(this);

			gameOpt = gameCtl.getGameOptions();
			ViewOptions viewOpt = gameOpt.getViewOptions();
			GuiThread gt = new GuiThread(gui, viewOpt.getResx(), viewOpt.getResy(), viewOpt.isFullscreen());
			
			Thread thread = new Thread(gt);
			
			thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

		        public void uncaughtException(Thread t, Throwable e) {
		            System.out.println("exception " + e + " from thread " + t);
		            cleanRegistry();
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
		
		this.gui.viewPlayTurn(board.getHoles(), drawnTile);
		
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
		gui.setPlayerMeepleColor(player.getId());
	}
	
	public void setScores(){
		gui.initScores(getCurrentPlayerScore(), getScores());
	}
	
	public void setGameOptions(String playerName, String lobbyURI) throws MalformedURLException {
		this.gameCtl.getGameOptions().setPlayerName(playerName);
		this.gameCtl.getGameOptions().setLobbyHostPort(lobbyURI);
		guiLock.unlock();
	}
	
	public void setViewOptions(int resx, int resy, boolean fullscreen) {
		this.gameCtl.getGameOptions().setViewOptions(resx, resy, fullscreen);
	}
	
	public ViewOptions getViewOptions() {
		return this.gameCtl.getGameOptions().getViewOptions();
	}
	
	public LobbyOptions getLobbyOptions() {
		return this.gameCtl.getGameOptions().getLobbyOptions();
	}
	
	public void joinSignalView(JoinStatus cause) {
		this.gui.joinSignalLobbyScene(cause);
	}
	
	public void cleanRegistry() {
		this.gameCtl.cleanRegistry();
	}
}
