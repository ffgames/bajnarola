package org.bajnarola.game.controller;

import java.util.List;
import java.util.Map;

import org.bajnarola.game.model.Tile;

public class ViewUpdate {
	/* The set of points of all the completed landscapes.
	 * For each point a boolean indicates whether a meeple is still present
	 * or not in the corresponding tile. */
	public Map<String, Boolean> points;
	public Tile placedTile;
	public List<String> scores;
	
	ViewUpdate(Map<String,Boolean> points, Tile placedTile, List<String> scores) {
		this.placedTile = placedTile;
		this.points = points;
		this.scores = scores;
	}
}
