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
import java.util.Hashtable;

import org.bajnarola.game.controller.BoardController;

public class Board {
	
	int turn;
	Hashtable<Integer, Tile> scenario;
	ArrayList<Player> players;
	ArrayList<Tile> deck;
	
	public Board() throws RemoteException {
		this.turn = 0;
		this.scenario = new Hashtable<Integer, Tile>();
		this.deck = new ArrayList<Tile>();
		this.players = new ArrayList<Player>();
	}
	
	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public Hashtable<Integer, Tile> getScenario() {
		return scenario;
	}
	
	
	public Tile getTile(short x, short y) {
		return scenario.get(getKey(x, y));
	}
	
	
	public ArrayList<Tile> getDeck() {
		return deck;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	/* Check if the given tile can be placed at position x y of the board */
	public Boolean probe(short x, short y, Tile tile) {
	
		Tile t;
		
		//LEFT TILE
		t = scenario.get(getKey((short)(x - 1), y));
		if (t != null && 
				tile.getElements()[Tile.SIDE_LEFT] != 
				t.getElements()[Tile.SIDE_RIGHT])
			return false;
	
		//RIGHT TILE
		t = scenario.get(getKey((short)(x + 1), y));
		if (t != null && 
				tile.getElements()[Tile.SIDE_RIGHT] != 
				t.getElements()[Tile.SIDE_LEFT])
			return false;

		//BOTTOM
		t = scenario.get(getKey(x, (short)(y - 1)));
		if (t != null && 
				tile.getElements()[Tile.SIDE_BOTTOM] != 
				t.getElements()[Tile.SIDE_TOP])
			return false;
	
		//TOP
		t = scenario.get(getKey(x, (short)(y + 1)));
		if (t != null && 
				tile.getElements()[Tile.SIDE_TOP] != 
				t.getElements()[Tile.SIDE_BOTTOM])
			return false;
			
		return true;
	}
	
	/* Check if the given meeple can be placed on the Tile tile (x,y of the board) at the position tilePos */
	public Boolean probeMeeple(short x, short y, Tile tile, Meeple meeple) {
		/* Can't place a meeple on the grass */
		if (tile.getElements()[meeple.getTileSide()] == Tile.ELTYPE_GRASS)
			return false;
		
		short tx, ty;
		tx = x;
		ty = y;
		
		switch(meeple.getTileSide()) {
			case Tile.SIDE_TOP:
				ty += 1;
				break;
			case Tile.SIDE_RIGHT:
				tx += 1;
				break;
			case Tile.SIDE_BOTTOM:
				ty -= 1;
				break;
			case Tile.SIDE_LEFT:
				tx -= 1;
				break;
			case Tile.SIDE_CENTER: 
		
		}
		
		
		return false;
	}
	
	/* Place a tile and a meeple (optional) to the position x y of the board.
	 * A null meeple must be passed to place the tile only. */
	public void place(short x, short y, Tile tile, Meeple meeple) {
		tile.setMeeple(meeple);
		
		scenario.put(getKey(x,y), tile);
	}

	public void updateLandscape(short x, short y) {
		/* TODO Landscape elements */
		
		/* Per ogni elemento della tile
		 *  -  Controlla se creare un landscape element (se cloister aggiungergli le tile adiacenti)
		 *    o aggiungere la tile ad un landscape già esistente
		 * -  Se è stata aggiunta controllare se fare merge del landscape a cui è stata collegata
		 *    con altri landscape adiacenti dello stesso tipo */
		
		/* Per tutte le 8 tile adiacenti a x y:
		 * - controllare se c'è un monastero e in caso aggiungerla al relativo landscape 
		 */
	}
	
	private static final Integer getKey(short x, short y) {	
		return (int) x | ((int) y << 16);
	}
	
}

