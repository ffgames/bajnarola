package org.bajnarola.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public abstract class LandscapeElement {
	boolean completed, visited, scoreSet;
	Map<Player, Integer> owners;
	List<Tile> tiles;
	Tile elementRoot;
	short value;
	
	public LandscapeElement(Tile elementRoot, short tileSide) {
		visited = completed = scoreSet = false;
		this.owners = new Hashtable<Player, Integer>();
		this.elementRoot = elementRoot;
		this.value = 0;
		this.tiles = new ArrayList<Tile>();
		
		/* Link this landscape to the elementRoot */
		this.elementRoot.getLandscapes().put((int)tileSide, this);
		tiles.add(elementRoot);
	}
	
	public abstract void merge(LandscapeElement el);
	public abstract void addTile(Tile t, short tileSide);
	
	public Map<Player, Integer> getOwners() {
		return owners;
	}
	
	public boolean isMeepleDeployable(Player player) {
		if (owners.isEmpty())
			return true;
		
		int max = Collections.max(owners.values());
		
		if (owners.get(player) != null && owners.get(player) == max)
			return true;
		
		return false;
	}
	
	public short getValue() {
		return this.value;
	}
	
	public List<Tile> getTiles(){
		return this.tiles;
	}
	public void visit() {
		visited = true;
	}
	
	public void setScore(){
		scoreSet = true;
	}
	
	public boolean isScoreSet(){
		return scoreSet;
	}
	
	protected void complete() {
		completed = true;
	}

	public boolean isCompleted() {
		return completed;
	}

	public boolean isVisited() {
		return visited;
	}
	
	protected void updateValue(short delta) {
		this.value += delta;
	}
	
	/* Unlink the passed landscape from all its tiles which
	 * must link to the current landscape instead. 
	 * Add those tiles to the current landscape tiles list. */
	protected void relink(LandscapeElement el) {
		List<Tile> elTiles  = el.getTiles();
		
		Tile tileTmp;
		
		while (!elTiles.isEmpty()) {
			tileTmp = elTiles.remove(0);	

			for (int i = 0; i < Tile.SIDE_COUNT; i++) {
				if (tileTmp.getLandscapes().get(i).equals(el)) {
					tileTmp.getLandscapes().remove(i);
					tileTmp.getLandscapes().put(i, this);
				}
				
			}
			
			tiles.add(tileTmp);
		
			
		}
	}
	
	public List<Player> getScoreOwners(){
		List<Player> actOwners = new ArrayList<Player>();
		
		for (Player player : owners.keySet()) {
			if (isMeepleDeployable(player))
				actOwners.add(player);
		}
		
		return actOwners;
	}
	
	public void clear(){
		Meeple m;
		for (Tile t : tiles){
			if((m = t.getMeeple()) != null){
				t.setMeeple(null);
				m.setTile(null);
				m.getOwner().getHand().add(m);
			}
		}
	}
	
	public void addMeeple(Meeple meeple) {
		
		int value = 1;
		if (owners.get(meeple.getOwner()) != null) {
			value = owners.remove(meeple.getOwner()) + 1;
		}
		owners.put(meeple.getOwner(), value);
	}
}