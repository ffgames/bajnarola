package org.bajnarola.game.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.Meeple;
import org.bajnarola.game.model.Player;
import org.bajnarola.game.model.Tile;

import sun.misc.Lock;

public class GameBoard extends UnicastRemoteObject implements BoardController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Board board;
	Lock diceLock;
	ReentrantLock playLock;
	Integer diceValue;
	Random randomGenerator;
	Condition waitCondition;
	TurnDiff myTurnDiff = null;

	public int myPlayedTurn = 0;
	private void throwDice() {
		this.diceValue = this.randomGenerator.nextInt();
		this.diceLock.unlock();
	}

	public GameBoard() throws RemoteException {
		/* XXX: check if the seed is distributed or we need
		 * a different integer distributed on the nodes.
		 */
		this.randomGenerator = new Random();
		// XXX: new Board constructor needs players list
		//this.board = new Board();

		this.diceLock = new Lock();
		this.playLock = new ReentrantLock();
		try {
			this.diceLock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		this.waitCondition = this.playLock.newCondition();
		this.myPlayedTurn=-1;
		this.diceValue = null;
		
		this.throwDice();
				
		/* XXX spareggi */
	}

	@Override
	public Integer getDiceValue() throws RemoteException {
		if(this.diceValue == null) {
			try {
				this.diceLock.lock();
				this.diceLock.unlock();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return this.diceValue;
	}
	
	/* Update the internal board status after a turn has been played by another player.
	 * Return true if the game has ended, false otherwise. */
	public boolean updateBoard(TurnDiff diff) throws Exception {
		Player p;		
		Tile tile;
		Meeple meeple;
	
		tile = board.beginTurn();
		if (tile == null) {
			System.out.println("Game end"); /* TODO: game end */
			return true;
		}
		
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
		
 		board.endTurn(tile);
 		
		return false;
	}

	@Override
	public int getTurn() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTurn(int turn) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String winner() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TurnDiff play(String sender, Integer turn) throws RemoteException {
		this.playLock.lock();
		try {
			if (this.myPlayedTurn < turn) {
				System.out.println("Asking for turn " + turn);
				this.waitCondition.await();
			} else if (this.myPlayedTurn > turn) {
				System.err.println("Wrong turn request: myTurn=" + this.myPlayedTurn + " turn: " + turn);
				/* throw new Exception("Wrong turn"); */
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.playLock.unlock();
		}
		return this.myTurnDiff;
	}
	
	public TurnDiff localPlay(String me){
		this.playLock.lock();
		
		System.out.print("Playing...");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Tile tile = board.beginTurn();
		
		/* TODO: - Wait for the view
		 * updateturn()
		 * 		 - Notify the model
		 *		 - Model create LocalDiff
		 *		 - Notify view */
		
		board.endTurn(tile);
		
		
		short meepleTileSide = -1;
		if (tile.getMeeple() != null)
			meepleTileSide = tile.getMeeple().getTileSide();
		
		myTurnDiff = new TurnDiff(tile.getX(),
		                          tile.getY(),
		                          (short)tile.getDirection(), 
		                          meepleTileSide,
		                          me);
		
		
		System.out.println("OK");
		
		this.myPlayedTurn++;
		this.waitCondition.signalAll();

		this.playLock.unlock();
		return this.myTurnDiff;
	}
	
}
