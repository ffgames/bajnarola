package org.bajnarola.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public abstract class LandscapeElement {
	boolean completed, visited;
	Map<Player, Integer> owners;
	List<Tile> tiles;
	Tile elementRoot;
	short value;
	
	public LandscapeElement(Tile elementRoot, short tileSide) {
		visited = completed = false;
		this.owners = new Hashtable<Player, Integer>();
		this.elementRoot = elementRoot;
		this.value = 0;
		this.tiles = new ArrayList<Tile>();
		
		/* Link this landscape to the elementRoot */
		this.elementRoot.getLandscapes().put((int)tileSide, this);
		
		addTileInList(elementRoot);
	}
	
	public abstract void merge(LandscapeElement el);
	public abstract void addTile(Tile t, short tileSide);
	
	public Map<Player, Integer> getOwners() {
		return owners;
	}
	
	public Boolean isMeepleDeployable(Player player) {
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
		
			addTileInList(tileTmp);
		}
	}
	
	protected void addTileInList(Tile t) {
		if (t.getMeeple() != null) {
			Player meepleOwner = t.getMeeple().getOwner();
			Integer meeples = owners.remove(meepleOwner);
			
			
			if (meeples != null)
				owners.put(meepleOwner, meeples + 1);
			else
				owners.put(meepleOwner, 1);
		}
		
		tiles.add(t);
	}

	
}
