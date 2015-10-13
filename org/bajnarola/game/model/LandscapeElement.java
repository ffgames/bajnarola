package org.bajnarola.game.model;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

public abstract class LandscapeElement {
	boolean completed, visited;
	Map<Player, Integer> owners;
	Tile elementRoot;
	int gameScore, finalScore;
	
	public LandscapeElement(Tile elementRoot) {
		visited = completed = false;
		this.owners = new Hashtable<Player, Integer>();
		this.elementRoot = elementRoot;
		
		if (elementRoot.getMeeple() != null)
			owners.put(elementRoot.getMeeple().getOwner(), 1);
	}
	
	public abstract void merge(LandscapeElement el);
	public abstract void addTile(Tile t);
	public abstract void updateScores();
	
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
	
}
