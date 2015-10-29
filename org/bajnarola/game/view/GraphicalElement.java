package org.bajnarola.game.view;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class GraphicalElement extends Image {
	String fname;
	String coords;
	HitBox hitbox;
	
	int globalCenterX, globalCenterY, size;
	int scaledX, scaledY, scaledSize;
	
	GameScene scene;
	
	public GraphicalElement(GameScene scene, String fname, String coordinates, int direction, int globalCenterX, int globalCenterY, int size) throws SlickException{
		super(fname);
		this.fname = fname;
		this.rotate(direction*90);
		this.size = size;
		this.scene = scene;
		hitbox = new HitBox();
		setCoordinates(coordinates, globalCenterX, globalCenterY);
	}
	
	public String getCoordinates(){
		return coords;
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
	
	public String getFileName(){
		return fname;
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
	
	public void draw(boolean small, float scaleFactor, Color color){
		if(small){
			setScaledVals(scaleFactor);
			this.draw(scaledX-scene.xOff, scaledY-scene.yOff, scaledSize, scaledSize, color);
		}
		else
			this.draw(hitbox.ulx-scene.xOff, hitbox.uly-scene.yOff, size, size, color);
		this.draw(hitbox.ulx-scene.xOff, hitbox.uly-scene.yOff, size, size, color);
	}
	
	public void draw(boolean small, float scaleFactor){
		if(small){
			setScaledVals(scaleFactor);
			this.draw(scaledX-scene.xOff, scaledY-scene.yOff, scaledSize, scaledSize);
		}
		else
			this.draw(hitbox.ulx-scene.xOff, hitbox.uly-scene.yOff, size, size);
	}
	
	public void draw(boolean small, float scaleFactor, float animScaleFactor){
		if(small)
			setScaledVals(scaleFactor*animScaleFactor);
		else
			setScaledVals(animScaleFactor);
		this.draw(scaledX-scene.xOff, scaledY-scene.yOff, scaledSize, scaledSize);
	}
	
	public boolean isInView(int offX, int offY, int viewWidth, int viewHeight){
		if(hitbox.lrx > offX && hitbox.ulx < (offX + viewWidth) && hitbox.lry > offY && hitbox.uly < (offY + viewHeight))
			return true;
		return false;
	}
}
