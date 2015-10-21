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
	public int getTurn() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTurn(int turn) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Hashtable<Integer, Tile> getScenario() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Tile> getDeck() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Player> getPlayers() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean probe(short x, short y, Tile tile) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean probeMeeple(short x, short y, Tile tile, Meeple meeple)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void place(short x, short y, Tile tile, Meeple meeple)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateLandscape(short x, short y) throws RemoteException {
		// TODO Auto-generated method stub
		
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
}
