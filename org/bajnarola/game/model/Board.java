/*****************************************************************************/
/* Local board implementation class                                          */
/*                                                                           */
/* Copyright (C) 2015                                                        */
/* Marco Melletti, Davide Berardi, Matteo Martelli                           */
/*                                                                           */
/* This program is free software; you can redistribute it and/or             */
/* modify it under the terms of the GNU General Public License               */
/* as published by the Free Software Foundation; either version 2            */
/* of the License, or any later version.                                     */
/*                                                                           */
/* This program is distributed in the hope that it will be useful,           */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of            */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             */
/* GNU General Public License for more details.                              */
/*                                                                           */
/* You should have received a copy of the GNU General Public License         */
/* along with this program; if not, write to the Free Software               */
/* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,*/
/* USA.                                                                      */
/*****************************************************************************/

package org.bajnarola.game.model;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import org.bajnarola.game.controller.BoardController;

public class Board extends UnicastRemoteObject implements BoardController {
	private static final long serialVersionUID = -6564070861147997471L;

	int turn;
	ArrayList<Tile> scenario;
	ArrayList<Player> players;
	ArrayList<Tile> deck;
	
	public Board() throws RemoteException {
		this.turn = 0;
		this.scenario = new ArrayList<Tile>();
		this.deck = new ArrayList<Tile>();
		this.players = new ArrayList<Player>();
	}
	
	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public ArrayList<Tile> getScenario() {
		return scenario;
	}
	
	public ArrayList<Tile> getDeck() {
		return deck;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	/* Check if the given tile can be placed at position x y of the board */
	public Boolean probe(int x, int y, int tile) {
		/* TODO implement */
		return false;
	}
	
	/* Check if the given meeple can be placed at position x y of the board */
	public Boolean probeMeeple(int x, int y, int tile, Meeple meeple) {
		/* TODO: implement */
		return false;
	}
	
	/* Place a tile and a meeple (optional) to the position x y of the board.
	 * A null meeple must be passed to place the tile only. */
	public Boolean place(int x, int y, int tile, Meeple meeple) {
		/* TODO implement */
		return false;
	}
	
}

