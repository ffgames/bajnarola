package org.bajnarola.game.view;

import org.newdawn.slick.Image;

public class Button {
	HitBox hitbox;
	Image inactiveImage, activeImage, disabledImage;
	int centerX, centerY, width, height;
	boolean active, disabled;
	
	public Button(int width, int height, int centerX, int centerY,Image inactiveImage, Image activeImage){
		this.centerX = centerX;
		this.centerY = centerY;
		this.height = height;
		this.width = width;
		this.inactiveImage = inactiveImage;
		this.activeImage = activeImage;
		this.disabledImage = null;
		hitbox = new HitBox(centerX-(width/2), centerY-(height/2), centerX+(width/2), centerY+(height/2));
		active = disabled = false;
	}
	
	public Button(int width, int height, int centerX, int centerY,
	              Image inactiveImage,Image activeImage, Image disabledImage){
	
		this(width, height, centerX, centerY, inactiveImage, activeImage);
		this.disabledImage = disabledImage;
	}
	
	public void disable() {
		this.disabled = true;
	}
	
	public void enable() {
		this.disabled = false;
	}
	
	public void deactivate() {
		this.active = false;
	}
	
	public void activate() {
		this.active = true;
	}
	
	public boolean isClicked(int x, int y){
		if (!disabled)
			return active = hitbox.hits(x, y);
		else 
			return active = false;
	}
	
	public void draw(){
		if(active){
			activeImage.draw(hitbox.ulx, hitbox.uly, width, height);
		} else if (disabled && disabledImage != null) {
			disabledImage.draw(hitbox.ulx, hitbox.uly, width, height);
		} else {
			inactiveImage.draw(hitbox.ulx, hitbox.uly, width, height);
		}
	}
}
