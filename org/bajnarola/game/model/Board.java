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
import java.util.Map;

import org.bajnarola.utils.Shuffler;

public class Board {
	
	public static final int TOTAL_TILES_COUNT = 72;
	
	int turn;
	Hashtable<String, Tile> scenario;
	ArrayList<Player> players;
	ArrayList<Tile> deck;
	List<String> holes;
	Shuffler random;
	
	/* TODO:
	 * - negotiate random seed for deck shuffling
	 * - check hasmaps with short keys mapped onto Integer (short must be cast to int before access)
	 * - probeAll to check the current scenario boundary
	 * */
	
	public Board() {
		this.turn = 0;
		this.scenario = new Hashtable<String, Tile>();
		this.deck = new ArrayList<Tile>();
		this.players = new ArrayList<Player>();
		this.holes = new ArrayList<String>();
	}
	
	public Tile initBoard(List<String> playerNames, boolean shuffle, int seed) {
		
		short i = 0;
		for(String pl : playerNames){
			players.add(new Player(pl, i));
			i++;	
		}
		
		/* initialise the deck: create all the tiles. */
		initDeck();
		
		if(shuffle){
			random = new Shuffler(seed);
			random.shuffleDeck(deck);
		}
		
		Tile initialTile = initTile(Tile.ELTYPE_CITY,
				Tile.ELTYPE_STREET, 
				Tile.ELTYPE_GRASS, 
				Tile.ELTYPE_STREET, 
				Tile.ELTYPE_GRASS, 
				false);
		
		/* add the initial tile */
		place((short) 0, (short) 0, initialTile);
		
		return initialTile;
	}
	
	public Tile initBoard(List<String> playerNames, int seed) {
		return initBoard(playerNames, true, seed);
	}
	
	public Player getPlayerByName(String name) {
		for (Player p : players) {
			if (p.getName().equals(name)) 
				return p;
		}
		return null;
	}
	
	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public Hashtable<String, Tile> getScenario() {
		return scenario;
	}
	
	public List<String> getHoles() {
		return this.holes;
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
	
	public Tile beginTurn(){
		Tile newTile = null;
		boolean ok = false;

		while(!ok){
			newTile = deck.remove(0);
			if(newTile == null)
				break;
			for(int i = 0; i < 4; i++){
				if(!ok){
					for(String k : holes){
						if(probe(k, newTile)){
							ok = true;
							break;
						}
					}
				}
				newTile.rotate(true);
			}
		}
		
		return newTile;
	}
	
	public Map<String, Boolean> endTurn(Tile tile){
		Map<String, Boolean> points = new Hashtable<String, Boolean>();
		
		for (short i = 0; i < Tile.SIDE_COUNT; i++) {
			points.putAll(checkScores(tile.getLSElement(i), false));
		}
		
		return points;
	}
	
	/* Check if the given tile can be placed at position x y of the board.
	 * Each side of the tile should touch a neighbour with the same side
	 * or a neighbour should not be present at all. */
	public boolean probe(int x, int y, Tile tile) {
	
		Tile t;
		short tileEls[] = tile.getElements();
		
		/* Left side */
		t = scenario.get(getKey((short)(x - 1), (short)y));
		if (t != null && 
				tileEls[Tile.SIDE_LEFT] != 
				t.getElements()[Tile.SIDE_RIGHT])
			return false;
	
		/* Right side */
		t = scenario.get(getKey((short)(x + 1), (short)y));
		if (t != null && 
				tileEls[Tile.SIDE_RIGHT] != 
				t.getElements()[Tile.SIDE_LEFT])
			return false;

		/* Bottom side */
		t = scenario.get(getKey((short)x, (short)(y - 1)));
		if (t != null && 
				tileEls[Tile.SIDE_BOTTOM] != 
				t.getElements()[Tile.SIDE_TOP])
			return false;
	
		/* Top side */
		t = scenario.get(getKey((short)x, (short)(y + 1)));
		if (t != null && 
				tileEls[Tile.SIDE_TOP] != 
				t.getElements()[Tile.SIDE_BOTTOM])
			return false;
			
		return true;
	}
	
	private boolean probe(String k, Tile tile){
		short x, y;
		x = (short) Integer.parseInt(k.split(";")[0]);
		y = (short) Integer.parseInt(k.split(";")[1]);
		
		return probe(x, y, tile);
	}
	
	/* Place a tile in the position x y of the board.
	 * The landscape elements related to that tile are updated 
	 * according to current status of the scenario. */
	public void place(int x, int y, Tile tile) {
		short sx = (short)x;
		short sy = (short)y;
		scenario.put(getKey(sx,sy), tile);
		tile.setCoordinates(sx, sy);
		updateHoles(sx, sy);
		updateLandscape(sx, sy, tile);
	}
	
	private void updateHoles(short x, short y){
		holes.remove(getKey(x, y));
		String k;
		for(short i = 0; i < Tile.SIDE_COUNT-1; i++){
			k = getNeighbourKey(x, y, i);
			if(scenario.get(k) == null && !holes.contains(k))
				holes.add(k);
		}
	}

	/* Given a tile at position x, y, return the neighbour tile that is 
	 * touching the first one at the specified side. */
	private Tile getNeighbourTile(short x, short y, short side) {
		String k = getNeighbourKey(x, y, side);
		if(k != null)
			return scenario.get(k);
		return null;
	}
	
	private String getNeighbourKey(short x, short y, short side) {
		switch (side) {
			case Tile.SIDE_TOP:
				return getKey(x,(short)(y + 1));
			case Tile.SIDE_RIGHT:
				return getKey((short)(x + 1),y);
			case Tile.SIDE_BOTTOM:
				return getKey(x,(short)(y - 1));
			case Tile.SIDE_LEFT:
				return getKey((short)(x - 1),y);
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
					neighbour.getLSElement(getInverseDirection(side)).addTile(tile, side);
				else {
					LandscapeElement ls1, ls2;
					if ((ls1 = tile.getLSElement(side)) != null) {
						ls2 = neighbour.getLSElement(getInverseDirection(side));
						ls1.merge(ls2);
					} else {
						neighbour.getLSElement(getInverseDirection(side)).addTile(tile, side);
						ls1 = tile.getLSElement(side);
						for (short j = 0; j < Tile.SIDE_COUNT; j++)
							if (tile.getElements()[j] == Tile.ELTYPE_CITY && j != side)
								tile.putLSElement(j, ls1);
					}
				}
				break;
			case Tile.ELTYPE_STREET:
				if (streetsCount != 2) {
					neighbour.getLSElement(getInverseDirection(side)).addTile(tile, side);
				} else {
					LandscapeElement ls1, ls2;
					if ((ls1 = tile.getLSElement(side)) != null) {
						ls2 = neighbour.getLSElement(getInverseDirection(side));
						ls1.merge(ls2);
					} else {
						neighbour.getLSElement(getInverseDirection(side)).addTile(tile, side);
						ls1 = tile.getLSElement(side);
						for (short j = 0; j < Tile.SIDE_COUNT; j++) {
							if (tile.getElements()[j] == Tile.ELTYPE_STREET && j != side) {
								tile.putLSElement(j, ls1);
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
				else if (tile.getLSElement(side) == null) {
					LandscapeElement nc = new City(tile, side);
					for (short j = 0; j < Tile.SIDE_COUNT; j++)
						if (tile.getElements()[j] == Tile.ELTYPE_CITY && j != side)
							tile.putLSElement(j, nc);
				}	
				break;
			case Tile.ELTYPE_STREET:
				if (streetsCount != 2)
					new Street(tile, side);
				else if (tile.getLSElement(side) == null) {
					/* If there is not a landscape for this side yet, 
					 * create it and spalmate it*/
					LandscapeElement ns = new Street(tile, side);
					for (short j = 0; j < Tile.SIDE_COUNT; j++)
						if (tile.getElements()[j] == Tile.ELTYPE_STREET && j != side)
							tile.putLSElement(j, ns);
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
			
		}
		
		LandscapeElement el;
		
		/* Check if there is any cloister on the 8 neighbour tiles of this current tile. 
		 * Add every cloister neighbour to its landscape. */
		for (short j = -1; j <= 1; j++) {
			for (short k = -1; k <= 1; k++) {
				if (j != k || j != 0) {
					neighbour = scenario.get(getKey((short)(x + j), (short)(y + k)));
					if (neighbour != null && neighbour.getElements()[Tile.SIDE_CENTER] == Tile.ELTYPE_CLOISTER){
						el = neighbour.getLSElement(Tile.SIDE_CENTER);
						el.addTile(tile, (short)-1);
						checkScores(el, false);
					}
					
				}
			}
		}		
	}
	
	/* Check if the given meeple can be placed on the Tile */
	public boolean probeMeeple(Tile tile, Meeple meeple) {
		/* Can't place a meeple on the grass */
		if (tile.getElements()[meeple.getTileSide()] == Tile.ELTYPE_GRASS || 
				meeple == null)
			return false;
		
		/* landscape should be present if element != GRASS */
		return tile.getLSElement(meeple.getTileSide()).isMeepleDeployable(meeple.getOwner());
	}
	
	public void placeMeeple(Tile tile, Meeple meeple) {
		//this updates the associated landscape as well
		tile.setMeeple(meeple);
		meeple.setTile(tile);
	}
	
	// TODO: better optimization
	public static final String getKey(short x, short y) {	
		return Integer.toString((int)x) + ";" + Integer.toString((int)y);
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
	
	/* If the exists landscape exists and it is completed (or if it exists and the game is ended) 
	 * assign the current landscape score to its owner(s). */
	private static final Map<String, Boolean> checkScores(LandscapeElement ls, boolean endGame) {
		if (ls != null && (ls.isCompleted() || endGame)){	
			short score;
			
			/* Add the points of a landscape to the set.
			 * Duplicated points are not added in the set. */
			Map<String,Boolean> points = new Hashtable<String, Boolean>();
			short x, y;
			String key;
			for (Tile t : ls.getTiles()) {
				x = t.getX();
				y = t.getY();
				key = Board.getKey(x, y);
				
				points.put(key, t.hasMeeple());
			}
			
			List<Player> owners = ls.getScoreOwners();
			
			for(Player o : owners){
				score = ls.getValue();
				o.setScore((short)(o.getScore() + score));
			}
			ls.clear();
			
			return points;
		}
		return null;
	}
	
	/* Look for all the landscapes of the scenario that are still empty 
	 * at the end of the game. For those empty landscapes, calculate the score
	 * and assign it to their current owners(s). */
	public void finalCheckScores() {
		LandscapeElement ls;
		
		for (Tile t : scenario.values()) {
			for (short side = 0; side < Tile.SIDE_COUNT; side++) {
				ls = t.getLSElement(side);
				checkScores(ls, true);
			}
		}
	}
	
	private static final Tile initTile(short top, short right, short bottom, 
	                                   short left, short center, boolean pennant){
		String name = "";
		short flags[] = {top, right, bottom, left, center};
		for (int i = 0; i < Tile.SIDE_COUNT; i++) {
			switch (flags[i]) {
				case Tile.ELTYPE_CITY:
					name += "C";
					break;
				case Tile.ELTYPE_GRASS:
					name += "G";
					break;
				case Tile.ELTYPE_STREET:
					name += "S";
					break;
				case Tile.ELTYPE_CLOISTER:
					name += "M";
					break;
			}
		}
		
		if (pennant)
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

