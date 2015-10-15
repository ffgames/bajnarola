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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Board {
	
	int turn;
	Hashtable<Integer, Tile> scenario;
	ArrayList<Player> players;
	ArrayList<Tile> deck;
	
	public Board(List<String> playerNames) {
		this.turn = 0;
		this.scenario = new Hashtable<Integer, Tile>();
		this.deck = new ArrayList<Tile>();
		this.players = new ArrayList<Player>();
		
		initBoard();
		
		for(String pl : playerNames){
			players.add(new Player(pl));
		}
	}
	
	private void initBoard() {
		/* - init the deck (create the tiles and add to it)
		 * - add initial tile (standard) */
		
		initDeck();
		
		scenario.put(getKey((short)0, (short)0), initTile(Tile.ELTYPE_CITY,
				Tile.ELTYPE_STREET, 
				Tile.ELTYPE_GRASS, 
				Tile.ELTYPE_STREET, 
				Tile.ELTYPE_GRASS, 
				false));
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
	public boolean probe(short x, short y, Tile tile) {
	
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
	
	/* Place a tile and a meeple (optional) to the position x y of the board.
	 * A null meeple must be passed to place the tile only. */
	public void place(short x, short y, Tile tile, Meeple meeple) {
		tile.setMeeple(meeple);
		
		scenario.put(getKey(x,y), tile);
		
		updateLandscape(x, y, tile);
	}

	private void updateLandscape(short x, short y, Tile tile) {
		
		short citiesCount, streetsCount;
		citiesCount = tile.countElement(Tile.ELTYPE_CITY);
		streetsCount = tile.countElement(Tile.ELTYPE_STREET);
		Tile tmpTile = null;
		
		for (short i = 0; i < Tile.SIDE_COUNT; i++) {
			/* Per ogni elemento della tile
			 *  -  Controlla se creare un landscape element (se cloister aggiungergli le tile adiacenti)
			 *    o aggiungere la tile ad un landscape già esistente
			 * -  Se è stata aggiunta controllare se fare merge del landscape a cui è stata collegata
			 *    con altri landscape adiacenti dello stesso tipo */
			switch (i) {
				case Tile.SIDE_TOP:
					tmpTile = scenario.get(getKey(x,(short)(y + 1)));
					break;
				case Tile.SIDE_RIGHT:
					tmpTile = scenario.get(getKey((short)(x + 1),y));
					break;
				case Tile.SIDE_BOTTOM:
					tmpTile = scenario.get(getKey(x,(short)(y - 1)));
					break;
				case Tile.SIDE_LEFT:
					tmpTile = scenario.get(getKey((short)(x - 1),y));
					break;
				case Tile.SIDE_CENTER:
					tmpTile = null;
					break;
			}
			
			/* Add the tile to an existent landscape */
			if (tmpTile != null) {
				switch (tile.getElements()[i]){
					case Tile.ELTYPE_CITY:
						if (citiesCount <= 2) 
							tmpTile.getLandscapes().get(getInverseDirection(i)).addTile(tile, i);
						else {
							LandscapeElement ls1, ls2;
							if ((ls1 = tile.getLandscapes().get(i)) != null) {
								ls2 = tmpTile.getLandscapes().get(getInverseDirection(i));
								ls1.merge(ls2);
							} else {
								tmpTile.getLandscapes().get(getInverseDirection(i)).addTile(tile, i);
								ls1 = tile.getLandscapes().get(i);
								for (int j = 0; j < Tile.SIDE_COUNT; j++)
									if (tile.getElements()[j] == Tile.ELTYPE_CITY && j != i)
										tile.getLandscapes().put(j, ls1);
							}
						}
						break;
					case Tile.ELTYPE_STREET:
						if (streetsCount != 2) {
							tmpTile.getLandscapes().get(getInverseDirection(i)).addTile(tile, i);
						} else {
							LandscapeElement ls1, ls2;
							if ((ls1 = tile.getLandscapes().get(i)) != null) {
								ls2 = tmpTile.getLandscapes().get(getInverseDirection(i));
								ls1.merge(ls2);
							} else {
								tmpTile.getLandscapes().get(getInverseDirection(i)).addTile(tile, i);
								ls1 = tile.getLandscapes().get(i);
								for (int j = 0; j < Tile.SIDE_COUNT; j++) {
									if (tile.getElements()[j] == Tile.ELTYPE_STREET && j != i) {
										tile.getLandscapes().put(j, ls1);
										break;
									}
								}
							}
						}
						break;	
				}
			} else {
				/* Create a new landscape */
				switch(tile.getElements()[i]){
					case Tile.ELTYPE_CITY:
						if (citiesCount <= 2)
							new City(tile, i);
						else if (tile.getLandscapes().get(i) == null) {
							LandscapeElement nc = new City(tile, i);
							for (int j = 0; j < Tile.SIDE_COUNT; j++)
								if (tile.getElements()[j] == Tile.ELTYPE_CITY && j != i)
									tile.getLandscapes().put(j, nc);
						}	
						break;
					case Tile.ELTYPE_STREET:
						if (streetsCount != 2)
							new Street(tile, i);
						else if (tile.getLandscapes().get(i) == null) {
							/* If there is not a landscape for this side yet, 
							 * create it and spalmate it*/
							LandscapeElement ns = new Street(tile, i);
							for (int j = 0; j < Tile.SIDE_COUNT; j++)
								if (tile.getElements()[j] == Tile.ELTYPE_STREET && j != i)
									tile.getLandscapes().put(j, ns);
						}	
						break;
					case Tile.ELTYPE_CLOISTER:
						Cloister c = new Cloister(tile, i);
						for (short j = -1; j <= 1; j++) {
							for (short k = -1; k <= 1; k++) {
								if (j != k || j != 0) {
									tmpTile = scenario.get(getKey((short)(x + j), (short)(y + k)));
									if (tmpTile != null)
										c.addTile(tmpTile, (short)-1);
								}
							}
						}
						break;					
				}
			} 
			checkScores(tile.getLandscapes().get(i));
		}
		
		
		/* Per tutte le 8 tile adiacenti a x y:
		 * - controllare se c'è un monastero e in caso aggiungerla al relativo landscape 
		 */
		for (short j = -1; j <= 1; j++) {
			for (short k = -1; k <= 1; k++) {
				if (j != k || j != 0) {
					tmpTile = scenario.get(getKey((short)(x + j), (short)(y + k)));
					if (tmpTile != null && tmpTile.getElements()[Tile.SIDE_CENTER] == Tile.ELTYPE_CLOISTER){
						tmpTile.getLandscapes().get(Tile.SIDE_CENTER).addTile(tile, (short)-1);
						checkScores(tmpTile.getLandscapes().get(Tile.SIDE_CENTER));
					}
					
				}
			}
		}
		
		
	}
	
	/* Check if the given meeple can be placed on the Tile tile (x,y of the board) at the position tilePos */
	public boolean probeMeeple(short x, short y, Tile tile, Meeple meeple) {
		/* Can't place a meeple on the grass */
		if (tile.getElements()[meeple.getTileSide()] == Tile.ELTYPE_GRASS)
			return false;
		
		return tile.getLandscapes().get(meeple.getTileSide()).isMeepleDeployable(meeple.getOwner());
	}
	
	public void placeMeeple(Tile tile, Meeple meeple) {
		tile.setMeeple(meeple);
	}
	
	private static final Integer getKey(short x, short y) {	
		return (int) x | ((int) y << 16);
	}
	
	private static final short getInverseDirection(short direction) {
		switch(direction) {
			case Tile.SIDE_TOP:
				return Tile.SIDE_BOTTOM;
			case Tile.SIDE_RIGHT:
				return Tile.SIDE_LEFT;
			case Tile.SIDE_BOTTOM:
				return Tile.SIDE_TOP;
			case Tile.SIDE_LEFT:
				return Tile.SIDE_RIGHT;
		}
		
		return direction;
	}
	
	private static final void checkScores(LandscapeElement scoreEl) {
		if (scoreEl != null && !scoreEl.isScoreSet() && scoreEl.isCompleted()){
			List<Player> owners = scoreEl.getScoreOwners();
			for(Player o : owners){
				o.setScore((short)(o.getScore()+scoreEl.getValue()));
			}
			scoreEl.clear();
			scoreEl.setScore();
		}
	}
	
	private static final Tile initTile(short top, short right, short bottom, short left, short center, boolean pennant){
		String name = "";
		short flags[] = {top, right, bottom, left, center};
		for(int i = 0; i < Tile.SIDE_COUNT; i++){
			switch(flags[i]){
				case Tile.ELTYPE_CITY:
					name += "C";
				case Tile.ELTYPE_GRASS:
					name += "G";
				case Tile.ELTYPE_STREET:
					name += "S";
				case Tile.ELTYPE_CLOISTER:
					name += "M";
			}
		}
		if(pennant)
			name += "P";
		
		return new Tile(center, top, right, bottom, left, pennant, name);
	}
	
	private void initDeck(){
		for(int i = 0; i < 4; i++){
			deck.add(initTile(
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_CLOISTER, 
					false));
		}
		
		for(int i = 0; i < 5; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					false));
		}
		
		deck.add(initTile(
				Tile.ELTYPE_GRASS,
				Tile.ELTYPE_CITY, 
				Tile.ELTYPE_GRASS, 
				Tile.ELTYPE_CITY, 
				Tile.ELTYPE_CITY, 
				false));
		
		for(int i = 0; i < 2; i++){
			deck.add(initTile(
					Tile.ELTYPE_GRASS,
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_CITY, 
					true));
		}
		
		for(int i = 0; i < 3; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_CITY, 
					false));
		}
		
		for(int i = 0; i < 2; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_CITY, 
					true));
		}
		
		for(int i = 0; i < 3; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					false));
		}
		
		for(int i = 0; i < 2; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					false));
		}
		
		for(int i = 0; i < 3; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_CITY, 
					false));
		}
		
		deck.add(initTile(
				Tile.ELTYPE_CITY,
				Tile.ELTYPE_CITY, 
				Tile.ELTYPE_GRASS, 
				Tile.ELTYPE_CITY, 
				Tile.ELTYPE_CITY, 
				true));
			
		deck.add(initTile(
				Tile.ELTYPE_CITY,
				Tile.ELTYPE_CITY, 
				Tile.ELTYPE_CITY, 
				Tile.ELTYPE_CITY, 
				Tile.ELTYPE_CITY, 
				true));
		
		for(int i = 0; i < 2; i++){
			deck.add(initTile(
					Tile.ELTYPE_GRASS,
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_CLOISTER, 
					false));
		}
		
		deck.add(initTile(
				Tile.ELTYPE_CITY,
				Tile.ELTYPE_CITY, 
				Tile.ELTYPE_STREET, 
				Tile.ELTYPE_CITY, 
				Tile.ELTYPE_CITY, 
				false));
		
		for(int i = 0; i < 2; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_CITY, 
					true));
		}
		
		for(int i = 0; i < 8; i++){
			deck.add(initTile(
					Tile.ELTYPE_GRASS,
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_GRASS, 
					false));
		}
		
		for(int i = 0; i < 3; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_GRASS, 
					false));
		}
		
		for(int i = 0; i < 3; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_CITY, 
					false));
		}
		
		for(int i = 0; i < 2; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_CITY, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_CITY, 
					true));
		}
		
		for(int i = 0; i < 9; i++){
			deck.add(initTile(
					Tile.ELTYPE_GRASS,
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_GRASS, 
					false));
		}
		
		for(int i = 0; i < 3; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_GRASS, 
					false));
		}
		
		for(int i = 0; i < 3; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_GRASS, 
					Tile.ELTYPE_GRASS, 
					false));
		}
		
		for(int i = 0; i < 4; i++){
			deck.add(initTile(
					Tile.ELTYPE_GRASS,
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_GRASS, 
					false));
		}
		
		for(int i = 0; i < 3; i++){
			deck.add(initTile(
					Tile.ELTYPE_CITY,
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_STREET, 
					Tile.ELTYPE_GRASS, 
					false));
		}
		
		deck.add(initTile(
				Tile.ELTYPE_STREET,
				Tile.ELTYPE_STREET, 
				Tile.ELTYPE_STREET, 
				Tile.ELTYPE_STREET, 
				Tile.ELTYPE_GRASS, 
				false));
	}
}

