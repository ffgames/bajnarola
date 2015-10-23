package org.bajnarola.game.view;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class GraphicalTile extends Image {
	static final String GTILE_EXTENSION = ".jpg";
	static final String GTILE_PATH = "res/tiles/";
	
	String fname;
	String coords;
	
	public GraphicalTile(String name, String coordinates, short direction) throws SlickException{
		super(GTILE_PATH + name + GTILE_EXTENSION);
		this.coords = coordinates;
		this.fname = GTILE_PATH + name + GTILE_EXTENSION;
		this.rotate(direction*90);
	}
	
	public String getCoordinates(){
		return coords;
	}
	
	public String getFileName(){
		return fname;
	}
}
