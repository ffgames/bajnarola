package org.bajnarola.game.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.bajnarola.game.BajnarolaServer;
import org.bajnarola.game.GameOptions;
import org.bajnarola.game.MainClass;
import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.Meeple;
import org.bajnarola.game.model.Player;
import org.bajnarola.game.model.Tile;

import sun.misc.Lock;

public class GameController extends UnicastRemoteObject implements
		GameControllerRemote {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum endGameCause {
		notEnded,
		deckEmpty,
		lastPlayer
	}
	
	endGameCause endCause;
	Map<String, Integer> finalScores;
	boolean winner;
	GameOptions goptions;
	BajnarolaServer myServer;
	
	Board board;
	Lock diceLock;
	ReentrantLock playLock;
	Integer diceValue;
	Random randomGenerator;
	Condition waitCondition;
	public ViewController viewCtl;
	TurnDiff myTurnDiff = null;
	
	public int myPlayedTurn = 0;

	private void throwDice() {
		this.diceValue = this.randomGenerator.nextInt();
		this.diceLock.unlock();
	}

	public GameController() throws RemoteException {
		
		this.endCause = endGameCause.notEnded;
		this.finalScores = new Hashtable<String, Integer>();
		this.winner = false;
		
		this.randomGenerator = new Random();

		this.board = new Board();
		

		this.goptions = new GameOptions();
		this.viewCtl = new ViewController(board, this);

		this.diceLock = new Lock();
		this.playLock = new ReentrantLock();
		try {
			this.diceLock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.waitCondition = this.playLock.newCondition();
		this.myPlayedTurn = -1;
		this.diceValue = null;
		
		this.throwDice();
		
		
		/* XXX spareggi */
	}

	public void setMyServer(BajnarolaServer s) {
		this.myServer = s;
	}
	
	public void setGameOptions(GameOptions goptions) {
		this.goptions = goptions;
	}
	
	public GameOptions getGameOptions() {
		return this.goptions;
	}
	
	public void waitOptionsFromUser() {
		viewCtl.waitOptionsFromView();
	}


	@Override
	public Integer getDiceValue() throws RemoteException {
		if (this.diceValue == null) {
			try {
				this.diceLock.lock();
				this.diceLock.unlock();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return this.diceValue;
	}

	@SuppressWarnings("unused")
	public boolean isDeckEmpty() {
		if(MainClass.debugPlay && board.debugPlayTiles <= 0)
			return true;
		return board.getDeck().isEmpty();
	}

	public endGameCause getEndCause() {
		return endCause;
	}

	public Map<String, Integer> getFinalScores() {
		return finalScores;
	}

	public boolean amIWinner() {
		return winner;
	}
	

	@Override
	public TurnDiff play(Integer turn) throws RemoteException {
		this.playLock.lock();
		try {
			if (this.myPlayedTurn < turn) {
				System.out.println("Asking for turn " + turn);
				this.waitCondition.await();
			} else if (this.myPlayedTurn > turn) {
				System.err.println("Wrong turn request: myTurn="
						+ this.myPlayedTurn + " turn: " + turn);
				/* throw new Exception("Wrong turn"); */
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.playLock.unlock();
		}
		return this.myTurnDiff;
	}

	/*
	 * Update the internal board status after a turn has been 
	 * played by another player. */
	public void updateBoard(TurnDiff diff) throws Exception {
		Player p;
		Tile tile;
		Meeple meeple;

		tile = board.beginTurn();
		for(int i = 0; i < diff.tileDirection; i++)
			tile.rotate(true);
		
		/* XXX: It should never reaches this point if the deck is empty 
		 * if (tile == null) {
			System.out.println("Game end");
			return true;
		}*/

		p = board.getPlayerByName(diff.playerName);
		if (p == null)
			throw new Exception("Error: missing player");

		if (!board.probe(diff.x, diff.y, tile))
			throw new Exception("Error: illegal tile placement");

		board.place(diff.x, diff.y, tile);

		if (diff.meepleTileSide > -1) {
			meeple = p.getMeeple();
			meeple.setTileSide(diff.meepleTileSide);
			if (!board.probeMeeple(tile, meeple))
				throw new Exception("Error: illegal meeple placement");

			board.placeMeeple(tile, meeple);
		}

		Tile updateTile = tile.clone();
		
		Map<String, Boolean> points = new Hashtable<String, Boolean>();
		points.putAll(board.endTurn(tile));
		List<String> scores = new ArrayList<String>();
		scores.addAll(board.getScoreUpdates());

		viewCtl.enqueueViewUpdate(new ViewUpdate(points, updateTile, scores));
	}

	public void localPlay(String me) {
		this.playLock.lock();

		System.out.print("Playing...");

		Tile tile = board.beginTurn();
		Tile drawTile;
		/* XXX: It should never reaches this point if the deck is empty
		if (tile == null) 
			return true; */

		drawTile = viewCtl.waitViewChange(tile);
		tile = drawTile.clone();
		
		Map<String, Boolean> points = new Hashtable<String, Boolean>();
		points.putAll(board.endTurn(drawTile));
		List<String> scores = new ArrayList<String>();
		scores.addAll(board.getScoreUpdates());

		viewCtl.enqueueViewUpdate(new ViewUpdate(points, tile, scores));
		
		short meepleTileSide = -1;
		if (tile.getMeeple() != null)
			meepleTileSide = tile.getMeeple().getTileSide();

		myTurnDiff = new TurnDiff(tile.getX(), tile.getY(),
				(short) tile.getDirection(), meepleTileSide, me);

		System.out.println("OK");

		this.myPlayedTurn++;
		this.waitCondition.signalAll();

		this.playLock.unlock();
	}

	public Map<String,Integer> finalCheckScore() {
		Map<String,Integer> scores = new Hashtable<String, Integer>();
		board.finalCheckScores();
		
		List<String> lsScores = new ArrayList<String>();
		lsScores.addAll(board.getScoreUpdates());
		viewCtl.enqueueViewUpdate(new ViewUpdate(null, null, lsScores));
		
		for (Player p : board.getPlayers())
			scores.put(p.getName(), (int)p.getScore());
		
		return scores;
	}

	
	/* This is called when the game ends */
	public void endGame(endGameCause cause, Map<String, Integer> scores, boolean winner) {
	
		this.endCause = cause;
		this.finalScores = scores;
		this.winner = winner;
		
		this.viewCtl.enqueueViewUpdate(new ViewUpdate(null, null, null));
	}


	public void initBoard(String playerName, List<String> playerNames, int seed) {
		Tile initialTile = board.initBoard(playerNames, seed);
		viewCtl.setPlayer(playerName);
		ViewUpdate firstupdate = new ViewUpdate(new Hashtable<String, Boolean>(), initialTile, null);
		viewCtl.enqueueViewUpdate(firstupdate);
		viewCtl.setScores();
	}
	
	public void cleanRegistry() {
		if (this.myServer != null)
			this.myServer.cleanRegistry();
	}
	
}
