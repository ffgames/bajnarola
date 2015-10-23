package org.bajnarola.game.view;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class GraphicalMeeple extends Image {
	static final String GMEEPLE_EXTENSION = ".jpg";
	static final String GMEEPLE_PATH = "res/meeples/";
	
	String coords;
	String fname;
	
	public GraphicalMeeple(int playerNum, String coordinates) throws SlickException{
		super(GMEEPLE_PATH + playerNum + GMEEPLE_EXTENSION);
		this.coords = coordinates;
		this.fname = GMEEPLE_PATH + playerNum + GMEEPLE_EXTENSION;
	}
	
	public String getCoordinates(){
		return coords;
	}
	
	public String getFileName(){
		return fname;
	}
}
