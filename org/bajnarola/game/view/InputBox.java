package org.bajnarola.game.view;

import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;


public class InputBox {
	HitBox hitbox;
	Image image;
	String text;
	Font font;
	
	boolean selected = false;
	boolean initialized = false;
	int centerX, centerY, width, height;
	
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
		
		boolean hitted = hitbox.hits(x, y);
		
		if (hitted) 
			initialize();
		
		return hitted;
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
	
	public void draw(Graphics g){
		image.draw(hitbox.ulx, hitbox.uly, width, height);
		g.drawString(text, hitbox.ulx + 4, hitbox.uly + 1);
	}
}
