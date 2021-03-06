package org.bajnarola.game.view;

import org.newdawn.slick.Font;
import org.newdawn.slick.Image;


public class InputBox {
	HitBox hitbox;
	Image image;
	String text;
	Font font;
	
	boolean selected = false;
	boolean initialized = false;
	int centerX, centerY, width, height, framecount = 0;
	
	public InputBox(int width, int height, int centerX, int centerY, 
	                String initText, Image activeImage, Font font){
		this.centerX = centerX;
		this.centerY = centerY;
		this.height = height;
		this.width = width;
		this.image = activeImage;
		this.text = initText;
		this.font = font;
		
		hitbox = new HitBox(centerX-(width/2), centerY-(height/2),
		                    centerX+(width/2), centerY+(height/2));
	}
	
	public void initialize() {
		if (!initialized) {
			text = "";
			initialized = true;
		}
	}
	
	public boolean isClicked(int x, int y){
		selected = hitbox.hits(x, y);
		
		return selected;
	}
	
	public String getText() {
		return text;
	}

	public void putChar(char c) {
		if (font.getWidth(this.text) < this.width - 40)
			this.text += c;
	}
	
	public void delChar() {
		if (this.text.length() > 0)
			this.text = this.text.substring(0, text.length() - 1);
	}
	
	public void draw(Gui guiManager){
		framecount++;
		image.draw(hitbox.ulx, hitbox.uly, width, height, Gui.defaultTextColor);
		guiManager.drawString(text + (selected && framecount < 50 ? "|" : ""), hitbox.ulx + 8, hitbox.uly + 1);
		if (framecount == 100)
			framecount = 0;
	}
}
