package org.bajnarola.game.controller;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bajnarola.game.GameOptions;
import org.bajnarola.game.GuiThread;
import org.bajnarola.game.controller.GameController.endGameCause;
import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.Meeple;
import org.bajnarola.game.model.Player;
import org.bajnarola.game.model.Tile;
import org.bajnarola.game.view.Gui;
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
			Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			appgc = new AppGameContainer(bajnarolaGui, screenSize.width, screenSize.height, false);
			
			GuiThread guiThread = new GuiThread(appgc);
			
			Thread thread = new Thread(guiThread);
			
			thread.start();

		} catch (SlickException e) {
			
			e.printStackTrace();
		}
	}
	
	public ViewUpdate dequeueViewUpdate() {
		if (this.viewUpdatesQueue.size() > 0)
			return this.viewUpdatesQueue.remove(0);
		else 
			return null;
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
		
		for (short i = 0; i < Tile.SIDE_COUNT; i++) {
			meeple.setTileSide(i);
			probeSides[i] = board.probeMeeple(drawnTile, meeple);
		}
		
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

	
	public void waitViewChange(Tile drawnTile) {
		this.drawnTile = drawnTile;
		
		try {
			guiLock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
	}
	
	public void setGameOptions(String playerName, String lobbyURI) {
		this.gameCtl.setGameOptions(new GameOptions(playerName, lobbyURI));
		guiLock.unlock();
	}
}
