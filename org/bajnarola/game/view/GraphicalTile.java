package org.bajnarola.game.view;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class GraphicalTile extends Image {
	static final String GTILE_EXTENSION = ".jpg";
	static final String GTILE_PATH = "res/tiles/";
	public static final int TILE_SIZE = 512;
	
	String fname;
	String coords;
	HitBox hitbox;
	
	int globalCenterX, globalCenterY, width, height;
	int scaledX, scaledY, scaledWidth, scaledHeight;
	
	public GraphicalTile(String name, String coordinates, short direction, int globalCenterX, int globalCenterY, int width, int height, float smallScaleFactor) throws SlickException{
		super(GTILE_PATH + name + GTILE_EXTENSION);
		this.coords = coordinates;
		this.fname = GTILE_PATH + name + GTILE_EXTENSION;
		this.rotate(direction*90);
		this.globalCenterX = globalCenterX;
		this.globalCenterY = globalCenterY;
		hitbox = new HitBox(globalCenterX-(width/2), globalCenterY-(height/2), globalCenterX+(width/2), globalCenterY+(height/2));
		scaledX = globalCenterX - (int)((width * smallScaleFactor) / 2);
		scaledY = globalCenterY - (int)((height * smallScaleFactor) / 2);
		scaledWidth = (int)(width * smallScaleFactor);
		scaledHeight = (int)(height * smallScaleFactor);
	}
	
	public String getCoordinates(){
		return coords;
	}
	
	public String getFileName(){
		return fname;
	}
	
	public boolean isClicked(int x, int y, int viewOffX, int viewOffY){
		return hitbox.hits(x, y, viewOffX, viewOffY);
	}
	
	public void draw(boolean small){
		if(small)
			this.draw(scaledX, scaledY, scaledWidth, scaledHeight);
		else
			this.draw(hitbox.ulx, hitbox.uly, width, height);
	}
	
	public boolean isInView(int offX, int offY, int viewWidth, int viewHeight){
		if(hitbox.lrx > offX && hitbox.ulx < (offX + viewWidth) && hitbox.lry > offY && hitbox.uly < (offY + viewHeight))
			return true;
		return false;
	}
}
