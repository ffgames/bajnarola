package org.bajnarola.game.view;

import org.newdawn.slick.Image;


public class InputBox {
	HitBox hitbox;
	Image activeImage;
	String text;
	int centerX, centerY, width, height;
	boolean active;
	
	public InputBox(int width, int height, int centerX, 
	                int centerY, Image inactiveImage, Image activeImage){
		this.centerX = centerX;
		this.centerY = centerY;
		this.height = height;
		this.width = width;
		this.activeImage = activeImage;
		hitbox = new HitBox(centerX-(width/2), centerY-(height/2), centerX+(width/2), centerY+(height/2));
		active = false;
	}
	
	public boolean isClicked(int x, int y){
		return active = hitbox.hits(x, y);
	}
	
	public void draw(){
		if(active){
			activeImage.draw(hitbox.ulx, hitbox.uly, width, height);
		} 
	}
}
