package org.bajnarola.game.view;

import org.newdawn.slick.Image;

public class Button {
	HitBox hitbox;
	Image inactiveImage, activeImage, disabledImage;
	Image inactiveImage2, activeImage2, disabledImage2;
	int centerX, centerY, width, height;
	boolean active, disabled, primary;
	
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
		primary = true;
	}
	
	public Button(int width, int height, int centerX, int centerY,
	              Image inactiveImage,Image activeImage, Image disabledImage){
	
		this(width, height, centerX, centerY, inactiveImage, activeImage);
		this.disabledImage = disabledImage;
	}
	
	public void setSecImages(Image inactiveImage, Image activeImage, Image disabledImage){
		inactiveImage2 = inactiveImage;
		activeImage2 = activeImage;
		disabledImage2 = disabledImage;
	}
	
	public void setPrimary(boolean primary){
		this.primary = primary;
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
	
	public boolean isEnabled(){
		return !disabled;
	}
	
	public boolean isClicked(int x, int y){
		if (!disabled)
			return active = hitbox.hits(x, y);
		else 
			return active = false;
	}
	
	public boolean hits(int x, int y){
		return hitbox.hits(x, y);
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void draw(){
		if(active){
			if(primary)
				activeImage.draw(hitbox.ulx, hitbox.uly, width, height);
			else
				activeImage2.draw(hitbox.ulx, hitbox.uly, width, height);
		} else if (disabled && disabledImage != null) {
			if(primary)
				disabledImage.draw(hitbox.ulx, hitbox.uly, width, height);
			else
				disabledImage2.draw(hitbox.ulx, hitbox.uly, width, height);
		} else {
			if(primary)
				inactiveImage.draw(hitbox.ulx, hitbox.uly, width, height);
			else
				inactiveImage2.draw(hitbox.ulx, hitbox.uly, width, height);
		}
	}
}
