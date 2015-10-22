package org.bajnarola.game.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import org.bajnarola.game.model.Meeple;
import org.bajnarola.game.model.Player;
import org.bajnarola.game.model.Tile;
import org.bajnarola.game.model.Board;

import sun.misc.Lock;

public class GameBoard extends UnicastRemoteObject implements BoardController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Board board;
	Lock diceLock;
	Integer diceValue;
	Random randomGenerator;
	
	private void throwDice() {
		try {
			this.diceLock.lock();
			this.diceValue = this.randomGenerator.nextInt();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
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
		
		this.diceValue = -1;
		
		this.throwDice();
		
		/* XXX spareggi */
	}

	@Override
	public Integer getDiceValue() throws RemoteException {
		if(this.diceValue == 0) {
			try {
				this.diceLock.lock();
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
	
}
