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
		/* initialise the deck: create all the tiles. */
		initDeck();
		
		/* add the initial tile */
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
	
	/* Check if the given tile can be placed at position x y of the board.
	 * Each side of the tile should touch a neighbour with the same side
	 * or a neighbour should not be present at all. */
	public boolean probe(short x, short y, Tile tile) {
	
		Tile t;
		
		/* Left side */
		t = scenario.get(getKey((short)(x - 1), y));
		if (t != null && 
				tile.getElements()[Tile.SIDE_LEFT] != 
				t.getElements()[Tile.SIDE_RIGHT])
			return false;
	
		/* Right side */
		t = scenario.get(getKey((short)(x + 1), y));
		if (t != null && 
				tile.getElements()[Tile.SIDE_RIGHT] != 
				t.getElements()[Tile.SIDE_LEFT])
			return false;

		/* Bottom side */
		t = scenario.get(getKey(x, (short)(y - 1)));
		if (t != null && 
				tile.getElements()[Tile.SIDE_BOTTOM] != 
				t.getElements()[Tile.SIDE_TOP])
			return false;
	
		/* Top side */
		t = scenario.get(getKey(x, (short)(y + 1)));
		if (t != null && 
				tile.getElements()[Tile.SIDE_TOP] != 
				t.getElements()[Tile.SIDE_BOTTOM])
			return false;
			
		return true;
	}
	
	/* Place a tile and a meeple (optional) to the position x y of the board.
	 * A null meeple must be passed to place the tile only.
	 * The landscape elements related to that tile are updated 
	 * according to current status of the scenario. */
	public void place(short x, short y, Tile tile, Meeple meeple) {
		tile.setMeeple(meeple);
		scenario.put(getKey(x,y), tile);
		updateLandscape(x, y, tile);
	}

	/* Given a tile at position x, y, return the neighbour tile that is 
	 * touching the first one at the specified side. */
	private Tile getNeighbourTile(short x, short y, short side) {
		switch (side) {
			case Tile.SIDE_TOP:
				return scenario.get(getKey(x,(short)(y + 1)));
			case Tile.SIDE_RIGHT:
				return scenario.get(getKey((short)(x + 1),y));
			case Tile.SIDE_BOTTOM:
				return scenario.get(getKey(x,(short)(y - 1)));
			case Tile.SIDE_LEFT:
				return scenario.get(getKey((short)(x - 1),y));
			case Tile.SIDE_CENTER:
			default:
				return null;
		}
	}
	
	private void attachToNeighbour(Tile tile, Tile neighbour, short side) {
		short citiesCount, streetsCount;
		citiesCount = tile.countElement(Tile.ELTYPE_CITY);
		streetsCount = tile.countElement(Tile.ELTYPE_STREET);
		
		switch (tile.getElements()[side]){
			case Tile.ELTYPE_CITY:
				if (citiesCount <= 2) 
					neighbour.getLandscapes().get(getInverseDirection(side)).addTile(tile, side);
				else {
					LandscapeElement ls1, ls2;
					if ((ls1 = tile.getLandscapes().get(side)) != null) {
						ls2 = neighbour.getLandscapes().get(getInverseDirection(side));
						ls1.merge(ls2);
					} else {
						neighbour.getLandscapes().get(getInverseDirection(side)).addTile(tile, side);
						ls1 = tile.getLandscapes().get(side);
						for (int j = 0; j < Tile.SIDE_COUNT; j++)
							if (tile.getElements()[j] == Tile.ELTYPE_CITY && j != side)
								tile.getLandscapes().put(j, ls1);
					}
				}
				break;
			case Tile.ELTYPE_STREET:
				if (streetsCount != 2) {
					neighbour.getLandscapes().get(getInverseDirection(side)).addTile(tile, side);
				} else {
					LandscapeElement ls1, ls2;
					if ((ls1 = tile.getLandscapes().get(side)) != null) {
						ls2 = neighbour.getLandscapes().get(getInverseDirection(side));
						ls1.merge(ls2);
					} else {
						neighbour.getLandscapes().get(getInverseDirection(side)).addTile(tile, side);
						ls1 = tile.getLandscapes().get(side);
						for (int j = 0; j < Tile.SIDE_COUNT; j++) {
							if (tile.getElements()[j] == Tile.ELTYPE_STREET && j != side) {
								tile.getLandscapes().put(j, ls1);
								break;
							}
						}
					}
				}
				break;	
		}
	}
	
	private void createTileLandscape(Tile tile, short x, short y, short side) {
		short citiesCount, streetsCount;
		citiesCount = tile.countElement(Tile.ELTYPE_CITY);
		streetsCount = tile.countElement(Tile.ELTYPE_STREET);
		
		Tile tmpTile;
		/* Create a new landscape if it does not exist yet.*/
		switch(tile.getElements()[side]){
			case Tile.ELTYPE_CITY:
				if (citiesCount <= 2)
					new City(tile, side);
				else if (tile.getLandscapes().get(side) == null) {
					LandscapeElement nc = new City(tile, side);
					for (int j = 0; j < Tile.SIDE_COUNT; j++)
						if (tile.getElements()[j] == Tile.ELTYPE_CITY && j != side)
							tile.getLandscapes().put(j, nc);
				}	
				break;
			case Tile.ELTYPE_STREET:
				if (streetsCount != 2)
					new Street(tile, side);
				else if (tile.getLandscapes().get(side) == null) {
					/* If there is not a landscape for this side yet, 
					 * create it and spalmate it*/
					LandscapeElement ns = new Street(tile, side);
					for (int j = 0; j < Tile.SIDE_COUNT; j++)
						if (tile.getElements()[j] == Tile.ELTYPE_STREET && j != side)
							tile.getLandscapes().put(j, ns);
				}	
				break;
			case Tile.ELTYPE_CLOISTER:
				Cloister c = new Cloister(tile, side);
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
	
	private void updateLandscape(short x, short y, Tile tile) {
		
		
		Tile neighbour = null;
		
		for (short i = 0; i < Tile.SIDE_COUNT; i++) {			
			/* Get the neighbour at the ith side. */
			neighbour = getNeighbourTile(x, y, i);
			
			/* If a neighbour exists at the ith side, also a related landscape 
			 * should be present. Thus add the tile to the existent landscape.
			 * Otherwise create a new landscape. */
			if (neighbour != null)
				attachToNeighbour(tile, neighbour, i);
			else
				createTileLandscape(tile, x, y, i);
			
			
			checkScores(tile.getLandscapes().get(i));
		}
		
		
		/* Check if there is any cloister on the 8 neighbour tiles of this current tile. 
		 * Add every cloister neighbour to its landscape. */
		for (short j = -1; j <= 1; j++) {
			for (short k = -1; k <= 1; k++) {
				if (j != k || j != 0) {
					neighbour = scenario.get(getKey((short)(x + j), (short)(y + k)));
					if (neighbour != null && neighbour.getElements()[Tile.SIDE_CENTER] == Tile.ELTYPE_CLOISTER){
						neighbour.getLandscapes().get(Tile.SIDE_CENTER).addTile(tile, (short)-1);
						checkScores(neighbour.getLandscapes().get(Tile.SIDE_CENTER));
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

