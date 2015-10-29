package org.bajnarola.game.view;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class GraphicalElement extends Image {
	String fname, coords;
	HitBox hitbox;
	
	int globalCenterX, globalCenterY, size, direction = 0;
	int scaledX, scaledY, scaledSize;
	
	boolean isMultiDirection;
	Image dirImages[] = null;
	
	GameScene scene;
	
	public GraphicalElement(GameScene scene, String fname, String coordinates, int direction, int globalCenterX, int globalCenterY, int size, boolean multiDirection) throws SlickException{
		super(fname);
		dirImages = new Image[4];
		dirImages[0] = this;
		this.isMultiDirection = multiDirection;
		if(isMultiDirection){
			dirImages[1] = new Image(fname.split(GraphicalTile.GTILE_EXTENSION)[0]+"r"+GraphicalTile.GTILE_EXTENSION);
			dirImages[2] = new Image(fname.split(GraphicalTile.GTILE_EXTENSION)[0]+"d"+GraphicalTile.GTILE_EXTENSION);
			dirImages[3] = new Image(fname.split(GraphicalTile.GTILE_EXTENSION)[0]+"l"+GraphicalTile.GTILE_EXTENSION);
			this.direction = direction;
		}
		this.fname = fname;
		this.size = size;
		this.scene = scene;
		hitbox = new HitBox();
		setCoordinates(coordinates, globalCenterX, globalCenterY);	
	}
	
	public String getCoordinates(){
		return coords;
	}
	
	public void rotate(boolean clockwise){
		direction = (direction + (clockwise ? 1 : 3)) % 4;
	}
	
	public void displace(int globalOffsetX, int globalOffsetY){
		globalCenterX += globalOffsetX;
		globalCenterY += globalOffsetY;
	}
	
	public void setCoordinates(String coordinates, int globalCenterX, int globalCenterY){
		this.coords = coordinates;
		this.globalCenterX = globalCenterX;
		this.globalCenterY = globalCenterY;
		hitbox.reset(globalCenterX-(size/2), globalCenterY-(size/2), globalCenterX+(size/2), globalCenterY+(size/2));
	}
	
	public GraphicalElement copy(){
		GraphicalElement newGE = null;
		try {
			newGE = new GraphicalElement(scene, fname, coords, direction, globalCenterX, globalCenterY, size, isMultiDirection);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		return newGE;
	}
	
	public GraphicalElement copy(String coordinates, int globalCenterX, int globalCenterY, int size){
		GraphicalElement newGE = null;
		try {
			newGE = new GraphicalElement(scene, fname, coordinates, direction, globalCenterX, globalCenterY, size, isMultiDirection);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		return newGE;
	}
	
	public void setCoordinates(HitBox newHb){
		hitbox = newHb;
		globalCenterX = hitbox.getCenterX();
		globalCenterY = hitbox.getCenterY();
	}
	
	public String getFileName(){
		return fname;
	}
	
	@Override
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
		if(isMultiDirection){
			dirImages[1].setAlpha(alpha);
			dirImages[2].setAlpha(alpha);
			dirImages[3].setAlpha(alpha);
		}
	}
	
	public boolean isClicked(int x, int y, int viewOffX, int viewOffY){
		return hitbox.hits(x, y, viewOffX, viewOffY);
	}
	
	//TODO: fix all this
	private void setScaledVals(float smallScaleFactor){
		scaledX = globalCenterX - (int)((size * smallScaleFactor) / 2);
		scaledY = globalCenterY - (int)((size * smallScaleFactor) / 2);
		scaledSize = (int)(size * smallScaleFactor);
	}
		
	public void drawAbsolute(){
		dirImages[direction].draw(hitbox.ulx, hitbox.uly, size, size);
	}
	
	public void draw(boolean small, float scaleFactor, Color color){
		if(small){
			setScaledVals(scaleFactor);
			dirImages[direction].draw(scaledX-scene.xOff, scaledY-scene.yOff, scaledSize, scaledSize, color);
		}
		else{
			dirImages[direction].draw(hitbox.ulx-scene.xOff, hitbox.uly-scene.yOff, size, size, color);
		}
	}
	
	public void draw(boolean small, float scaleFactor){
		if(small){
			setScaledVals(scaleFactor);
			dirImages[direction].draw(scaledX-scene.xOff, scaledY-scene.yOff, scaledSize, scaledSize);
		}
		else{
			dirImages[direction].draw(hitbox.ulx-scene.xOff, hitbox.uly-scene.yOff, size, size);
		}
	}
	
	public void draw(boolean small, float scaleFactor, float animScaleFactor){
		if(small)
			setScaledVals(scaleFactor*animScaleFactor);
		else
			setScaledVals(animScaleFactor);
		dirImages[direction].draw(scaledX-scene.xOff, scaledY-scene.yOff, scaledSize, scaledSize);
	}
	
	public boolean isInView(int offX, int offY, int viewWidth, int viewHeight){
		if(hitbox.lrx > offX && hitbox.ulx < (offX + viewWidth) && hitbox.lry > offY && hitbox.uly < (offY + viewHeight))
			return true;
		return false;
	}
}
