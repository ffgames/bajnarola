package org.bajnarola.game.view;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;

public class Button {
	public static final Color inactiveColor = new Color(0xBFA360);
	public static final Color disabledColor = new Color(0x5E5E5E);
	public static final Color activeColor = new Color(0xD4C14E);
	
	HitBox hitbox;
	Image inactiveImage, activeImage, disabledImage;
	int centerX, centerY, width, height;
	boolean active, disabled, primary;
	String text, secText;
	Font font;
	int textX, textY, secTextX, secTextY;
	
	public Button(int width, int height, int centerX, int centerY, String text, Font font, Image inactiveImage, Image activeImage, Image disabledImage){
		this.centerX = centerX;
		this.centerY = centerY;
		this.height = height;
		this.width = width;
		this.inactiveImage = inactiveImage;
		this.activeImage = activeImage;
		this.disabledImage = disabledImage;
		this.text = text;
		this.font = font;
		hitbox = new HitBox(centerX-(width/2), centerY-(height/2), centerX+(width/2), centerY+(height/2));
		active = disabled = false;
		primary = true;
		
		if(text != null && !text.isEmpty() && font != null){
			textY = centerY - (font.getHeight(text)/2);
			textX = centerX - (font.getWidth(text)/2);
		}
	}
	
	public Button(int width, int height, int centerX, int centerY,
	              Image inactiveImage,Image activeImage){
	
		this(width, height, centerX, centerY, null, null, inactiveImage, activeImage, null);
	}
	
	public Button(int width, int height, int centerX, int centerY,
            Image inactiveImage,Image activeImage, Image disabledImage){

		this(width, height, centerX, centerY, null, null, inactiveImage, activeImage, disabledImage);
	}
	
	public Button(int width, int height, int centerX, int centerY, String text, Font font){
		this(width, height, centerX, centerY, text, font, Gui.buttonInactiveBg, Gui.buttonActiveBg, Gui.buttonDisabledBg);
	}
	
	public Button(int width, int height, int centerX, int centerY, String text){
		this(width, height, centerX, centerY, text, Gui.buttonFont, Gui.buttonInactiveBg, Gui.buttonActiveBg, Gui.buttonDisabledBg);
	}
	
	public void setSecText(String text){
		this.secText = text;
		if(secText != null && !secText.isEmpty() && font != null){
			secTextY = centerY - (font.getHeight(secText)/2);
			secTextX = centerX - (font.getWidth(secText)/2);
		}
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
		int sx, sy;
		String str;
		Image bg;
		Color color;
		if(primary){
			sx = textX;
			sy = textY;
			str = text;
		} else {
			sx = secTextX;
			sy = secTextY;
			str = secText;
		}
		if(active){
			bg = activeImage;
			color = activeColor;
		} else if (disabled && disabledImage != null) {
			bg = disabledImage;
			color = disabledColor;
		} else {
			bg = inactiveImage;
			color = inactiveColor;
		}
		bg.draw(hitbox.ulx, hitbox.uly, width, height);
		if(str != null && !str.isEmpty() && font != null)
			font.drawString(sx, sy, str, color);
	}
}
