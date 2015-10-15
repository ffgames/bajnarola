package org.bajnarola.game.view;

public class GraphicalTile {
	String fname;
	
	static final String GTILE_EXTENSION = ".jpg";
	
	public GraphicalTile(String name){
		this.fname = name + GTILE_EXTENSION;
	}
}
